package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;

/**
 * Created by pospekhovsm on 3/15/2018.
 */
@Controller
public class JspMealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);
    @Autowired
    private MealService mealService;

    @GetMapping("/meals")
    public String getMeals(Model model, HttpServletRequest request) {


        model.addAttribute("meals", MealsUtil.getWithExceeded(mealService.getAll(AuthorizedUser.id()), AuthorizedUser.getCaloriesPerDay()));
        return "meals";
    }

    @PostMapping("/meals")
    public String setMeal(HttpServletRequest request) {
        log.debug("Post Meals for ID: {}", request.getParameter("id"));
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (request.getParameter("id").isEmpty()) {
            mealService.create(meal, AuthorizedUser.id());
        } else {
            assureIdConsistent(meal, getId(request));
            mealService.update(meal, AuthorizedUser.id());
        }
        log.debug("END Post Meals for ID: {}", request.getParameter("id"));
        return "redirect:/meals";
    }

    @PostMapping("/meals/filter")
    public String getFilterMeals(Model model, HttpServletRequest request) {

        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));

        List<Meal> mealsDateFiltered = mealService.getBetweenDates(
                startDate != null ? startDate : DateTimeUtil.MIN_DATE,
                endDate != null ? endDate : DateTimeUtil.MAX_DATE, AuthorizedUser.id());

        model.addAttribute("meals", MealsUtil.getFilteredWithExceeded(mealsDateFiltered,
                startTime != null ? startTime : LocalTime.MIN,
                endTime != null ? endTime : LocalTime.MAX,
                AuthorizedUser.getCaloriesPerDay()
        ));
        return "meals";
    }

    @GetMapping("/meals/add")
    public String addMeal(HttpServletRequest request, HttpServletResponse response) throws Exception{
        final Meal meal = request.getParameter("id") == null ?
                new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                mealService.get(getId(request), AuthorizedUser.id());
        request.setAttribute("meal", meal);
        //request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
        //mealService.delete(getId(request), AuthorizedUser.id());
        return "mealForm";
    }

    @GetMapping("/meals/delete")
    public String deleteMeal(HttpServletRequest request) {
        mealService.delete(getId(request), AuthorizedUser.id());
        return "redirect:/meals";
    }



    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }


}
