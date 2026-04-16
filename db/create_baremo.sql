CREATE TABLE IF NOT EXISTS sexo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS baremo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prueba_id INT NOT NULL,
    categoria_id INT NOT NULL,
    sexo_id INT NOT NULL,
    min_valor DOUBLE NOT NULL,
    max_valor DOUBLE NOT NULL,
    puntaje DOUBLE NOT NULL,
    INDEX idx_baremo_prueba_categoria_sexo (prueba_id, categoria_id, sexo_id),
    CONSTRAINT fk_baremo_prueba FOREIGN KEY (prueba_id) REFERENCES prueba(id),
    CONSTRAINT fk_baremo_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    CONSTRAINT fk_baremo_sexo FOREIGN KEY (sexo_id) REFERENCES sexo(id)
);
