package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GKislin
 * 31.05.2015.
 */
public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
//        .toLocalDate();
//        .toLocalTime();
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> totalMealCaloriesPer1Day = new HashMap<>();

        List<UserMeal> filteredList = mealList.stream()
                .filter(userMeal -> {
                    Duration start = Duration.between(startTime, userMeal.getDateTime().toLocalTime());
                    Duration end = Duration.between(userMeal.getDateTime().toLocalTime(), endTime);

                    if (!start.isNegative() && !end.isNegative()) {
                        int totalCaloriesInDay = 0;
                        Integer caloriesInDay = totalMealCaloriesPer1Day.get(userMeal.getDateTime().toLocalDate());
                        if (caloriesInDay != null) {
                            totalCaloriesInDay = caloriesInDay;
                        }
                        totalMealCaloriesPer1Day.put(userMeal.getDateTime().toLocalDate(), totalCaloriesInDay+userMeal.getCalories());
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        return filteredList.stream()
                .map(userMeal -> new UserMealWithExceed(userMeal.getDateTime(),
                        userMeal.getDescription(),
                        userMeal.getCalories(),
                        totalMealCaloriesPer1Day.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
