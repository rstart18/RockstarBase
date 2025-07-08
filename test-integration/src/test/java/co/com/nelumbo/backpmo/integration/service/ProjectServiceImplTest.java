package co.com.nelumbo.backpmo.integration.service;

import co.com.nelumbo.backpmo.application.service.ProjectServiceImpl;
import co.com.nelumbo.backpmo.domain.model.Project;
import co.com.nelumbo.backpmo.domain.ports.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceImplTest {

    private ProjectRepository repository;
    private ProjectServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(ProjectRepository.class);
        service = new ProjectServiceImpl(repository);
    }

    @Test
    void createShouldSaveProject() {
        Project project = new Project();
        Project saved = new Project();
        when(repository.save(project)).thenReturn(saved);

        Project result = service.create(project);

        verify(repository).save(project);
        assertSame(saved, result);
    }

    @Test
    void listShouldReturnAllProjects() {
        List<Project> projects = Arrays.asList(new Project(), new Project());
        when(repository.findAll()).thenReturn(projects);

        List<Project> result = service.list();

        verify(repository).findAll();
        assertEquals(projects, result);
    }

    @Test
    void findByIdShouldReturnProject() {
        Project project = new Project();
        when(repository.findById(1)).thenReturn(Optional.of(project));

        Optional<Project> result = service.findById(1);

        verify(repository).findById(1);
        assertTrue(result.isPresent());
        assertSame(project, result.get());
    }
}
