package com.rockstar.infrastructure;

import com.rockstar.application.service.UserCreator;
import com.rockstar.infrastructure.adapter.in.rest.UserController;
import com.rockstar.infrastructure.adapter.out.InMemoryUserRepository;

public class RockstarApplication {
    public static void main(String[] args) {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserCreator creator = new UserCreator(repository);
        UserController controller = new UserController(creator);

        controller.create("Alice");
        controller.create("Bob");

        System.out.println("Users: " + repository.allUsers().size());
    }
}
