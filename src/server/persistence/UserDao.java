package server.persistence;

import shared.domain.User;

public interface UserDao {
    User findByUsername(String username);
}
