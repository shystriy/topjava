package ru.javawebinar.topjava.repository.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.user.ProfileRestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(1, meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
        }
        if (!repository.containsKey(userId)) {
            repository.put(userId, new HashMap<>());
        }

        repository.get(userId).put(meal.getId(), meal);
        return meal;
    }

    @Override
    public void delete(int userId, int id) {
        repository.get(userId).remove(id);
    }

    @Override
    public Meal get(int userId, int id) {
        return repository.get(userId).get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        List <Meal> result = new ArrayList<>(repository.get(userId).values());
        result.sort(Comparator.comparing(Meal::getDateTime));
        return result;
    }
}

