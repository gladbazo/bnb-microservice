package com.example.bnbmicroservice.Service;

import com.example.bnbmicroservice.BnbMicroserviceApplication;
import com.example.bnbmicroservice.Entity.Currency;
import com.example.bnbmicroservice.Entity.CurrencyList;
import com.example.bnbmicroservice.Init.Initialization;
import com.example.bnbmicroservice.Repository.CurrencyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.JAXBException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MSSQLServerContainer;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BnbMicroserviceApplication.class)
@ActiveProfiles("test")
@ContextConfiguration(initializers = {CurrencyServiceIntegrationTest.Initializer.class})
public class CurrencyServiceIntegrationTest {

    @ClassRule
    public static MSSQLServerContainer<?> mssqlserverContainer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2017-CU12");
//            .withUsername("sa")
//            .withPassword("sa");

    @Autowired
    private CurrencyService currencyService;

    private TestSessionHandler sessionHandler = new TestSessionHandler();
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private Initialization initialization;
    @BeforeEach
    public void setup() {
        initialization.insertDataIntoDatabase();
    }

    @Test
    @Transactional
    public void testDownloadCurrencies() throws JAXBException, IOException {

        CurrencyList data = currencyService.downloadCurrencies();


        assertNotNull(data);
        assertFalse(data.getCurrencies().isEmpty());


        List<Currency> currencies = data.getCurrencies();
        assertEquals(30, currencies.size());


        Currency aud = currencies.get(0);
        assertEquals(1, aud.getGold());
        assertEquals("Австралийски долар", aud.getName());
        assertEquals("Australian Dollar", aud.getName_en());
        assertEquals("AUD", aud.getCode());
        assertEquals(1.0000, aud.getRatio(), String.valueOf(0.0001));
        assertEquals(0.8378, aud.getReverse_rate(), String.valueOf(0.0001));
        assertEquals(1.1936, aud.getRate(), String.valueOf(0.0001));
        assertEquals(LocalDate.of(2024, 5, 2), aud.getCurr_date());
        assertEquals(0, aud.getF_star());


    }


    @Test
    @Transactional
    public void testSendExchangeRates() throws Exception {

        String expectedJsonCurrencies = "[{\"gold\":1,\"name\":\"Австралийски долар\",\"code\":\"AUD\",\"ratio\":1,\"reverse_rate\":0.847056,\"rate\":1.18056,\"curr_date\":\"18.04.2024\",\"f_star\":0}]";


        currencyService.sendExchangeRates();

        Thread.sleep(5000);


        assertEquals(expectedJsonCurrencies, sessionHandler.getPayload());
    }

    @Test
    @Transactional
    public void testHandleJsonCurrencies() throws JsonProcessingException {

        initialization.insertDataIntoDatabase();
        String jsonCurrencies = "[{\"gold\":1,\"name\":\"Австралийски долар\",\"name_en\":\"Australian Dollar\",\"code\":\"AUD\",\"ratio\":1,\"reverse_rate\":0.8378,\"rate\":1.1936,\"curr_date\":\"02.05.2024\",\"f_star\":0}," +
                "{\"gold\":1,\"name\":\"Бразилски реал\",\"name_en\":\"Brazilian Real\",\"code\":\"BRL\",\"ratio\":10,\"reverse_rate\":2.8134,\"rate\":3.5544,\"curr_date\":\"02.05.2024\",\"f_star\":0}]";


        currencyService.handleJsonCurrencies(jsonCurrencies);


        List<Currency> currencies = currencyRepository.findAll();


        assertEquals(2, currencies.size());


        Currency aud = currencies.get(0);
        assertEquals(1, aud.getGold());
        assertEquals("Австралийски долар", aud.getName());
        assertEquals("Australian Dollar", aud.getName_en());
        assertEquals("AUD", aud.getCode());
        assertEquals(1.0000, aud.getRatio(), String.valueOf(0.0001));
        assertEquals(0.8378, aud.getReverse_rate(), String.valueOf(0.0001));
        assertEquals(1.1936, aud.getRate(), String.valueOf(0.0001));
        assertEquals(LocalDate.of(2024, 5, 2), aud.getCurr_date());
        assertEquals(0, aud.getF_star());


        Currency brl = currencies.get(1);
        assertEquals(1, brl.getGold());
        assertEquals("Бразилски реал", brl.getName());
        assertEquals("Brazilian Real", brl.getName_en());
        assertEquals("BRL", brl.getCode());
        assertEquals(10.0000, brl.getRatio(), String.valueOf(0.0001));
        assertEquals(2.8134, brl.getReverse_rate(), String.valueOf(0.0001));
        assertEquals(3.5544, brl.getRate(), String.valueOf(0.0001));
        assertEquals(LocalDate.of(2024, 5, 2), brl.getCurr_date());
        assertEquals(0, brl.getF_star());
    }


    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + mssqlserverContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mssqlserverContainer.getUsername(),
                    "spring.datasource.password=" + mssqlserverContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
            System.out.println("Username: " + mssqlserverContainer.getUsername() + "\nPassword: "
                    + mssqlserverContainer.getPassword());
        }
    }
}
    class TestSessionHandler extends StompSessionHandlerAdapter {
        private String payload;

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("Connected to WebSocket server");
            session.subscribe("/currenciesJson", this);
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.payload = (String) payload;
        }

        public String getPayload() {
            return payload;
        }
    }



