
CREATE TABLE banking_users (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               email VARCHAR(254) NOT NULL,
                               password_hash VARCHAR(100) NOT NULL,
                               role VARCHAR(30) NOT NULL DEFAULT 'CUSTOMER',
                               enabled BOOLEAN NOT NULL DEFAULT TRUE,
                               created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                               CONSTRAINT pk_banking_users PRIMARY KEY (id),
                               CONSTRAINT uk_banking_users_email UNIQUE (email),

                               CONSTRAINT chk_banking_users_role
                                   CHECK (role IN ('CUSTOMER', 'TELLER', 'SUPPORT', 'ADMIN', 'COMPLIANCE'))
);
