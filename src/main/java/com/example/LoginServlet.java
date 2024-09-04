package com.example;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/yourdbname";
    private static final String DB_USER = "yourusername";
    private static final String DB_PASSWORD = "yourpassword";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Подключение к базе данных
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL-запрос для получения пользователя по имени
            String sql = "SELECT password FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Получение хешированного пароля из базы данных
                String hashedPassword = rs.getString("password");

                // Проверка введённого пароля
                if (BCrypt.checkpw(password, hashedPassword)) {
                    // Создание сессии и установка атрибута
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);

                    // Перенаправление на страницу приветствия
                    response.sendRedirect("welcome.jsp");
                    return;
                }
            }

            // Если проверка не удалась, перенаправляем обратно на страницу входа
            response.sendRedirect("login.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            // Логирование ошибки
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
        } finally {
            // Закрытие ресурсов
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
