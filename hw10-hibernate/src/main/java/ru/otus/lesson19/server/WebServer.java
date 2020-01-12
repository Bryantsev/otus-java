package ru.otus.lesson19.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.hibernate.SessionFactory;
import ru.otus.lesson19.api.model.Address;
import ru.otus.lesson19.api.model.Phone;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.service.DBServiceUser;
import ru.otus.lesson19.api.service.DbServiceUserImpl;
import ru.otus.lesson19.config.ServerProperties;
import ru.otus.lesson19.hibernate.HibernateUtils;
import ru.otus.lesson19.hibernate.dao.UserDaoHibernate;
import ru.otus.lesson19.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.lesson19.server.filters.AuthorizationFilter;
import ru.otus.lesson19.server.servlets.LoginServlet;
import ru.otus.lesson19.server.servlets.LogoutServlet;
import ru.otus.lesson19.server.servlets.UserServlet;

public class WebServer {

    private static final String STATIC = "/static";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGOUT_PATH = "/logout";
    private static final String USERS_PATH = "/users/*";

    private final Server server;

    public WebServer() {
        ServerProperties.loadProperties(); // Загрузим настройки сервера
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(new LoginServlet()), LOGIN_PATH);
        context.addServlet(new ServletHolder(new LogoutServlet()), LOGOUT_PATH);
        context.addServlet(new ServletHolder(new UserServlet(getUserService())), USERS_PATH);
        context.addFilter(new FilterHolder(new AuthorizationFilter()), USERS_PATH, null);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource(STATIC));
        resourceHandler.setPathInfoOnly(true);
        resourceHandler.setDirAllowed(false);

        server = new Server(ServerProperties.getServerPort());
        server.setHandler(new HandlerList(resourceHandler, context));
    }

    private DBServiceUser getUserService() {
        SessionFactory sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml", User.class, Address.class, Phone.class);
        return new DbServiceUserImpl(new UserDaoHibernate(new SessionManagerHibernate(sessionFactory)));
    }

    public void start() throws Exception {
        server.start();
        server.join();
    }

}
