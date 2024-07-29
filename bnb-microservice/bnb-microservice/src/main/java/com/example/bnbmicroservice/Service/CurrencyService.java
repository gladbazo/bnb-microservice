package com.example.bnbmicroservice.Service;

import com.example.bnbmicroservice.Entity.Currency;
import com.example.bnbmicroservice.Entity.CurrencyList;
import com.example.bnbmicroservice.Repository.CurrencyRedisRepository;
import com.example.bnbmicroservice.Repository.CurrencyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class CurrencyService {

    private static final String bnbUrl = "https://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/index.htm?download=xml&search=&lang=BG";
    private static final String bnbUrlEn = "https://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/index.htm?download=xml&search=&lang=EN";
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private final CurrencyRepository currencyRepository;
    private final CurrencyRedisRepository currencyRedisRepository;
    private final CurrencyReceiverService currencyReceiverService;
    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final SimpMessagingTemplate template;
    private final RedisTemplate<String, Currency> redisTemplate;


    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository, CurrencyRedisRepository currencyRedisRepository, CurrencyReceiverService currencyReceiverService, RestTemplate restTemplate, JdbcTemplate jdbcTemplate, SimpMessagingTemplate template, RedisTemplate<String, Currency> redisTemplate) {
        this.currencyRepository = currencyRepository;
        this.currencyRedisRepository = currencyRedisRepository;
        this.currencyReceiverService = currencyReceiverService;
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.template = template;
        this.redisTemplate = redisTemplate;
    }

    public CurrencyList downloadCurrencies() throws JAXBException, IOException {
        String xmlDataBg = restTemplate.getForObject(bnbUrl, String.class);
        String xmlDataEn = restTemplate.getForObject(bnbUrlEn, String.class);

        CurrencyList currencyListBg = unmarshalCurrencyList(xmlDataBg);
        CurrencyList currencyListEn = unmarshalCurrencyList(xmlDataEn);

        updateCurrencies(currencyListBg, currencyListEn);

        transferRecordsToRedis();
        sendExchangeRates();
        return currencyListBg;
    }

    private CurrencyList unmarshalCurrencyList(String xmlData) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(CurrencyList.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        try (ByteArrayInputStream input = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8))) {
            return (CurrencyList) jaxbUnmarshaller.unmarshal(input);
        }
    }

    private void updateCurrencies(CurrencyList currencyListBg, CurrencyList currencyListEn) {
        for (int i = 0; i < currencyListBg.getCurrencies().size(); i++) {
            Currency currencyBg = currencyListBg.getCurrencies().get(i);
            Currency currencyEn = currencyListEn.getCurrencies().get(i);
            currencyBg.setName_en(currencyEn.getName());

            if (currencyBg.getGold() != null && currencyBg.getGold() != 0) {
                Integer fStar = (currencyBg.getF_star() != null) ? currencyBg.getF_star() : 0;

                try {
                    insertCurrency(currencyBg, currencyEn, fStar);
                } catch (DataAccessException e) {
                    updateCurrency(currencyBg, currencyEn, fStar);
                }
            }
        }
    }

    private void insertCurrency(Currency currencyBg, Currency currencyEn, Integer fStar) {
        String sql = "INSERT INTO dbo.currencies (gold, name, name_en, code, ratio, reverse_rate, rate, curr_date, f_star) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, currencyBg.getGold(), currencyBg.getName(), currencyEn.getName(), currencyBg.getCode(),
                currencyBg.getRatio(), currencyBg.getReverse_rate(), currencyBg.getRate(), currencyBg.getCurr_date(), fStar);
    }

    private void updateCurrency(Currency currencyBg, Currency currencyEn, Integer fStar) {
        String sql = "UPDATE dbo.currencies SET gold = ?, name = ?, name_en = ?, ratio = ?, reverse_rate = ?, rate = ?, curr_date = ?, f_star = ? WHERE code = ?";
        jdbcTemplate.update(sql, currencyBg.getGold(), currencyBg.getName(), currencyEn.getName(), currencyBg.getRatio(),
                currencyBg.getReverse_rate(), currencyBg.getRate(), currencyBg.getCurr_date(), fStar, currencyBg.getCode());
    }


    public void sendExchangeRates() throws JsonProcessingException {
        List<Currency> currencies = jdbcTemplate.query("SELECT * FROM dbo.currencies", new BeanPropertyRowMapper<>(Currency.class));
        for (Currency currency : currencies) {
            logger.info("Currency: " + currency.toString());
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String jsonCurrencies = mapper.writeValueAsString(currencies);
//        this.template.convertAndSend("/topic/exchangeRates", jsonCurrencies);
        this.template.convertAndSend("/app/currenciesJson", jsonCurrencies);
        handleJsonCurrencies(jsonCurrencies);
        logger.info("Sent exchange rates to WebSocket clients.");
    }



    @MessageMapping("/currenciesJson")
    public void handleJsonCurrencies(String jsonCurrencies) throws JsonProcessingException {
        logger.info("Received message: " + jsonCurrencies);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Currency> currencies = mapper.readValue(jsonCurrencies, new TypeReference<>() {
        });

        for (Currency currency : currencies) {
            if (currency.getGold() != null && currency.getGold() != 0) {
                try {
                    String sql = "INSERT INTO dbo.currenciesJson (gold, name, name_en, code, ratio, reverse_rate, rate, curr_date, f_star) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(sql, currency.getGold(), currency.getName(), currency.getName_en(), currency.getCode(), currency.getRatio(), currency.getReverse_rate(), currency.getRate(), currency.getCurr_date(), currency.getF_star());
                    logger.info("Inserted currency: " + currency.getCode());
                } catch (DataAccessException e) {
                    String sql = "UPDATE dbo.currenciesJson SET gold = ?, name = ?, name_en = ?, ratio = ?, reverse_rate = ?, rate = ?, curr_date = ?, f_star = ? WHERE code = ?";
                    jdbcTemplate.update(sql, currency.getGold(), currency.getName(), currency.getName_en(), currency.getRatio(), currency.getReverse_rate(), currency.getRate(), currency.getCurr_date(), currency.getF_star(), currency.getCode());
                    logger.info("Updated currency: " + currency.getCode());
                }
            }
        }
    }



    public void transferRecordsToRedis() {
        List<Currency> currencies = jdbcTemplate.query("SELECT * FROM dbo.currencies", new BeanPropertyRowMapper<>(Currency.class));
        HashOperations<String, String, Currency> hashOps = redisTemplate.opsForHash();
        for (Currency currency : currencies) {
            logger.info("Transferring currency record to Redis: {}", currency);

            if (currency.getCode() != null) {
                if (currency.getGold() == null) {
                    currency.setGold(0L);
                }
                if (currency.getName() == null) {
                    currency.setName("");
                }
                if (currency.getCurr_date() == null) {
                    currency.setCurr_date(LocalDate.now());
                }
                if (currency.getRatio() == null) {
                    currency.setRatio(BigDecimal.ZERO);
                }
                if (currency.getReverse_rate() == null) {
                    currency.setReverse_rate(BigDecimal.ZERO);
                }
                if (currency.getRate() == null) {
                    currency.setRate(BigDecimal.ZERO);
                }
                hashOps.put("Currencies", currency.getCode(), currency);
            }
            }
        }
    }





