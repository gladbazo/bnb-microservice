package com.example.bnbmicroservice;

import com.example.bnbmicroservice.Entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootApplication
@EnableRedisRepositories
public class BnbMicroserviceApplication implements CommandLineRunner {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(BnbMicroserviceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		String sql = "SELECT * FROM dbo.currencies";
//		List<Currency> currencies = jdbcTemplate.query(sql,
//				BeanPropertyRowMapper.newInstance(Currency.class));
//		currencies.forEach(System.out :: println);
	}
}
