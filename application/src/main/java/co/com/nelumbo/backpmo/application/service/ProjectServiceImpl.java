package co.com.nelumbo.backpmo.application.service;

import co.com.nelumbo.backpmo.domain.model.Project;
import co.com.nelumbo.backpmo.domain.ports.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository repository;

    public ProjectServiceImpl(ProjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public Project create(Project project) {
        return repository.save(project);
    }

    @Override
    public List<Project> list() {
        return repository.findAll();
    }

    @Override
    public Optional<Project> findById(Integer id) {
        return repository.findById(id);
    }
}
