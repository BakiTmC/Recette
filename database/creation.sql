-- Dans psql sur la VM, avec un administrateur (une seule fois).
CREATE USER recettesuser WITH PASSWORD 'recettespass';
CREATE DATABASE recettesdb OWNER recettesuser;
