package ru.otus.lesson19.server.servlets;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lesson19.config.ServerProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(LoginServlet.class);

    private void authenticateAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String uri = req.getRequestURI();
        // Выход пользователя
        if ("/logout".equals(uri)) {
            if (session != null) {
                session.invalidate();
            }

            // Логин пользователя
        } else if ("/login".equals(uri)) {
            // Если пользователь имеет открытую сессию, то перенаправим на список пользователей
            if (session != null) {
                logger.info("User {} was authenticated", session.getAttribute("login"));
                resp.sendRedirect("/users");
                return;
            }

            // Проверим учетные данные админа
            final String login = req.getParameter("login");
            if (authenticate(login, req.getParameter("password"))) {
                // Создаем сессию, сохраняем логин и переходим на список пользователей
                session = req.getSession();
                session.setAttribute("login", login);
                session.setMaxInactiveInterval(ServerProperties.getAdminSessionExpireInterval());
                resp.sendRedirect("/users");
                return;
            }
        }

        // иначе отправляемся на страницу логина
        logger.info("User must be authenticated");
        resp.sendRedirect("/login.html");
    }

    private boolean authenticate(String login, String password) {
        logger.info("login: {}", login);

        return
            !Strings.isNullOrEmpty(login) && !Strings.isNullOrEmpty(password) &&
                login.equals(ServerProperties.getAdminUser()) && password.equals(ServerProperties.getAdminPassword());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        authenticateAdmin(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        authenticateAdmin(req, resp);
    }

}
