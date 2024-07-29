package com.example.bnbmicroservice.Controller;

import com.example.bnbmicroservice.Entity.Currency;
import com.example.bnbmicroservice.Service.CurrencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

//@Controller
//public class WebSocketController {
//    private final CurrencyService currencyService;
//    private final JdbcTemplate jdbcTemplate;
//    private static final Logger logger = LoggerFactory.getLogger(Currency.class);
//
//    @Autowired
//    public WebSocketController(CurrencyService currencyService, JdbcTemplate jdbcTemplate) {
//        this.currencyService = currencyService;
//        this.jdbcTemplate = jdbcTemplate;
//    }
//}

//    @MessageMapping("/currenciesJson")
//    public void handleJsonCurrencies(String jsonCurrencies) throws JsonProcessingException {
//        logger.info("Received message: " + jsonCurrencies);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        List<Currency> currencies = mapper.readValue(jsonCurrencies, new TypeReference<List<Currency>>() {
//        });
//
//        for (Currency currency : currencies) {
//            try {
//                String sql = "INSERT INTO dbo.currenciesJson (gold, name, name_en, code, ratio, reverse_rate, rate, curr_date, f_star) " +
//                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
//                jdbcTemplate.update(sql, currency.getGold(), currency.getName(), currency.getName_en(), currency.getCode(), currency.getRatio(), currency.getReverse_rate(), currency.getRate(), currency.getCurr_date(), currency.getF_star());
//                logger.info("Inserted currency: " + currency.getCode());
//            } catch (DataAccessException e) {
//                String sql = "UPDATE dbo.currenciesJson SET gold = ?, name = ?, name_en = ?, ratio = ?, reverse_rate = ?, rate = ?, curr_date = ?, f_star = ? WHERE code = ?";
//                jdbcTemplate.update(sql, currency.getGold(), currency.getName(), currency.getName_en(), currency.getCode(), currency.getRatio(), currency.getReverse_rate(), currency.getRate(), currency.getCurr_date(), currency.getF_star());
//                logger.info("Updated currency: " + currency.getCode());
//            }
//        }
//    }
//}

