package server.service;

import server.persistence.UserDao;
import shared.domain.User;

// Håndterer login - tjekker om brugernavn og password passer
public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String username, String password) {
        if (username == null || password == null) return null;

        User user = userDao.findByUsername(username);
        if (user == null) return null;

        // Simpel sammenligning af passwords
        if (!user.password().equals(password)) return null;

        return user;
    }
}
