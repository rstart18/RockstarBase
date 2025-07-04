package co.com.nelumbo.backpmo.infrastructure.controller;

import co.com.nelumbo.backpmo.apifirst.openapi.api.ProjectsApi;
import co.com.nelumbo.backpmo.apifirst.openapi.model.NewProjectDto;
import co.com.nelumbo.backpmo.apifirst.openapi.model.ProjectDto;
import co.com.nelumbo.backpmo.application.service.ProjectService;
import co.com.nelumbo.backpmo.domain.model.Project;
import co.com.nelumbo.backpmo.domain.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProjectController implements ProjectsApi {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<ProjectDto> createProject(NewProjectDto newProjectDto) {
        Project project = fromDto(newProjectDto);
        Project saved = service.create(project);
        return new ResponseEntity<>(toDto(saved), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ProjectDto> getProjectById(Integer projectId) {
        return service.findById(projectId)
                .map(p -> ResponseEntity.ok(toDto(p)))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<List<ProjectDto>> getProjects() {
        List<ProjectDto> list = service.list().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    private ProjectDto toDto(Project project) {
        ProjectDto dto = new ProjectDto(project.getId(), project.getName(), ProjectDto.StatusEnum.valueOf(project.getStatus().name()));
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        return dto;
    }

    private Project fromDto(NewProjectDto dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(Status.valueOf(dto.getStatus().name()));
        return project;
    }
}
