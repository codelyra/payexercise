package com.payconiq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payconiq.model.StockDataModel;
import com.payconiq.service.ClientService;
import com.payconiq.service.NonBlockingClient;
import com.payconiq.service.ThrottledClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.payconiq.model.InMemoryDataAccess;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StocksAPIController {

    // reading configuration values
    @Value("${use.case}")
    private String useCase;

    // inject services implementing business logic
    @Autowired
    private ThrottledClient throttledClient;

    @Autowired
    private NonBlockingClient nonBlockingClient;

    @Autowired
    private InMemoryDataAccess inMemoryDataAccess;

    // implements client API

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/stocks")
    public List<StockDataModel> getStocks() {

        return inMemoryDataAccess.readAll();

    }

    @GetMapping(value = "/stock/{id}")
    public StockDataModel getStock(@PathVariable(value="id") int id) {

        return inMemoryDataAccess.read(id).get();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/stocks", consumes = "application/json", produces = "application/json")
    public int postStock(@RequestBody String stockJson) {

        int id = -1;
        try {
            ObjectMapper mapper = new ObjectMapper();
            StockDataModel stock = mapper.readValue(stockJson, StockDataModel.class);
            id = inMemoryDataAccess.add(stock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping(value = "/stocks/{id}", consumes = "application/json", produces = "application/json")
    public void putStock(@PathVariable(value="id") int id, @RequestBody String stockJson) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            StockDataModel stock = mapper.readValue(stockJson, StockDataModel.class);
            stock.setId(id);
            inMemoryDataAccess.update(stock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping(value = "/stocks/{id}")
    public void putStock(@PathVariable(value="id") int id) {

        try {
            inMemoryDataAccess.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
