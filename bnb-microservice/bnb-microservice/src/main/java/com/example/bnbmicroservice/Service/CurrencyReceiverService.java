package com.example.bnbmicroservice.Service;

import com.example.bnbmicroservice.Entity.Currency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CurrencyReceiverService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @JmsListener(destination = "/topic/exchangeRates")
    public void receiveMessage(String jsonCurrencies) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Currency> currenciesJson = Arrays.asList(mapper.readValue(jsonCurrencies, Currency[].class));

        for (Currency currency : currenciesJson) {
            String sql = "UPDATE dbo.currenciesJson SET gold = ?, name = ?, name_en = ?, code = ?, ratio = ?, reverse_rate = ?, rate = ?, curr_date = ?, f_star = ? WHERE code = ?";
            jdbcTemplate.update(sql, currency.getGold(), currency.getName(), currency.getName_en(), currency.getCode(), currency.getRatio(), currency.getReverse_rate(), currency.getRate(), currency.getCurr_date(), currency.getF_star(), currency.getCode());
        }
    }
}


