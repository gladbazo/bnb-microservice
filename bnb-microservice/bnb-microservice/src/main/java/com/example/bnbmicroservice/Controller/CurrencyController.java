package com.example.bnbmicroservice.Controller;

import com.example.bnbmicroservice.Service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyController {
    private final CurrencyService currencyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyController.class);

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/download-currencies")
    public ResponseEntity<String> getCurrencies() throws Exception {
            currencyService.downloadCurrencies();
            return ResponseEntity.ok("Currencies downloaded and saved successfully.");
        }
    }


