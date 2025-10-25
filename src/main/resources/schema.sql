CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    owner_name TEXT NOT NULL,
    balance NUMERIC(12,2) NOT NULL DEFAULT 0,
    account_type TEXT NOT NULL CHECK (account_type IN ('personal','business','vip'))
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    type TEXT NOT NULL CHECK (type IN ('TRANSFER','DEPOSIT','WITHDRAW')),
    amount NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    fee NUMERIC(18,2) NOT NULL DEFAULT 0,
    from_account_id INT REFERENCES accounts(id),
    to_account_id INT REFERENCES accounts(id)
);
