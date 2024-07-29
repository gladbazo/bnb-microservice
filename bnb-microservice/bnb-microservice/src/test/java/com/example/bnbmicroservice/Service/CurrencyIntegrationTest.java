package com.example.bnbmicroservice.Service;

import com.example.bnbmicroservice.Entity.Currency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyIntegrationTest {

    @Autowired
    private CurrencyService currencyService;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private WebSocketStompClient stompClient;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Test
    public void testDatabaseAndWebSocketData() throws Exception {
        // Arrange
        String jsonCurrencies = "[{\"gold\":1,\"name\":\"Австралийски долар\",\"code\":\"AUD\",\"ratio\":1,\"reverse_rate\":0.847056,\"rate\":1.18056,\"curr_date\":\"18.04.2024\",\"f_star\":0}]"; // Your actual JSON data

        // Act
        // Insert data into the mock database
        currencyService.handleJsonCurrencies(jsonCurrencies);

        // Trigger the WebSocket server
        currencyService.sendExchangeRates();

        // Setup WebSocket connection
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        TestSessionHandler sessionHandler = new TestSessionHandler();
        stompClient.connect("ws://localhost:" + port + "/currenciesJson", headers, sessionHandler);

        // Wait for WebSocket connection to be established
        Thread.sleep(5000);

        // Capture the WebSocket message
        // Capture the WebSocket message
        String webSocketData = sessionHandler.getPayload();


        // Assert
        // Compare the data
        assertEquals(jsonCurrencies, webSocketData);

        // Verify
        Mockito.verify(jdbcTemplate, times(1)).update(anyString(), any());
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

}


