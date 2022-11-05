package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        return crudRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = crudRepository.getReferenceById(id);
        crudRepository.delete(meal);
        return !crudRepository.existsById(id);
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.getReferenceById(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAll();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.getMealsByDateTimeAfterAndDateTimeBeforeOrderByDateTimeDesc(startDateTime, endDateTime);
    }
}
