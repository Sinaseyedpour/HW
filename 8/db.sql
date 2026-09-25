-- ============================================================
--  SMS Panel Subscription Management System - database schema
--  PostgreSQL
-- ============================================================

DROP DATABASE IF EXISTS sms_panel;
CREATE DATABASE sms_panel;
\connect sms_panel;

-- ------------------------------------------------------------
-- Table: users
-- The accounts of the SMS panel.
-- ------------------------------------------------------------
CREATE TABLE users
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(100)  NOT NULL,
    username          VARCHAR(50)   NOT NULL UNIQUE,
    password          VARCHAR(255)  NOT NULL,
    credit            NUMERIC(15, 2) NOT NULL DEFAULT 0,
    registration_date TIMESTAMP     NOT NULL DEFAULT NOW(),
    status            VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'DEACTIVATED'))
);

-- ------------------------------------------------------------
-- Table: transactions
-- Every credit movement of every account.
-- ------------------------------------------------------------
CREATE TABLE transactions
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    amount           NUMERIC(15, 2) NOT NULL,
    type             VARCHAR(30)    NOT NULL
        CHECK (type IN ('GIFT', 'CREDIT_INCREASE', 'DEBIT')),
    description      VARCHAR(255),
    transaction_date TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transactions_user_id ON transactions (user_id);

-- ------------------------------------------------------------
-- Relationships
--   one user  ->  many transactions  (1 : N)
-- ------------------------------------------------------------
