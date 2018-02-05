package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import java.util.List;

@Controller
public class MealRestController {

    @Autowired
    private MealService service;

    public MealService getService() {
        return service;
    }

    public Meal create(Meal meal){
        return service.create(AuthorizedUser.id(), meal);
    }

    public void delete(Integer mealId){
        service.delete(AuthorizedUser.id(), mealId);
    }

    public void edit(Meal meal){
        service.edit(AuthorizedUser.id(), meal);
    }

    public Meal get(Integer mealId){
        return service.get(AuthorizedUser.id(), mealId);
    }

    public List<Meal> getAll() {
        return service.getAll(AuthorizedUser.id());
    }

}