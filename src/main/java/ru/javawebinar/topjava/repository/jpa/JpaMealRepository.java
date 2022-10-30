package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Transactional
public class JpaMealRepository implements MealRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            User ref = em.getReference(User.class, userId);
            meal.setUser(ref);
            em.persist(meal);
            return meal;
        } else {
            User ref = em.getReference(User.class, userId);
            if (userId == get(meal.id(), userId).getUser().id()) {
                meal.setUser(ref);
                return em.merge(meal);
            }
            return null;
        }
    }

    @Override
    public boolean delete(int id, int userId) {
        User user = em.getReference(User.class, userId);
        return em.createNamedQuery(Meal.DELETE)
                .setParameter("id", id)
                .setParameter("user", user)
                .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        User user = em.getReference(User.class, userId);
        return (Meal) em.createNamedQuery(Meal.GET)
                .setParameter("user", user)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public List<Meal> getAll(int userId) {
        User user = em.getReference(User.class, userId);
        return em.createNamedQuery(Meal.GET_ALL, Meal.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        User user = em.getReference(User.class, userId);
        return em.createNamedQuery(Meal.GET_BETWEEN_HALF_OPEN, Meal.class)
                .setParameter("start", startDateTime)
                .setParameter("end", endDateTime)
                .setParameter("user", user)
                .getResultList();
    }
}