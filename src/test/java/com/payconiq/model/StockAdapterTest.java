package com.payconiq.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class StockAdapterTest {

    @Autowired
    private InMemoryDataAccess stockAdapter;

    private ArrayList<StockDataModel> stocks;

    @BeforeEach
    public void setUp( ) {

        stocks = new ArrayList<>();

        Timestamp timestamp = Timestamp.valueOf("2021-08-12 12:00:00");
        stocks.add(new StockDataModel(1,"stock_1", 10.10, timestamp));
        stocks.add(new StockDataModel(2,"stock_2", 20.20, timestamp));

        stockAdapter.addAll(stocks); // test initialize method implicitly

    }

    public void tearDown(  ) {
    }

    @Test
    void read() {
        Optional<StockDataModel> optStock = stockAdapter.read(2);
        StockDataModel stock = optStock.get(); // let throw exception if null
        assertEquals(stock.getId(), 2, "stock id should be 2");
    }

    @Test
    void readAll() {
        List<StockDataModel> stocks = stockAdapter.readAll();
        assertTrue(stocks.size() > 0, "list of stocks cannot be empty");
    }

    @Test
    void create() {
        StockDataModel stock = new StockDataModel();
        stock.setName("wonder");
        stock.setCurrentPrice(10.50);
        long now = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(now);
        stock.setLastUpdate(timestamp);
        int size = stockAdapter.size(); // test method size implicitly
        int id = stockAdapter.add(stock);
        assertEquals(size + 1, id, "id should be 1");
    }

    @Test
    void update() {
        Optional<StockDataModel> optStock = stockAdapter.read(2);
        StockDataModel stock = optStock.get(); // let throw exception if null
        stock.setName("stock_2_changed");
        stockAdapter.update(stock);
        optStock = stockAdapter.read(2);
        stock = optStock.get();
        assertEquals(stock.getName(), "stock_2_changed", "name should be changed");
    }

    @Test
    void delete() {
        Optional<StockDataModel> optStock = stockAdapter.read(2);
        StockDataModel stock = optStock.get(); // let throw exception if null
        stockAdapter.delete(2);
        assertEquals(stockAdapter.size(), 1, "size should be 1");
    }
}