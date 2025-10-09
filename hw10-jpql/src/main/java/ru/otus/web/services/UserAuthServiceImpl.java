package ru.otus.web.services;

import java.util.Optional;

import ru.otus.crm.model.User;
import ru.otus.web.dao.UserDao;

public class UserAuthServiceImpl implements UserAuthService {

    private final UserDao userDao;

    public UserAuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean authenticate(String login, String password) {
        Optional<User> user = userDao.findByLogin(login);
        return user.isPresent() && user.get().getPassword().equals(password);
    }

    public boolean isAdmin(String login) {
        Optional<User> user = userDao.findByLogin(login);
        return user.isPresent() && user.get().isAdmin();
    }
}
