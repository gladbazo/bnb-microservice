CREATE TABLE dbo.currenciesJson (
        id INT PRIMARY KEY,
        gold INT,
        name VARCHAR(255) COLLATE Cyrillic_General_CI_AS,
        name_en VARCHAR(255),
        code VARCHAR(255),
        ratio DECIMAL,
        reverse_rate DECIMAL,
        rate DECIMAL,
        curr_date DATE,
        f_star INT
        );

CREATE TABLE dbo.currencies (
        id INT PRIMARY KEY,
        gold INT,
        name VARCHAR(255) COLLATE Cyrillic_General_CI_AS,
        name_en VARCHAR(255),
        code VARCHAR(255),
        ratio DECIMAL,
        reverse_rate DECIMAL,
        rate DECIMAL,
        curr_date DATE,
        f_star INT
        );