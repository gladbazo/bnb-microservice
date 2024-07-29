IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'currencies' AND TABLE_SCHEMA = 'dbo')
    CREATE TABLE currencies (
        gold BIGINT,
        name VARCHAR(255),
        name_en VARCHAR(255),
        code VARCHAR(255) PRIMARY KEY,
        ratio DECIMAL(19, 4),
        reverse_rate DECIMAL(19, 4),
        rate DECIMAL(19, 4),
        curr_date DATE,
        f_star INT
    );
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'currenciesJson' AND TABLE_SCHEMA = 'dbo')
    CREATE TABLE currenciesJson (
        gold BIGINT,
        name VARCHAR(255),
        name_en VARCHAR(255),
        code VARCHAR(255) PRIMARY KEY,
        ratio DECIMAL(19, 4),
        reverse_rate DECIMAL(19, 4),
        rate DECIMAL(19, 4),
        curr_date DATE,
        f_star INT
    );


