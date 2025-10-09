-- 角色表
CREATE TABLE "role"
(
    id           BIGINT PRIMARY KEY,
    create_time  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_by    VARCHAR(100),
    update_time  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_by    VARCHAR(100),
    status       VARCHAR(20)                 DEFAULT 'active',
    del_flag     BOOLEAN      NOT NULL       DEFAULT FALSE,

    role_name    VARCHAR(100) NOT NULL UNIQUE,
    description  TEXT,
    default_flag BOOLEAN      NOT NULL       DEFAULT FALSE
);

COMMENT ON TABLE "role" IS '角色表 - 存储自定义角色性格特征';
COMMENT ON COLUMN "role".id IS '主键，唯一标识';
COMMENT ON COLUMN "role".create_time IS '数据创建时间';
COMMENT ON COLUMN "role".create_by IS '数据创建人';
COMMENT ON COLUMN "role".update_time IS '数据更新时间';
COMMENT ON COLUMN "role".update_by IS '数据更新人';
COMMENT ON COLUMN "role".status IS '数据状态';
COMMENT ON COLUMN "role".del_flag IS '逻辑删除状态';
COMMENT ON COLUMN "role".role_name IS '角色名称';
COMMENT ON COLUMN "role".description IS '角色描述，用于详细说明该角色的性格特征';
COMMENT ON COLUMN "role".default_flag IS '是否全局默认角色';


-- 项目表
CREATE TABLE project
(
    id           BIGINT PRIMARY KEY,
    create_time  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_by    VARCHAR(100),
    update_time  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_by    VARCHAR(100),
    status       VARCHAR(20)                 DEFAULT 'active',
    del_flag     BOOLEAN      NOT NULL       DEFAULT FALSE,

    project_name VARCHAR(200) NOT NULL,
    role_id      INTEGER      NOT NULL,
    CONSTRAINT fk_project_role FOREIGN KEY (role_id) REFERENCES "role" (id)
);

COMMENT ON TABLE project IS '项目表 - 存储项目信息，便于进行分组、分类管理';
COMMENT ON COLUMN project.id IS '主键，唯一标识';
COMMENT ON COLUMN project.create_time IS '数据创建时间';
COMMENT ON COLUMN project.create_by IS '数据创建人';
COMMENT ON COLUMN project.update_time IS '数据更新时间';
COMMENT ON COLUMN project.update_by IS '数据更新人';
COMMENT ON COLUMN project.status IS '数据状态';
COMMENT ON COLUMN project.del_flag IS '逻辑删除状态';
COMMENT ON COLUMN project.project_name IS '项目名称';
COMMENT ON COLUMN project.role_id IS '关联的角色，项目的默认角色';


-- 会话表
CREATE TABLE session
(
    id            SERIAL PRIMARY KEY,
    create_time   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_by     VARCHAR(100),
    update_time   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_by     VARCHAR(100),
    status        VARCHAR(20)                 DEFAULT 'active',
    del_flag      BOOLEAN NOT NULL            DEFAULT FALSE,

    project_id    INTEGER, -- 可为空，表示全局会话
    role_id       INTEGER NOT NULL,
    session_title VARCHAR(255),
    CONSTRAINT fk_session_project FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE SET NULL,
    CONSTRAINT fk_session_role FOREIGN KEY (role_id) REFERENCES "role" (id)
);

COMMENT ON TABLE session IS '会话表 - 存储会话信息';
COMMENT ON COLUMN session.id IS '主键，唯一标识';
COMMENT ON COLUMN session.create_time IS '数据创建时间';
COMMENT ON COLUMN session.create_by IS '数据创建人';
COMMENT ON COLUMN session.update_time IS '数据更新时间';
COMMENT ON COLUMN session.update_by IS '数据更新人';
COMMENT ON COLUMN session.status IS '数据状态';
COMMENT ON COLUMN session.del_flag IS '逻辑删除状态';
COMMENT ON COLUMN session.project_id IS '项目id，用于关联项目（项目会话），可为空（全局会话）';
COMMENT ON COLUMN session.role_id IS '关联的角色，会话使用的角色';
COMMENT ON COLUMN session.session_title IS '会话标题';


-- 消息历史表
CREATE TABLE message
(
    id          SERIAL PRIMARY KEY,
    create_time TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_by   VARCHAR(100),
    update_time TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(100),
    status      VARCHAR(20)                 DEFAULT 'active',
    del_flag    BOOLEAN     NOT NULL        DEFAULT FALSE,

    session_id  INTEGER     NOT NULL,
    role        VARCHAR(20) NOT NULL,
    content     TEXT        NOT NULL,
    CONSTRAINT fk_message_session FOREIGN KEY (session_id) REFERENCES session (id) ON DELETE CASCADE
);

COMMENT ON TABLE message IS '消息历史表 - 存储用户与助手的对话记录';
COMMENT ON COLUMN message.id IS '主键，唯一标识';
COMMENT ON COLUMN message.create_time IS '数据创建时间';
COMMENT ON COLUMN message.create_by IS '数据创建人';
COMMENT ON COLUMN message.update_time IS '数据更新时间';
COMMENT ON COLUMN message.update_by IS '数据更新人';
COMMENT ON COLUMN message.status IS '数据状态';
COMMENT ON COLUMN message.del_flag IS '逻辑删除状态';
COMMENT ON COLUMN message.session_id IS '关联 session 表中的会话 ID';
COMMENT ON COLUMN message.role IS '消息发送者角色（例如：user、assistant）';
COMMENT ON COLUMN message.content IS '消息内容';