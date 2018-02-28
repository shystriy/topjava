package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

/**
 * Created by pospekhovsm on 14.02.2018.
 */

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService mealService;

    @Test
    public void get() throws Exception {
        Meal meal = mealService.get(MEAL_1_ID, USER_ID);
        assertMatch(meal, MEAL_1);
    }

    @Test(expected = NotFoundException.class)
    public void getWihErr() throws Exception {
        mealService.get(MEAL_1_ID, ADMIN_ID);
    }

    @Test
    public void delete() throws Exception {
        mealService.delete(MEAL_1_ID, USER_ID);
        assertMatch(mealService.getAll(USER_ID), MEAL_2);
    }

    @Test(expected = NotFoundException.class)
    public void deleteWithErr() throws Exception {
        mealService.delete(MEAL_1_ID, ADMIN_ID);
    }

    @Test
    public void getBetweenDates() throws Exception {
        List<Meal> list = mealService.getBetweenDates(LocalDate.of(2007,4,6), LocalDate.of(2007,4,8), USER_ID);
        assertMatch(list, MEAL_2, MEAL_1);
    }

    @Test
    public void getBetweenDateTimes() throws Exception {
        List<Meal> list = mealService.getBetweenDateTimes(LocalDateTime.of(2007,4,7,12,0), LocalDateTime.of(2007,4,7,13,0), USER_ID);
        assertMatch(list, MEAL_1);
    }

    @Test
    public void getAll() throws Exception {
        List<Meal> list = mealService.getAll(USER_ID);
        assertMatch(list, MEAL_2, MEAL_1);
    }

    @Test
    public void update() throws Exception {
        Meal updated = new Meal(MEAL_1);
        updated.setDescription("UpdatedDescr");
        updated.setCalories(330);
        mealService.update(updated, USER_ID);
        assertMatch(mealService.get(MEAL_1_ID, USER_ID), updated);
    }

    @Test(expected = NotFoundException.class)
    public void updateWithErr() throws Exception {
        Meal updated = new Meal(MEAL_1);
        updated.setDescription("UpdatedDescr1");
        updated.setCalories(331);
        mealService.update(updated, ADMIN_ID);
    }

    @Test
    public void create() throws Exception {
        Meal meal = new Meal(LocalDateTime.now(), "Sok", 100);
        Meal created = mealService.create(meal, USER_ID);
        meal.setId(created.getId());
        List<Meal> list = mealService.getAll(USER_ID);
        assertMatch(list, meal, MEAL_2, MEAL_1);
    }

}