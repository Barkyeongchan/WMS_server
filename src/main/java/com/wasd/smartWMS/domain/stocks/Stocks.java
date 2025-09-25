package com.wasd.smartWMS.domain.stocks;

import com.wasd.smartWMS.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Stocks")
public class Stocks extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer inventory;

    @Builder
    public Stocks(String name, Integer inventory) {
        this.name = name;
        this.inventory = inventory;
    }

    public void Update(String name, Integer inventory) {
        this.name = name;
        this.inventory = inventory;
    }
}
