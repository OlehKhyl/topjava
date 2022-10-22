package ru.javawebinar.topjava.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration({
            "classpath:spring/spring-app.xml",
            "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    private static final int USER_ID = SecurityUtil.authUserId();
    private static final int NOT_FOUND = 55;


    @Test
    public void get() {
        int mealId = 100007;
        Meal meal = new Meal(mealId, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
        Meal received = service.get(mealId, USER_ID);

        Assert.assertEquals(meal, received);

    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));

    }

    @Test
    public void getAnotherUser() {
        assertThrows(NotFoundException.class, () -> service.get(100005, 222));

    }

    @Test
    public void delete() {
        Meal meal = new Meal(null, LocalDateTime.of(2022, 10, 22, 14, 0), "Обед", 1000);
        Meal created = service.create(meal, USER_ID);
        int id = created.getId();
        service.delete(id, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(id, USER_ID));

    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));

    }

    @Test
    public void deleteAnotherUser() {
        assertThrows(NotFoundException.class, () -> service.delete(100005, 222));

    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> mealList = new ArrayList<>();
        mealList.add(new Meal(100005, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        mealList.add(new Meal(100004, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        mealList.add(new Meal(100003, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));


        List<Meal> receivedMealList = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 30), LocalDate.of(2020, Month.JANUARY, 30), USER_ID);

        Assert.assertEquals(mealList, receivedMealList);


    }

    @Test
    public void getAll() {
        List<Meal> mealList = new ArrayList<>();
        mealList.add(new Meal(100009, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
        mealList.add(new Meal(100008, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        mealList.add(new Meal(100007, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        mealList.add(new Meal(100006, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        mealList.add(new Meal(100005, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        mealList.add(new Meal(100004, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        mealList.add(new Meal(100003, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));

        List<Meal> receivedMealList = service.getAll(USER_ID);

        Assert.assertEquals(mealList, receivedMealList);

    }

    @Test
    public void update() {
        Meal meal = new Meal(null, LocalDateTime.of(2022, 10, 22, 13, 0), "Обед", 1000);
        Meal created = service.create(meal, USER_ID);

        meal.setId(created.getId());
        meal.setDateTime(LocalDateTime.of(2022, 10, 22, 20, 0));
        meal.setDescription("Ужин");
        meal.setCalories(500);
        service.update(meal, USER_ID);

        Assert.assertEquals(service.get(meal.getId(), USER_ID), meal);
    }

    @Test
    public void updateAnotherUser() {
        Meal meal = new Meal(null, LocalDateTime.of(2022, 10, 22, 13, 0), "Обед", 1000);
        Meal created = service.create(meal, USER_ID);

        meal.setId(created.getId());
        meal.setDateTime(LocalDateTime.of(2022, 10, 22, 20, 0));
        meal.setDescription("Ужин");
        meal.setCalories(500);
        assertThrows(NotFoundException.class, () -> service.update(meal, 222));

    }

    @Test
    public void create() {
        Meal meal = new Meal(null, LocalDateTime.of(2022, 10, 22, 13, 0), "Обед", 1000);
        Meal created = service.create(meal, USER_ID);
        meal.setId(created.getId());
        Assert.assertEquals(meal, created);
        Assert.assertEquals(meal.getId(), created.getId());
    }

    @Test
    public void createDuplicateTime() {
        Meal meal = new Meal(null, LocalDateTime.of(2022, 10, 22, 13, 0), "Обед", 1000);
        Meal created = service.create(meal, USER_ID);

        Meal mealWithSameTime = new Meal(null, LocalDateTime.of(2022, 10, 22, 13, 0), "Суп", 750);
        assertThrows(DataAccessException.class, () -> service.create(mealWithSameTime, USER_ID));
    }
}