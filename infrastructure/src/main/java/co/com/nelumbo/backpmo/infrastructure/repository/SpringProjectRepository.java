package co.com.nelumbo.backpmo.infrastructure.repository;

import co.com.nelumbo.backpmo.infrastructure.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringProjectRepository extends JpaRepository<ProjectEntity, Integer> {
}
