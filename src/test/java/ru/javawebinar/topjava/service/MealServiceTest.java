package ru.javawebinar.topjava.service;

import org.junit.*;
import org.junit.rules.Stopwatch;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {
    public static String log;

    @BeforeClass
    public static void LogInit() {
        log = "";
    }


    @Autowired
    private MealService service;

    @Rule
    public final Stopwatch time = new Stopwatch();


    @Rule
    public final TestName name = new TestName();

    @Test
    public void delete() {
        service.delete(MEAL1_ID, USER_ID);
        assertThrows(NoResultException.class, () -> service.get(MEAL1_ID, USER_ID));
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL1_ID, ADMIN_ID));
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        int newId = created.id();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        Assert.assertEquals(created, newMeal);
        Assert.assertEquals(service.get(newId, USER_ID), newMeal);
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null, meal1.getDateTime(), "duplicate", 100), USER_ID));
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void get() {
        Meal actual = service.get(ADMIN_MEAL_ID, ADMIN_ID);
        Assert.assertEquals(actual, adminMeal1);
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void getNotFound() {
        assertThrows(NoResultException.class, () -> service.get(NOT_FOUND, USER_ID));
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void getNotOwn() {
        Assert.assertThrows(NoResultException.class, () -> service.get(MEAL1_ID, ADMIN_ID));
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        Assert.assertEquals(service.get(MEAL1_ID, USER_ID), getUpdated());
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void updateNotOwn() {
        assertThrows(NoResultException.class, () -> service.update(meal1, ADMIN_ID));
        Assert.assertEquals(service.get(MEAL1_ID, USER_ID), meal1);
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void getAll() {
        Assert.assertEquals(service.getAll(USER_ID), meals);
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> expected = new ArrayList<>();
        expected.add(meal3);
        expected.add(meal2);
        expected.add(meal1);
        Assert.assertEquals(expected,
                service.getBetweenInclusive(
                        LocalDate.of(2020, Month.JANUARY, 30),
                        LocalDate.of(2020, Month.JANUARY, 30), USER_ID)
                );
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @Test
    public void getBetweenWithNullDates() {
        Assert.assertEquals(service.getBetweenInclusive(null, null, USER_ID), meals);
        log += name.getMethodName() + " " + time.runtime(TimeUnit.NANOSECONDS) + "ns \n";
        System.out.println(time.runtime(TimeUnit.NANOSECONDS) + "ns \n");
    }

    @AfterClass
    public static void LogInfo() {
        System.out.println(log);
    }
}