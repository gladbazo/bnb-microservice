package com.example.bnbmicroservice.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Setter
@RedisHash("CurrencyRedis")
public class CurrencyRedis {
    @Id
    private String id;
    private Long gold;
    private String name;
    private String name_en;
    private String code;
    private BigDecimal ratio;
    private BigDecimal reverse_rate;
    private BigDecimal rate;
    private LocalDate curr_date;
    private Integer f_star;

    public String getId() {
        return id;
    }

    public Long getGold() {
        return gold;
    }

    public String getName() {
        return name;
    }

    public String getName_en() {
        return name_en;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public BigDecimal getReverse_rate() {
        return reverse_rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public LocalDate getCurr_date() {
        return curr_date;
    }

    public Integer getF_star() {
        return f_star;
    }
}
