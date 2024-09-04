-- DROP TABLES
DROP TABLE IF EXISTS usuario CASCADE;
DROP TABLE IF EXISTS notificacion CASCADE;
DROP TABLE IF EXISTS carpeta CASCADE;
DROP TABLE IF EXISTS formulario CASCADE;
DROP TABLE IF EXISTS imagen CASCADE;
DROP TABLE IF EXISTS ocr CASCADE;

-- -- DROP SEQUENCES
-- DROP SEQUENCE IF EXISTS seq_id_carpeta;
-- DROP SEQUENCE IF EXISTS seq_id_notificacion;
-- DROP SEQUENCE IF EXISTS seq_id_formulario;
-- DROP SEQUENCE IF EXISTS seq_id_imagen;
-- DROP SEQUENCE IF EXISTS seq_id_ocr;

-- -- CREATE SEQUENCES
-- CREATE SEQUENCE id_carpeta_eq START WITH 1 INCREMENT BY 1 NO MINVALUE MAXVALUE 99999 CACHE 1;
-- CREATE SEQUENCE id_notificacion_eq START WITH 1 INCREMENT BY 1 NO MINVALUE MAXVALUE 99999 CACHE 1;
-- CREATE SEQUENCE id_formulario_eq START WITH 1 INCREMENT BY 1 NO MINVALUE MAXVALUE 99999 CACHE 1;
-- CREATE SEQUENCE id_imagen_eq START WITH 1 INCREMENT BY 1 NO MINVALUE MAXVALUE 99999 CACHE 1;
-- CREATE SEQUENCE id_ocr_eq START WITH 1 INCREMENT BY 1 NO MINVALUE MAXVALUE 99999 CACHE 1;

-- ALTER TABLE id_carpeta_eq OWNER TO hortiscan_admin;
-- ALTER TABLE id_notificacion_eq OWNER TO hortiscan_admin;
-- ALTER TABLE id_formulario_eq OWNER TO hortiscan_admin;
-- ALTER TABLE id_imagen_eq OWNER TO hortiscan_admin;
-- ALTER TABLE id_ocr_eq OWNER TO hortiscan_admin;

-- CREATE TABLES
CREATE TABLE usuario(
  id_usuario SERIAL NOT NULL CONSTRAINT pk_usuario PRIMARY KEY,
  nombre_usuario VARCHAR(20) NOT NULL,
  password_usuario VARCHAR(50) NOT NULL
);

CREATE TABLE notificacion(
  id_notificacion SERIAL NOT NULL CONSTRAINT pk_notificacion PRIMARY KEY,
  id_usuario INTEGER NOT NULL,
  mensaje_notificacion VARCHAR(300) NOT NULL,
  fecha_notificacion TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT fk_notificacion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE carpeta(
  id_carpeta SERIAL NOT NULL CONSTRAINT pk_carpeta PRIMARY KEY,
  id_usuario INTEGER NOT NULL,
  nombre_carpeta VARCHAR(30),
  ruta_carpeta VARCHAR(50),
  fecha_creacion_carpeta TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE formulario(
  id_formulario SERIAL NOT NULL CONSTRAINT pk_formulario PRIMARY KEY,
  id_usuario INTEGER NOT NULL,
  nombre_formulario VARCHAR(20),
  estado_formulario VARCHAR(20),
  CONSTRAINT fk_formulario_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE imagen(
  id_imagen SERIAL NOT NULL CONSTRAINT pk_imagen PRIMARY KEY,
  id_formulario INTEGER NOT NULL,
  id_carpeta INTEGER NOT NULL,
  ruta_almacenamiento VARCHAR(30),
  fecha_creacion_imagen TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT fk_imagen_formulario FOREIGN KEY (id_formulario) REFERENCES formulario(id_formulario),
  CONSTRAINT fk_imagen_carpeta FOREIGN KEY (id_carpeta) REFERENCES carpeta(id_carpeta)
);

CREATE TABLE ocr(
  id_ocr SERIAL NOT NULL CONSTRAINT pk_ocr PRIMARY KEY,
  id_imagen INTEGER NOT NULL,
  texto_extraido VARCHAR(1000),
  precision_ocr INTEGER,
  CONSTRAINT fk_ocr_imagen FOREIGN KEY (id_imagen) REFERENCES imagen(id_imagen)
);


-- ALTER TABLES
ALTER TABLE usuario OWNER TO hortiscan_admin;
ALTER TABLE notificacion OWNER TO hortiscan_admin;
ALTER TABLE carpeta OWNER TO hortiscan_admin;
ALTER TABLE formulario OWNER TO hortiscan_admin;
ALTER TABLE imagen OWNER TO hortiscan_admin;
ALTER TABLE ocr OWNER TO hortiscan_admin;