package com.payconiq.model;


import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

public interface DataAccess<T> {

    // crud operations on data repository

    Optional<T> read(int id);
    Collection<T> readAll();
    int add(T el);
    void addAll(Collection<T> c);
    void update(T el);
    void delete(int id);
}

