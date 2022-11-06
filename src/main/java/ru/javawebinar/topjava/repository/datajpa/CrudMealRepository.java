package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    Meal getMealById(int id);
    List<Meal> getMealsByUserIdOrderByDateTimeDesc(int userId);
    List<Meal> getMealsByDateTimeAfterAndDateTimeBeforeAndUserIdOrderByDateTimeDesc(LocalDateTime start, LocalDateTime end, int userId);
}
