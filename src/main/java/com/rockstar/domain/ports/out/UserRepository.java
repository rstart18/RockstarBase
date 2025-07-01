package com.rockstar.domain.ports.out;

import com.rockstar.domain.model.User;

public interface UserRepository {
    void save(User user);
}
