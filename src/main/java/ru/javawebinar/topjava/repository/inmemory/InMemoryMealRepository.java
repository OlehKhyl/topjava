package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach((meal -> save(meal, meal.getUserId())));
    }

    @Override
    public Meal save(Meal meal, Integer userId) {
        if (meal.getUserId().equals(userId)) {
            if (meal.isNew()) {
                meal.setId(counter.incrementAndGet());
                repository.put(meal.getId(), meal);
                return meal;
            }
            // handle case: update, but not present in storage
            return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        }
        return null;
    }

    @Override
    public boolean delete(int id, Integer userId) {
        if (Objects.equals(repository.get(id).getUserId(), userId)) {
            return repository.remove(id) != null;
        }

        return false;
    }

    @Override
    public Meal get(int id, Integer userId) {
        Meal meal = repository.get(id);
        if (Objects.equals(meal.getUserId(), userId)) {
            return meal;
        }
        return null;
    }

    @Override
    public List<Meal> getAll(Integer userId) {
        return repository.values().stream()
                .filter((meal) -> meal.getUserId().equals(userId))
                .sorted((m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()))
                .collect(Collectors.toList());
    }
}

