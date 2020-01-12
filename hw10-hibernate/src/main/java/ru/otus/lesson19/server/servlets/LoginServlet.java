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

    private static final String USERS_PAGE_LINK = "/users";
    private static Logger logger = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        login(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        login(req, resp);
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        // Если пользователь имеет открытую сессию, то перенаправим на список пользователей
        if (session != null) {
            resp.sendRedirect(USERS_PAGE_LINK);
        } else {
            // Проверим учетные данные админа
            final String login = req.getParameter("login");
            String password = req.getParameter("password");
            if (!Strings.isNullOrEmpty(login) && !Strings.isNullOrEmpty(password) &&
                login.equals(ServerProperties.getAdminUser()) && password.equals(ServerProperties.getAdminPassword())) {
                logger.info("User with login {} was authenticated", login);
                // Создаем сессию, сохраняем логин и переходим на список пользователей
                session = req.getSession();
                session.setAttribute("login", login);
                session.setMaxInactiveInterval(ServerProperties.getAdminSessionExpireInterval());
                resp.sendRedirect(USERS_PAGE_LINK);
            } else {
                logger.info("Wrong login {} or password", login);
                // иначе отправляемся на страницу логина
                resp.sendRedirect("/login.html");
            }
        }
    }

}
