package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.util.List;

public interface MealService {
    Meal create(Integer userId, Meal meal);

    void delete(Integer userId, Integer mealId);

    void edit(Integer userId, Meal meal);

    Meal get(Integer userId, Integer mealId);

    List<Meal> getAll(Integer userId);
}