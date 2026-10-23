INSERT INTO lock (id, version) VALUES (42, 0);
INSERT INTO lock_cron_expression (lock_id, cron_expression)
VALUES (42, '0 0 22 * * MON-FRI'), (42, '0 30 23 * * *');
