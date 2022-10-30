package ru.javawebinar.topjava.repository.jpa;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class JpaMealRepositoryTest {

    @Autowired
    private MealRepository mealRepository;

    @Test
    public void save() {
        Meal meal = MealTestData.getNew();
        int userId = SecurityUtil.authUserId();
        Meal saved = mealRepository.save(meal, userId);
        Assert.assertEquals(meal, saved);
    }

    @Test
    public void delete() {
        Meal meal = MealTestData.getNew();
        int userId = SecurityUtil.authUserId();
        Meal saved = mealRepository.save(meal, userId);
        mealRepository.delete(saved.id(), userId);
        Assert.assertThrows(NoResultException.class, () -> mealRepository.get(saved.id(), userId));
    }

    @Test
    public void get() {
        int userId = SecurityUtil.authUserId();
        Meal meal = MealTestData.meal1;
        Assert.assertEquals(meal, mealRepository.get(MealTestData.MEAL1_ID, userId));
    }

    @Test
    public void getAll() {
        int userId = SecurityUtil.authUserId();
        List<Meal> expectedMeals = new ArrayList<>(MealTestData.meals);

        List<Meal> actual = mealRepository.getAll(userId);

        Assert.assertEquals(expectedMeals, actual);
    }

    @Test
    public void getBetweenHalfOpen() {
        int userId = SecurityUtil.authUserId();
        List<Meal> expected = new ArrayList<>();
        expected.add(MealTestData.meal3);
        expected.add(MealTestData.meal2);
        expected.add(MealTestData.meal1);

        List<Meal> actual = mealRepository.getBetweenHalfOpen(LocalDateTime.of(2020, 1, 30, 0, 0), LocalDateTime.of(2020, 1, 30, 23, 59), userId);

        Assert.assertEquals(expected, actual);

    }
}