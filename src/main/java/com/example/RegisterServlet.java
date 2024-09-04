package com.example;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/yourdbname";
    private static final String DB_USER = "yourusername";
    private static final String DB_PASSWORD = "yourpassword";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Подключение к базе данных
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL-запрос для вставки данных
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Не забудьте добавить хеширование пароля!

            // Выполнение запроса
            pstmt.executeUpdate();

            // Перенаправление на страницу логина
            response.sendRedirect("login.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            // Обработка ошибок (например, вывести сообщение об ошибке)
        } finally {
            // Закрытие ресурсов
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
