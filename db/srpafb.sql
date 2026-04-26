CREATE DATABASE srpafb;
USE srpafb;

CREATE TABLE categoria (
   id INT AUTO_INCREMENT PRIMARY KEY,
   nombre VARCHAR(50) NOT NULL
);

CREATE TABLE persona (
   id INT AUTO_INCREMENT PRIMARY KEY,
   nombre VARCHAR(50),
   apellido VARCHAR(50),
   grado VARCHAR(50),
   dni VARCHAR(20),
   fecha_nac DATE,
   sexo ENUM('M','F'),
   peso DOUBLE,
   talla DOUBLE,
   categoria_id INT,
   FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE prueba (
   id INT AUTO_INCREMENT PRIMARY KEY,
   nombre VARCHAR(50),
   unidad VARCHAR(20)
);

CREATE TABLE resultado (
   id INT AUTO_INCREMENT PRIMARY KEY,
   persona_id INT,
   prueba_id INT,
   valor DOUBLE,
   anio INT,
   puntaje INT,
   FOREIGN KEY (persona_id) REFERENCES persona(id),
   FOREIGN KEY (prueba_id) REFERENCES prueba(id)
);

CREATE TABLE baremo (
   id INT AUTO_INCREMENT PRIMARY KEY,
   prueba_id INT,
   categoria_id INT,
   genero ENUM('M','F'),
   valor_min DOUBLE,
   valor_max DOUBLE,
   puntaje INT,
   FOREIGN KEY (prueba_id) REFERENCES prueba(id),
   FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);
