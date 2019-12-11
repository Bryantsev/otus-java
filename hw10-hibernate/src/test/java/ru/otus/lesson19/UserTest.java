package ru.otus.lesson19;


import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lesson19.api.dao.UserDao;
import ru.otus.lesson19.api.model.Address;
import ru.otus.lesson19.api.model.Phone;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.service.DBServiceUser;
import ru.otus.lesson19.api.service.DbServiceUserImpl;
import ru.otus.lesson19.hibernate.HibernateUtils;
import ru.otus.lesson19.hibernate.dao.UserDaoHibernate;
import ru.otus.lesson19.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.List;
import java.util.Optional;

public class UserTest {
    private static Logger logger = LoggerFactory.getLogger(UserTest.class);


    @Test
    public void test() {
        SessionFactory sessionFactory =
            HibernateUtils.buildSessionFactory("hibernate.cfg.xml", User.class, Address.class, Phone.class);

        SessionManagerHibernate sessionManager = new SessionManagerHibernate(sessionFactory);
        UserDao userDao = new UserDaoHibernate(sessionManager);
        DBServiceUser dbServiceUser = new DbServiceUserImpl(userDao);

        // Добавляем пользователя
        final User newUser = new User(null, "Вася", 40);
        newUser.getPhones().add(new Phone(newUser, "499 333-44-55"));
        newUser.getPhones().add(new Phone(newUser, "499 333-44-99"));
        long id = dbServiceUser.saveUser(newUser);
        Optional<User> userOpt = dbServiceUser.getUser(id, true, true);
        Assert.assertTrue(userOpt.isPresent());
        User user = userOpt.get();
        Assert.assertEquals(Long.valueOf(1L), user.getId()); // 1му пользователю 1й ид-р
        Assert.assertEquals("Вася", user.getName());
        Assert.assertEquals(Integer.valueOf(40), user.getAge());

        // Добавляем адрес и заново вычитываем пользователя из базы
        Address address = user.getAddress();
        if (address == null) {
            user.setAddress(new Address(user, "ул. Центральная", "23", null));
            id = dbServiceUser.saveUser(user);
            userOpt = dbServiceUser.getUser(id, true, true);
            Assert.assertTrue(userOpt.isPresent());
            user = userOpt.get();
            address = user.getAddress();
        }

        Assert.assertNotNull(address);
        Assert.assertEquals(Long.valueOf(1L), address.getId()); // 1му адресу 1й ид-р
        Assert.assertEquals("ул. Центральная", address.getStreet());
        Assert.assertEquals("23", address.getHouse());
        Assert.assertNull(address.getFlat());

        List<Phone> phones = user.getPhones();
        Assert.assertEquals(2, phones.size());
        for (Phone phone : phones) {
            Assert.assertTrue("499 333-44-55".equals(phone.getNumber()) || "499 333-44-99".equals(phone.getNumber()));
        }

        // Изменяем пользователя, его адрес и телефоны
        user.setName("Вася2");
        user.setAge(44);
        address.setStreet("Другая ул.");
        address.setHouse("11");
        address.setFlat("9");
        // Удалим телефон 499 333-44-99
        for (int i = phones.size() - 1; i >= 0; i--) {
            if ("499 333-44-99".equals(phones.get(i).getNumber())) {
                phones.remove(i);
                break;
            }
        }
        // Добавим новый телефон
        phones.add(new Phone(user, "499 333-44-77"));
        // Сохраним пользователя с измененными данными
        id = dbServiceUser.saveUser(user);
        userOpt = dbServiceUser.getUser(id, true, true);
        Assert.assertTrue(userOpt.isPresent());
        user = userOpt.get();
        Assert.assertEquals(Long.valueOf(1L), user.getId());
        Assert.assertEquals("Вася2", user.getName());
        Assert.assertEquals(Integer.valueOf(44), user.getAge());

        address = user.getAddress();
        Assert.assertNotNull(address);
        Assert.assertEquals("Другая ул.", address.getStreet());
        Assert.assertEquals("11", address.getHouse());
        Assert.assertEquals("9", address.getFlat());

        phones = user.getPhones();
        Assert.assertEquals(2, phones.size());
        for (Phone phone : phones) {
            Assert.assertTrue("499 333-44-55".equals(phone.getNumber()) || "499 333-44-77".equals(phone.getNumber()));
        }
    }

}
