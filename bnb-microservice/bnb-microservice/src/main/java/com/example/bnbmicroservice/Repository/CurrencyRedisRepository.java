package com.example.bnbmicroservice.Repository;

import com.example.bnbmicroservice.Entity.CurrencyRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRedisRepository extends CrudRepository<CurrencyRedis, String> {
}
