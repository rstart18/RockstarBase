package co.com.nelumbo.backpmo.domain.ports;

import co.com.nelumbo.backpmo.domain.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project);
    List<Project> findAll();
    Optional<Project> findById(Integer id);
}
