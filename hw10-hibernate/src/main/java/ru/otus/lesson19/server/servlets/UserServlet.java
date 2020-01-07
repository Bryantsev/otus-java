package ru.otus.lesson19.server.servlets;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.service.DBServiceUser;
import ru.otus.lesson19.api.service.DbServiceException;
import ru.otus.lesson19.server.utils.TemplateProcessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(UserServlet.class);

    private static final String USERS_PAGE_TEMPLATE = "users.ftl";
    private final DBServiceUser userService;
    private final TemplateProcessor templateProcessor;

    public UserServlet(DBServiceUser userService) {
        this.userService = userService;
        this.templateProcessor = new TemplateProcessor();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        executeAction(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        executeAction(req, resp);
    }

    private void executeAction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        String uri = req.getRequestURI();
        // Добавление пользователя
        if ("/users/add".equals(uri)) {
            String name = req.getParameter("name");
            if (Strings.isNullOrEmpty(name)) {
                pageVariables.put("error_msg", "Не задано имя пользователя!");
                pageVariables.put("age", req.getParameter("age"));
            } else {
                final String age = req.getParameter("age");
                try {
                    userService.saveUser(new User(null, name, Strings.isNullOrEmpty(age) ? null : Integer.parseInt(age)));
                } catch (DbServiceException ex) {
                    logger.error("executeAction: {}", ex.getMessage()); // Логгируем без стека вызовов
                    saveErrorDataForClient(req.getParameter("name"), req.getParameter("age"), pageVariables, "Ошибка добавления пользователя: " + ex.getMessage());
                } catch (Exception ex) {
                    logger.error("executeAction: {}", ex.getMessage(), ex);
                    saveErrorDataForClient(req.getParameter("name"), req.getParameter("age"), pageVariables, "Ошибка добавления пользователя: " + ex.getMessage());
                }
            }
            // Удаление пользователя
        } else if ("/users/delete".equals(uri)) {
            String id = req.getParameter("id");
            if (!Strings.isNullOrEmpty(id)) {
                try {
                    userService.deleteUser(Long.parseLong(id));
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    pageVariables.put("error_msg", "Ошибка удаления пользователя: " + ex.getMessage());
                }
            }
        }
        // Генерация списка пользователей
        resp.setContentType("text/html;charset=utf-8");
        HttpSession session = req.getSession(false);
        pageVariables.put("login", session.getAttribute("login")); // Сессию на null не проверяем, без нее фильтр не пропустит на эту страницу

        List<User> users = userService.selectAll();
        logger.info("Users count: {}", users.size());
        pageVariables.put("users", users);
        resp.getWriter().println(templateProcessor.getPage(USERS_PAGE_TEMPLATE, pageVariables));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void saveErrorDataForClient(final String name, final String age, Map<String, Object> pageVariables, String errorMessage) {
        pageVariables.put("error_msg", errorMessage);
        pageVariables.put("name", name);
        pageVariables.put("age", age);
    }

}
