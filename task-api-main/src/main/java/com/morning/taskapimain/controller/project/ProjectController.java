package com.morning.taskapimain.controller.project;

import com.morning.taskapimain.entity.Field;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.dto.FieldDTO;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.entity.dto.UserDTO;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor
@CrudExceptionHandler
@AccessExceptionHandler
@BadRequestExceptionHandler
public class ProjectController {

    private final JwtService jwtService;
    private final ProjectService projectService;

    @GetMapping("")
    public Flux<ProjectDTO> getProjectsByUsername(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        return projectService.findAllByUsername(jwtService.extractUsername(token)).map(ProjectDTO::fromProject);
    }

    @GetMapping("/{id}")
    public Mono<?> getProjectById(@RequestHeader(name = HttpHeaders.AUTHORIZATION,required = false) String token, @PathVariable String id) {
        return token == null ?
                projectService.findByIdAndVisibility(Long.valueOf(id)).map(ProjectDTO::fromProject) :
                projectService.findByIdAndVisibility(Long.valueOf(id), token).map(ProjectDTO::fromProject);
    }

    @PostMapping("")
    public Mono<Project> addProject(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token, @RequestBody ProjectDTO dto){
        return projectService.addProject(dto, token);
    }
    @PutMapping("/{id}")
    public Mono<Project> updateProject(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                 @RequestBody ProjectDTO dto,
                                 @PathVariable(value = "id") Long id){
        return projectService.updateProject(dto, token);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteProject(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                   @PathVariable(value = "id") Long id){

        return projectService.deleteProjectById(id, token)
                .thenReturn(new ResponseEntity<>("Project was successfully deleted!", HttpStatus.OK));
    }

    @GetMapping("/{id}/fields")
    public Flux<Field> getProjectFields(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                        @PathVariable(value = "id") Long projectId){
        return projectService.findProjectFieldsByProjectId(projectId, token);
    }

    @PutMapping("/{id}/fields/{fieldId}")
    public Mono<Field> updateField(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                       @RequestBody FieldDTO dto,
                                       @PathVariable(value = "id") Long id,
                                   @PathVariable(value = "fieldId") Long fieldId){
        return projectService.updateField(dto, token);
    }

    @PostMapping("/{id}/fields")
    public Mono<Field> addField(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                   @RequestBody FieldDTO dto,
                                   @PathVariable(value = "id") Long id){
        return projectService.addField(dto, token);
    }

    @DeleteMapping("/{id}/fields/{fieldId}")
    public Mono<ResponseEntity<String>> deleteField(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                   @PathVariable(value = "id") Long id,
                                   @PathVariable(value = "fieldId") Long fieldId){
        return projectService.deleteFieldById(fieldId, token)
                .thenReturn(new ResponseEntity<>("Field was successfully deleted!", HttpStatus.OK));
    }


    @GetMapping("/{id}/users")
    public Flux<UserDTO> getProjectUsers(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable(value = "id") Long projectId){
        return projectService.findUsersByProjectId(projectId, token);
    }
    @DeleteMapping("/{id}/users")
    public Flux<UserDTO> deleteUserFromProject(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                               @PathVariable(value = "id") Long projectId,
                                               @RequestParam(name = "username", required = true) String username){
        return projectService.deleteUserFromProject(projectId, username, token);
    }

    @GetMapping("/{id}/users/change-role")
    public Mono<Object> changeUserRole(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                          @PathVariable(value = "id") Long projectId,
                                          @RequestParam(name = "username", required = true) String username,
                                          @RequestParam(name = "role", required = true) String newRole){
        return projectService.changeUserRole(projectId, username, newRole, token);
    }

    @GetMapping("/{id}/users/invite")
    public Mono<Void> inviteUserToProject(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                          @PathVariable(value = "id") Long projectId,
                                          @RequestParam(name = "username", required = true) String username){
        return projectService.inviteUserToProject(projectId, username, token);
    }


}
