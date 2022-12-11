package ru.javawebinar.topjava.to;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.Objects;

public class MealBuilder extends BaseTo {

    @NotNull
    private final String dateTime;

    @NotBlank
    @Size(min = 2, max = 100)
    private final String description;

    @NotNull
    @Range(min = 5, max = 100000)
    private final int calories;

    @ConstructorProperties({"id", "dateTime", "description", "calories"})
    public MealBuilder(Integer id, String dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealBuilder mealBuilder = (MealBuilder) o;
        return calories == mealBuilder.calories &&
                Objects.equals(id, mealBuilder.id) &&
                Objects.equals(dateTime, mealBuilder.dateTime) &&
                Objects.equals(description, mealBuilder.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateTime, description, calories);
    }

    @Override
    public String toString() {
        return "MealTo{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
