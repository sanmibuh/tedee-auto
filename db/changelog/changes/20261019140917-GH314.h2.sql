-- liquibase formatted sql

-- changeset davidbelmez:1792418964276-1 splitStatements:false
CREATE TABLE lock (id INT NOT NULL, CONSTRAINT lockPK PRIMARY KEY (id));

-- changeset davidbelmez:1792418964276-2 splitStatements:false
CREATE TABLE lock_cron_expression (lock_id INT NOT NULL, cron_expression VARCHAR(512) NOT NULL, CONSTRAINT lock_cron_expressionPK PRIMARY KEY (lock_id, cron_expression));

-- changeset davidbelmez:1792418964276-3 splitStatements:false
ALTER TABLE lock_cron_expression ADD CONSTRAINT fk_lock_cron_expression_lock FOREIGN KEY (lock_id) REFERENCES lock (id);

