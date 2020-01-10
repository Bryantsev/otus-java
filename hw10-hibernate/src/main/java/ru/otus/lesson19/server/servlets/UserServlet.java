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

    private static final String USERS_ADD_PATH = "/users/add";
    private static final String USERS_DELETE_PATH = "/users/delete";
    private static final String LOGIN_ATTRIBUTE = "login";
    private static final String USER_NAME_PARAMETER = "name";
    private static final String USER_AGE_PARAMETER = "age";
    private static final String ERROR_MSG_PARAMETER = "error_msg";
    private static final String USERS_PARAMETER = "users";
    private static final String USER_ID_PARAMETER = "id";
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
        if (USERS_DELETE_PATH.equals(req.getRequestURI())) {
            deleteUser(req, resp);
        } else {
            generateUsersList(req, resp, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (USERS_ADD_PATH.equals(req.getRequestURI())) {
            addUser(req, resp);
        } else {
            generateUsersList(req, resp, null);
        }
    }

    private void generateUsersList(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> pageVariables) throws IOException {
        if (pageVariables == null) {
            pageVariables = new HashMap<>();
        }
        // Генерация списка пользователей
        resp.setContentType("text/html;charset=utf-8");
        HttpSession session = req.getSession(false);
        pageVariables.put(LOGIN_ATTRIBUTE, session.getAttribute(LOGIN_ATTRIBUTE)); // Сессию на null не проверяем, без нее фильтр не пропустит на эту страницу

        List<User> users = userService.selectAll();
        logger.info("Users count: {}", users.size());
        pageVariables.put(USERS_PARAMETER, users);
        resp.getWriter().println(templateProcessor.getPage(USERS_PAGE_TEMPLATE, pageVariables));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void addUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> pageVariables = new HashMap<>();
        String name = req.getParameter(USER_NAME_PARAMETER);
        if (Strings.isNullOrEmpty(name)) {
            pageVariables.put(ERROR_MSG_PARAMETER, "Не задано имя пользователя!");
            pageVariables.put(USER_AGE_PARAMETER, req.getParameter(USER_AGE_PARAMETER));
        } else {
            final String age = req.getParameter(USER_AGE_PARAMETER);
            try {
                userService.saveUser(new User(null, name, Strings.isNullOrEmpty(age) ? null : Integer.parseInt(age)));
            } catch (DbServiceException ex) {
                logger.error("addUser: {}", ex.getMessage()); // Логгируем без стека вызовов
                saveErrorDataForClient(name, req.getParameter(USER_AGE_PARAMETER), pageVariables, "Ошибка добавления пользователя: " + ex.getMessage());
            } catch (Exception ex) {
                logger.error("addUser: {}", ex.getMessage(), ex);
                saveErrorDataForClient(name, req.getParameter(USER_AGE_PARAMETER), pageVariables, "Ошибка добавления пользователя: " + ex.getMessage());
            }
        }
        generateUsersList(req, resp, pageVariables);
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> pageVariables = new HashMap<>();
        String id = req.getParameter(USER_ID_PARAMETER);
        if (!Strings.isNullOrEmpty(id)) {
            try {
                userService.deleteUser(Long.parseLong(id));
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                pageVariables.put(ERROR_MSG_PARAMETER, "Ошибка удаления пользователя: " + ex.getMessage());
            }
        }
        generateUsersList(req, resp, pageVariables);
    }

    private void saveErrorDataForClient(final String name, final String age, Map<String, Object> pageVariables, String errorMessage) {
        pageVariables.put(ERROR_MSG_PARAMETER, errorMessage);
        pageVariables.put(USER_NAME_PARAMETER, name);
        pageVariables.put(USER_AGE_PARAMETER, age);
    }

}
