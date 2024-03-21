package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.Task;
import com.morning.taskapimain.repository.ProjectRepository;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final DatabaseClient client;
    private final ProjectRepository projectRepository;
    private final JwtService jwtService;
    private static final String SELECT_QUERY =     """
    select t.id, t.name, t.description, t.status, t.created_at, t.updated_at, t.project_id, t.field_id from task  as t
    join project on t.project_id=project.id
    join field on t.field_id=field.id
    """;
    private static final String FULL_SELECT_QUERY =     """
    select t.id, t.name, t.description, t.status, t.created_at, t.updated_at, t.project_id, t.field_id from task  as t
    join project on t.project_id=project.id
    join field on t.field_id=field.id
    join user_project on project.id=user_project.project_id
    join users on user_project.user_id=users.id
    """;
    private static final String CHECK_TASK_VISIBILITY =     """
    select project.visibility from task as t
    join project on t.project_id=project.id
    join field on t.field_id=field.id
    """;

    public Mono<Task> getTaskById(Long id) {
        String query = String.format("%s WHERE t.id = %s", SELECT_QUERY, id);
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(Task::fromMap);
    }
    public Mono<Task> getTaskByIdCheckingOwner(Long id, String token) {
        String username = token != null ?
                jwtService.extractUsername(token) :
                "";
        return isOpenTaskById(id).flatMap(aBoolean -> {
            if(aBoolean){
                return getTaskById(id);
            }
            String query = String.format(
                    "%s WHERE t.id = %s AND users.username = '%s'",
                    FULL_SELECT_QUERY, id, username);
            return client.sql(query)
                    .fetch()
                    .first()
                    .flatMap(Task::fromMap);
        });
    }

    private Mono<Boolean> isOpenTaskById(Long id){
        String query = String.format("%s WHERE t.id = %s", CHECK_TASK_VISIBILITY, id);
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(stringObjectMap -> {
                    return stringObjectMap.get("visibility").equals("OPEN") ?
                            Mono.just(true) :
                            Mono.just(false);
                });
    }
}
