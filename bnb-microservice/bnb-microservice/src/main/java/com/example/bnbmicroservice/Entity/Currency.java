package com.example.bnbmicroservice.Entity;

import com.example.bnbmicroservice.Config.LocalDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Setter
@Table(name = "currencies")
@XmlRootElement(name = "ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class Currency implements Serializable {
        @Column(name = "gold")
        @XmlElement(name = "GOLD")
        private Long gold;
        @Column(name = "name")
        @XmlElement(name = "NAME_")
        private String name;
        @Column(name = "name_en")
        private String name_en;
        @Id
        @Column(name = "code")
        @XmlElement(name = "CODE")
        private String code;
        @Column(name = "ratio")
        @XmlElement(name = "RATIO")
        private BigDecimal ratio;
        @Column(name = "reverse_rate")
        @XmlElement(name = "REVERSERATE")
        private BigDecimal reverse_rate;
        @Column(name = "rate")
        @XmlElement(name = "RATE")
        private BigDecimal rate;
        @Column(name = "curr_date")
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @XmlJavaTypeAdapter(LocalDateAdapter.class)
        @XmlElement(name = "CURR_DATE")
        private LocalDate curr_date;
        @Column(name = "f_star")
        @XmlElement(name = "F_STAR")
        private Integer f_star;

        public Currency() {
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


        public void setName_en(String name_en) {
                this.name_en = name_en;
        }

        @Override
        public String toString() {
                return "Currency{" +
                        "gold=" + gold +
                        ", name='" + name + '\'' +
                        ", name_en='" + name_en + '\'' +
                        ", code='" + code + '\'' +
                        ", ratio=" + ratio +
                        ", reverse_rate=" + reverse_rate +
                        ", rate=" + rate +
                        ", curr_date=" + curr_date +
                        ", f_star=" + f_star +
                        '}';
        }
}

