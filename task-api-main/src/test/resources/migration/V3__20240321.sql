CREATE TABLE field (
    id  SERIAL PRIMARY KEY,
    name   VARCHAR(64)   NOT NULL,
    project_id INT,
    FOREIGN KEY (project_id) REFERENCES project(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE task (
    id  SERIAL PRIMARY KEY,
    name   VARCHAR(64)   NOT NULL,
    description VARCHAR(512),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    project_id INT,
    field_id INT,
    FOREIGN KEY (project_id) REFERENCES project(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (field_id) REFERENCES field(id) ON UPDATE CASCADE ON DELETE CASCADE
);
INSERT INTO "field" (name, project_id)
VALUES ('field1', 1),
       ('field2', 2),
       ('field3', 4);

INSERT INTO "task" (name, project_id,field_id , created_at, updated_at)
VALUES ('task1', 1,1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
       ('task2', 2,2,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

