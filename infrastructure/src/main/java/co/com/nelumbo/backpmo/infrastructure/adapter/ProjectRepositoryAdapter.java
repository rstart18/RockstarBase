package co.com.nelumbo.backpmo.infrastructure.adapter;

import co.com.nelumbo.backpmo.domain.model.Project;
import co.com.nelumbo.backpmo.domain.model.Status;
import co.com.nelumbo.backpmo.domain.ports.ProjectRepository;
import co.com.nelumbo.backpmo.infrastructure.entity.ProjectEntity;
import co.com.nelumbo.backpmo.infrastructure.repository.SpringProjectRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final SpringProjectRepository repository;

    public ProjectRepositoryAdapter(SpringProjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = toEntity(project);
        ProjectEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Project> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Project> findById(Integer id) {
        return repository.findById(id).map(this::toDomain);
    }

    private ProjectEntity toEntity(Project project) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(project.getId());
        entity.setName(project.getName());
        entity.setDescription(project.getDescription());
        entity.setStartDate(project.getStartDate());
        entity.setEndDate(project.getEndDate());
        entity.setStatus(project.getStatus());
        return entity;
    }

    private Project toDomain(ProjectEntity entity) {
        return new Project(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus()
        );
    }
}
