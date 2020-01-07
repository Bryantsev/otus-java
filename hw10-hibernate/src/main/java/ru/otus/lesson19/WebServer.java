package ru.otus.lesson19;

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
import ru.otus.lesson19.server.servlets.UserServlet;

public class WebServer {

    private final static String STATIC = "/static";

    public static void main(String[] args) throws Exception {
        ServerProperties.loadProperties(); // Загрузим настройки сервера
        new WebServer().start();
    }

    static DBServiceUser userService() {
        SessionFactory sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml", User.class, Address.class, Phone.class);
        return new DbServiceUserImpl(new UserDaoHibernate(new SessionManagerHibernate(sessionFactory)));
    }

    private void start() throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        final ServletHolder loginHolder = new ServletHolder(new LoginServlet());
        context.addServlet(loginHolder, "/login");
        context.addServlet(loginHolder, "/logout");
        context.addServlet(new ServletHolder(new UserServlet(userService())), "/users/*");
        context.addFilter(new FilterHolder(new AuthorizationFilter()), "/users/*", null);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource(STATIC));
        resourceHandler.setPathInfoOnly(true);
        resourceHandler.setDirAllowed(false);

        Server server = new Server(ServerProperties.getServerPort());
        server.setHandler(new HandlerList(resourceHandler, context));

        server.start();
        server.join();
    }

}
