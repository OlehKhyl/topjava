package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;
    @PersistenceContext
    private EntityManager em;

    public DataJpaMealRepository(CrudMealRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user = em.getReference(User.class, userId);
        meal.setUser(user);
        if (meal.isNew()) {
            return crudRepository.save(meal);
        } else if (get(meal.id(), userId) == null) {
            return null;
        } else {
            return crudRepository.save(meal);
        }
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        try {
            Meal meal = crudRepository.getMealById(id);
            if (meal != null && userId == meal.getUser().id()) {
                crudRepository.delete(meal);
                return !crudRepository.existsById(id);
            }
            throw new NotFoundException("Not found entity with id=" + id);
        } catch (JpaObjectRetrievalFailureException e) {
            throw new NotFoundException("Not found entity with id=" + id);
        }
    }

    @Override
    @Transactional
    public Meal get(int id, int userId) {
        Meal meal = crudRepository.getMealById(id);
        User user = em.getReference(User.class, userId);
        if (meal != null && meal.getUser().id() == user.id()) {
            return meal;
        }
        throw new NotFoundException("Not found entity with id=" + id);
    }

    @Override
    @Transactional
    public List<Meal> getAll(int userId) {
        return crudRepository.getMealsByUserIdOrderByDateTimeDesc(userId);
    }

    @Override
    @Transactional
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.getMealsByDateTimeAfterAndDateTimeBeforeAndUserIdOrderByDateTimeDesc(startDateTime, endDateTime, userId);
    }
}
