package com.rockstar;

import com.rockstar.application.service.UserCreator;
import com.rockstar.domain.model.User;
import com.rockstar.infrastructure.adapter.out.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserCreatorTest {
    @Test
    void createsUsers() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserCreator creator = new UserCreator(repository);
        creator.create("Test");
        assertEquals(1, repository.allUsers().size());
        assertEquals("Test", repository.allUsers().get(0).getName());
    }
}
