package co.com.nelumbo.backpmo.application.service;

import co.com.nelumbo.backpmo.domain.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project create(Project project);
    List<Project> list();
    Optional<Project> findById(Integer id);
}
