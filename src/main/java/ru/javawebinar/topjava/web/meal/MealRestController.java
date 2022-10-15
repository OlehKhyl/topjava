package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.util.List;

@Controller
public class MealRestController {
    private MealService service;

    public MealRestController(@Autowired MealService service) {
        this.service = service;
    }

    public List<Meal> getAll(Integer userId) {
        return service.getAll(userId);
    }

    public Meal get(int id, Integer userId) {
        return service.get(id, userId);
    }

    public Meal create(Meal meal, Integer userId) {
        ValidationUtil.checkNew(meal);

        return service.create(meal, userId);
    }

    public void delete(int id, Integer userId) {
        service.delete(id, userId);
    }

    public void update(Meal meal, int id, Integer userId) {
        ValidationUtil.assureIdConsistent(meal, id);
        service.update(meal, userId);
    }
}