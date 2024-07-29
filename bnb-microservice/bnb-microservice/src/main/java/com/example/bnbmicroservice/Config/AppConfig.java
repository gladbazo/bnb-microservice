package com.example.bnbmicroservice.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketException;
import java.time.LocalDate;

@Configuration
public class AppConfig {

    private static final String PROXY_SERVER_HOST = "proxy.glb.postbank.bg";
    private static final int PROXY_SERVER_PORT = 8080;
    private static final int MAX_RETRIES = 5;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_SERVER_HOST, PROXY_SERVER_PORT));
        requestFactory.setProxy(proxy);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                    int retries = 0;
                    while (retries < MAX_RETRIES) {
                        try {
                            restTemplate.execute(response.getHeaders().getLocation(), HttpMethod.GET, null, null);
                            break;
                        } catch (ResourceAccessException e) {
                            if (e.getCause() instanceof SocketException && e.getCause().getMessage().equals("Connection reset")) {
                                retries++;
                                System.out.println("Connection reset. Retry attempt: " + retries);
                                if (retries == MAX_RETRIES) {
                                    System.out.println("Max retries reached. Exiting...");
                                    throw e;
                                }
                            } else {
                                throw e;
                            }
                        }
                    }
                } else {
                    super.handleError(response);
                }
            }
        });

        return restTemplate;
    }
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

}
