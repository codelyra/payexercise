package com.payconiq.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FileDataPersistence implements DataPersistence {

    @Autowired
    private InMemoryDataAccess stockAdapter;

    private static final Logger logger = LoggerFactory.getLogger(FileDataPersistence.class);

    @Value("${stocks.file}")
    private String stocksFile;

    @Override
    @EventListener(ContextRefreshedEvent.class)
    public void loadOnStart() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = Paths.get(stocksFile).toFile();
            if(file.isFile()) {
                List<StockDataModel> stocks = new ArrayList<>(Arrays.asList(mapper.readValue(file, StockDataModel[].class)));
                stockAdapter.addAll(stocks);
                logger.info("initializing in-memory list: " + stocks.size() + " stocks loaded from file " + stocksFile);
                stocks.forEach(System.out::println);
            }
            logger.info("file " + stocksFile + " not found. In-memory list empty");

        } catch (Exception ex) {
            logger.error("could not load file " + stocksFile + ". In-memory list empty");
            ex.printStackTrace();
        }
    }

    @Override
    @PreDestroy
    public void saveOnShutdown() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = Paths.get(stocksFile).toFile();
            mapper.writeValue(file, stockAdapter.readAll());
            logger.info("System shutdown. In-memory list persisted to file " + stocksFile);

        } catch (Exception ex) {
            logger.error("System shutdown. Could no persist in-memory list to file " + stocksFile);
            ex.printStackTrace();
        }
    }
}
