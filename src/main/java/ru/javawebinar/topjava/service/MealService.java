package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealService {
    Meal create();

    void delete();

    void edit();

    Meal get();

    List<Meal> getAll();
}