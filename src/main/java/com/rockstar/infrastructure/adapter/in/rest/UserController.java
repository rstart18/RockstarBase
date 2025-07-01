package com.rockstar.infrastructure.adapter.in.rest;

import com.rockstar.application.ports.in.CreateUserUseCase;

public class UserController {
    private final CreateUserUseCase useCase;

    public UserController(CreateUserUseCase useCase) {
        this.useCase = useCase;
    }

    public void create(String name) {
        useCase.create(name);
    }
}
