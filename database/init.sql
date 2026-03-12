CREATE DATABASE IF NOT EXISTS flexspace;

CREATE USER IF NOT EXISTS 'flexuser'@'localhost'
IDENTIFIED BY 'flexpass123';

GRANT ALL PRIVILEGES ON flexspace.* TO 'flexuser'@'localhost';

FLUSH PRIVILEGES;