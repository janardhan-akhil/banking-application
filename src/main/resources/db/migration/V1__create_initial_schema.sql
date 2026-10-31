
CREATE TABLE customers (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           customer_reference VARCHAR(36) NOT NULL,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email VARCHAR(254) NOT NULL,
                           phone_number VARCHAR(20),
                           status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                           created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                           updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                               ON UPDATE CURRENT_TIMESTAMP(6),

                           CONSTRAINT pk_customers PRIMARY KEY (id),
                           CONSTRAINT uk_customers_reference UNIQUE (customer_reference),
                           CONSTRAINT uk_customers_email UNIQUE (email),
                           CONSTRAINT chk_customers_status
                               CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'CLOSED'))
);

CREATE INDEX idx_customers_status_created
    ON customers (status, created_at);
