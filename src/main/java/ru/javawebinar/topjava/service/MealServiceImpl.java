package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.List;

@Service
public class MealServiceImpl implements MealService {

    @Autowired
    private MealRepository repository;

    @Override
    public Meal create(Integer userId, Meal meal) {
        return repository.save(userId, meal);
    }

    @Override
    public void delete(Integer userId, Integer mealId) {
        repository.delete(userId, mealId);
    }

    @Override
    public void edit(Integer userId, Meal meal) {
        delete(userId, meal.getId());
        repository.save(userId, meal);
    }

    @Override
    public Meal get(Integer userId, Integer mealId) {
        return repository.get(userId, mealId);
    }

    @Override
    public List<Meal> getAll(Integer userId) {
        return repository.getAll(userId);
    }
}