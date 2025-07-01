package com.rockstar.application.service;

import com.rockstar.application.ports.in.CreateUserUseCase;
import com.rockstar.domain.model.User;
import com.rockstar.domain.ports.out.UserRepository;

public class UserCreator implements CreateUserUseCase {
    private final UserRepository repository;

    public UserCreator(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(String name) {
        repository.save(new User(name));
    }
}
