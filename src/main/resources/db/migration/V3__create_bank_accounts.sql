
CREATE TABLE bank_accounts (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               account_number VARCHAR(12) NOT NULL,
                               customer_id BIGINT NOT NULL,
                               account_type VARCHAR(20) NOT NULL,
                               balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
                               currency VARCHAR(3) NOT NULL DEFAULT 'INR',
                               status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                               version BIGINT NOT NULL DEFAULT 0,
                               created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                               updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                   ON UPDATE CURRENT_TIMESTAMP(6),

                               CONSTRAINT pk_bank_accounts PRIMARY KEY (id),
                               CONSTRAINT uk_bank_accounts_account_number UNIQUE (account_number),
                               CONSTRAINT fk_bank_accounts_customer
                                   FOREIGN KEY (customer_id) REFERENCES customers(id),
                               CONSTRAINT chk_bank_accounts_balance CHECK (balance >= 0),
                               CONSTRAINT chk_bank_accounts_type
                                   CHECK (account_type IN ('SAVINGS', 'CURRENT')),
                               CONSTRAINT chk_bank_accounts_status
                                   CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),

                               INDEX idx_bank_accounts_customer_id (customer_id)
);
