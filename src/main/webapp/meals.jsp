<%@ page import="ru.javawebinar.topjava.model.MealTo" %>
<%@ page import="java.util.List" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.time.format.DateTimeFormatter" %><%--
  Created by IntelliJ IDEA.
  User: olegk
  Date: 08.10.2022
  Time: 14:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Meals</title>
    <style>
        table, th, td {
            border: 1px solid black;
        }
    </style>
</head>
<body>

        <a href="addMeal.jsp">Add Meal</a>
        <%
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            List<MealTo> meals = (List<MealTo>) request.getAttribute("meals");
            PrintWriter writer = response.getWriter();
            writer.println("<table style=\"border:solid\">");

            for (MealTo meal : meals) {
                String color = null;

                if (meal.isExcess()) {
                    color = "#FF0000";
                } else {
                    color = "#008000";
                }


                writer.println("<tr style=\"color:" + color +"\">");
                writer.println("<td>");
                writer.println(meal.getId());
                writer.println("</td>");
                writer.println("<td>");
                writer.println(formatter.format(meal.getDateTime()));
                writer.println("</td>");
                writer.println("<td>");
                writer.println(meal.getDescription());
                writer.println("</td>");
                writer.println("<td>");
                writer.println(meal.getCalories());
                writer.println("</td>");
                writer.println("<td>");
                writer.println("<a href=\"editMeal.jsp?id=" + meal.getId() + "\">Edit</a>");
                writer.println("</td>");
                writer.println("<td>");
                writer.println("<a href=\"meals?action=delete&id=" + meal.getId() + "\">Delete</a>");
                writer.println("</td>");
                writer.println("</tr>");
            }

            writer.println("</table>");
        %>
</body>
</html>
