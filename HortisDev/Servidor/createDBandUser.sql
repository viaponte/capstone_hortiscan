-- Creación de BD + Usuario
CREATE DATABASE bd_hortiscan;
CREATE USER hortiscan_admin WITH ENCRYPTED PASSWORD 'hortiscan_admin';
GRANT ALL PRIVILEGES ON DATABASE bd_hortiscan TO hortiscan_admin;