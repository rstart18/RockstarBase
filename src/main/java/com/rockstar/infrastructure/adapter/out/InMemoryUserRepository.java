package com.rockstar.infrastructure.adapter.out;

import com.rockstar.domain.model.User;
import com.rockstar.domain.ports.out.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public void save(User user) {
        users.add(user);
    }

    public List<User> allUsers() {
        return List.copyOf(users);
    }
}
