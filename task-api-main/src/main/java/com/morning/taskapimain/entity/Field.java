package com.morning.taskapimain.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("field")
public class Field {
    @Id
    @Column(name = "id")
    private Long id;
    private String name;
    private Long projectId;

    public static Mono<Field> fromMap(Map<String, Object> map) {
        return Mono.just(Field
                .builder()
                .id(Long.valueOf(map.get("id").toString()))
                .name((String) map.get("name"))
                .projectId(Long.valueOf(map.get("project_id").toString()))
                .build());
    }
}
