package com.payconiq.model;

import lombok.*;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDataModel implements DataModel {

    private int id;
    private String name;
    private double currentPrice;
    @Nullable
    private Timestamp lastUpdate;

}
