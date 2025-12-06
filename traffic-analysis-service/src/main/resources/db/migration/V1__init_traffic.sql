CREATE TABLE traffic_logs (
                              id SERIAL PRIMARY KEY,
                              source_ip VARCHAR(50),
                              dest_ip VARCHAR(50),
                              size_bytes INT,
                              dest_port INT,
                              timestamp TIMESTAMP
);

CREATE TABLE anomaly_reports (
                                 id SERIAL PRIMARY KEY,
                                 anomaly_type VARCHAR(100),
                                 description TEXT,
                                 created_at TIMESTAMP,
                                 image_url VARCHAR(500),
                                 traffic_log_id INT REFERENCES traffic_logs(id),
                                 user_id INT -- Просто число, без REFERENCES на іншу базу
);

CREATE TABLE webhook_subscriptions (
                                       id SERIAL PRIMARY KEY,
                                       url VARCHAR(255) NOT NULL,
                                       user_id INT
);