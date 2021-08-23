package com.payconiq.model;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryDataAccess implements DataAccess<StockDataModel> {

    private ArrayList<StockDataModel> stockList = new ArrayList<>();

    @Override
    public Optional<StockDataModel> read(int id) {
        return Optional.ofNullable(stockList.get(id-1));
    }

    @Override
    public List<StockDataModel> readAll() {
        return stockList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public int add(StockDataModel stock) {
        stock.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        stockList.add(stock);
        int index = stockList.size() ;
        stock.setId(index);
        return index;
    }

    @Override
    public void addAll(Collection<StockDataModel> stocks) {
        if(stocks != null) {
            this.stockList = (ArrayList) stocks;
        }
    }

    @Override
    public void update(StockDataModel stock) {
        stock.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        stockList.set(stock.getId()-1, stock);
    }

    @Override
    public void delete(int id) {
        stockList.set(id - 1, null);
    }

    public int size() {
        return this.stockList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)).size();
    }

}
