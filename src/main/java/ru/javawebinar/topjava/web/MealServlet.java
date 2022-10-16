package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRestController controller;

    public MealServlet() {
        try (ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext("spring/spring-app.xml");){
            controller = appContext.getBean(MealRestController.class);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                SecurityUtil.authUserId(),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        if (meal.isNew()) {
            controller.create(meal, SecurityUtil.authUserId());
        } else {
            controller.update(meal, meal.getId(), SecurityUtil.authUserId());
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete id={}", id);
                controller.delete(id, SecurityUtil.authUserId());
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(SecurityUtil.authUserId(),LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        controller.get(getId(request), SecurityUtil.authUserId());
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                LocalDate dateFrom = request.getParameter("dateFrom") != null && !request.getParameter("dateFrom").isEmpty() ? LocalDate.parse(request.getParameter("dateFrom")) : LocalDate.MIN;
                LocalDate dateTo = request.getParameter("dateTo") != null && !request.getParameter("dateTo").isEmpty() ? LocalDate.parse(request.getParameter("dateTo")) : LocalDate.MAX;

                LocalTime timeFrom = request.getParameter("timeFrom") != null && !request.getParameter("timeFrom").isEmpty() ? LocalTime.parse(request.getParameter("timeFrom")) : LocalTime.MIN;
                LocalTime timeTo = request.getParameter("timeTo") != null && !request.getParameter("timeTo").isEmpty() ? LocalTime.parse(request.getParameter("timeTo")) : LocalTime.MAX;



                request.setAttribute("meals",
                        MealsUtil.getTos(controller.getAll(SecurityUtil.authUserId()), MealsUtil.DEFAULT_CALORIES_PER_DAY)
                                .stream().filter((m) -> DateTimeUtil.isBetweenDate(m.getDateTime().toLocalDate(), dateFrom, dateTo))
                                .filter((m) -> DateTimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), timeFrom, timeTo))
                                .collect(Collectors.toList()));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
