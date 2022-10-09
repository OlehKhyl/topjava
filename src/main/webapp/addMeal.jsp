<%--
  Created by IntelliJ IDEA.
  User: olegk
  Date: 09.10.2022
  Time: 9:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Meal</title>
</head>
<body>
        <a href="index.html">Home</a><br>
        <a href="meals">Back</a><br>
        <hr>
        <h1>Add Meal</h1><br>
        <form action="meals" method="post">
          <label for="date">DateTime:</label>
          <input type="datetime-local" name="date" id="date">
          <label for="description">Description</label>
          <input type="text" name="description" id="description">
          <label for="calories">Calories</label>
          <input type="number" name="calories" id="calories">
          <input type="submit" value="Save">
        </form>
</body>
</html>
