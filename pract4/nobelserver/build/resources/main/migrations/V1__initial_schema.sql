CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prizes (
    id SERIAL PRIMARY KEY,
    award_year INTEGER NOT NULL,
    category VARCHAR(50) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    motivation TEXT,
    detail_link VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(award_year, category)
);

CREATE TABLE IF NOT EXISTS laureates (
    id SERIAL PRIMARY KEY,
    prize_id INTEGER NOT NULL REFERENCES prizes(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    portion VARCHAR(10) DEFAULT '1',
    motivation TEXT,
    portrait_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(prize_id, full_name)
);

CREATE TABLE IF NOT EXISTS user_prizes (
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    prize_id INTEGER NOT NULL REFERENCES prizes(id) ON DELETE CASCADE,
    added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(user_id, prize_id)
);

CREATE INDEX IF NOT EXISTS idx_prizes_year_category ON prizes(award_year, category);
CREATE INDEX IF NOT EXISTS idx_laureates_prize_id ON laureates(prize_id);
CREATE INDEX IF NOT EXISTS idx_user_prizes_user_id ON user_prizes(user_id);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_prizes_updated_at BEFORE UPDATE ON prizes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();