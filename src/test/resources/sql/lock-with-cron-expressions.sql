INSERT INTO lock (id) VALUES (42);
INSERT INTO lock_cron_expression (lock_id, cron_expression)
VALUES (42, '0 0 22 * * MON-FRI'), (42, '0 30 23 * * *');
