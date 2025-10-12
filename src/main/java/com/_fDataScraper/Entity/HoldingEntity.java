package com._fDataScraper.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "HOLDINGS")
@Setter
@Getter
public class HoldingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle의 IDENTITY 컬럼과 매핑
    @Column(name = "HOLDING_ID")
    private Long holdingId;

    @Column(name = "TICKER", length = 20)
    private String ticker;

    @Column(name = "NAME_OF_ISSUER", length = 255)
    private String nameOfIssuer;

    @Column(name = "SHARES")
    private Long shares;

    @Column(name = "VALUE")
    private Long value;

    public HoldingEntity() {}
}
