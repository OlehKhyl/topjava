package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ru.javawebinar.topjava.util.MealsUtil.filteredByStreams;


public class MealServlet extends HttpServlet {
    private static final List<Meal> meals = Arrays.asList(
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    private static final Map<Integer, Meal> storage = new ConcurrentHashMap<>();

    static {
        for (Meal meal : meals) {
            storage.put(meal.getId(), meal);
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(MealServlet.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getParameter("action") != null) {
            if ((req.getParameter("action")).equals("delete")) {
                logger.debug("receive delete");
                Integer id = Integer.parseInt(req.getParameter("id"));
                storage.remove(id);
                logger.debug("removed ".concat(id.toString()));
                resp.sendRedirect("meals");
            }
        } else {

            List<MealTo> mealsTo = filteredByStreams(new ArrayList<>(storage.values()), LocalTime.of(0, 0), LocalTime.of(23, 59), 2000);

            req.setAttribute("meals", mealsTo);
            logger.debug("redirect to meals");
            req.getRequestDispatcher("meals.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        logger.debug("income post from form");
        LocalDateTime date = LocalDateTime.parse(req.getParameter("date"));
        String description = req.getParameter("description");
        int calories = Integer.parseInt(req.getParameter("calories"));
        if (req.getParameter("action") != null) {
            if (req.getParameter("action").equals("edit")) {
                Integer id = Integer.parseInt(req.getParameter("id"));
                Meal mealToUpdate = new Meal(id, date, description, calories);
                logger.debug("update meal".concat(" ").concat(mealToUpdate.getId().toString()));
                storage.put(mealToUpdate.getId(), mealToUpdate);
            }
        } else {
            Meal newMeal = new Meal(date, description, calories);
            logger.debug("add new meal".concat(" ").concat(newMeal.toString()));
            storage.put(newMeal.getId(), newMeal);
        }
        logger.debug("redirect to meals");
        resp.sendRedirect("meals");
    }
}
