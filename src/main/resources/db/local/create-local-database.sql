SELECT 'CREATE DATABASE api_usuario_local'
WHERE NOT EXISTS (
    SELECT 1
    FROM pg_database
    WHERE datname = 'api_usuario_local'
)\gexec

\connect api_usuario_local

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    usuario VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(100) NOT NULL,
    CONSTRAINT uk_usuarios_usuario UNIQUE (usuario),
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT ck_usuarios_nombre_length CHECK (char_length(trim(nombre)) BETWEEN 2 AND 50),
    CONSTRAINT ck_usuarios_usuario_length CHECK (char_length(trim(usuario)) BETWEEN 2 AND 20),
    CONSTRAINT ck_usuarios_email_format CHECK (email LIKE '%@%.%'),
    CONSTRAINT ck_usuarios_password_length CHECK (char_length(password) BETWEEN 5 AND 100)
);

CREATE TABLE IF NOT EXISTS tarea (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    descripcion VARCHAR(255),
    completada BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_tarea_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id)
        ON DELETE CASCADE,
    CONSTRAINT uk_tarea_usuario_descripcion UNIQUE (usuario_id, descripcion)
);

CREATE TABLE IF NOT EXISTS sueno (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_dormir TIME NOT NULL,
    hora_despertar TIME NOT NULL,
    horas_dormidas DOUBLE PRECISION NOT NULL DEFAULT 0,
    calidad INTEGER NOT NULL,
    CONSTRAINT fk_sueno_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id)
        ON DELETE CASCADE,
    CONSTRAINT ck_sueno_horas_dormidas CHECK (horas_dormidas >= 0),
    CONSTRAINT ck_sueno_calidad CHECK (calidad BETWEEN 1 AND 5)
);

CREATE TABLE IF NOT EXISTS evento (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    evento VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    fecha DATE NOT NULL,
    CONSTRAINT fk_evento_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id)
        ON DELETE CASCADE,
    CONSTRAINT ck_evento_nombre_length CHECK (char_length(trim(evento)) BETWEEN 5 AND 100),
    CONSTRAINT ck_evento_descripcion_length CHECK (char_length(trim(descripcion)) BETWEEN 5 AND 255)
);

CREATE INDEX IF NOT EXISTS idx_tarea_usuario_id ON tarea (usuario_id);
CREATE INDEX IF NOT EXISTS idx_sueno_usuario_id ON sueno (usuario_id);
CREATE INDEX IF NOT EXISTS idx_sueno_usuario_fecha ON sueno (usuario_id, fecha);
CREATE INDEX IF NOT EXISTS idx_evento_usuario_id ON evento (usuario_id);
CREATE INDEX IF NOT EXISTS idx_evento_usuario_fecha ON evento (usuario_id, fecha);

CREATE OR REPLACE VIEW vista_resumen_usuario AS
SELECT
    u.id AS usuario_id,
    u.nombre,
    COUNT(DISTINCT t.id) AS tareas_totales,
    COUNT(DISTINCT t.id) FILTER (WHERE t.completada = TRUE) AS tareas_completadas,
    COUNT(DISTINCT t.id) FILTER (WHERE t.completada = FALSE) AS tareas_pendientes,
    CASE
        WHEN COUNT(DISTINCT t.id) = 0 THEN 0
        ELSE ROUND(
            (
                COUNT(DISTINCT t.id) FILTER (WHERE t.completada = TRUE)::NUMERIC
                * 100
                / COUNT(DISTINCT t.id)
            ),
            2
        )
    END AS progreso_tareas,
    COALESCE(ROUND(AVG(s.horas_dormidas)::NUMERIC, 2), 0) AS promedio_sueno,
    COALESCE(ROUND(AVG(s.calidad)::NUMERIC, 2), 0) AS calidad_promedio
FROM usuarios u
LEFT JOIN tarea t ON t.usuario_id = u.id
LEFT JOIN sueno s ON s.usuario_id = u.id
GROUP BY u.id, u.nombre;
