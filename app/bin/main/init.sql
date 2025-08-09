CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    name VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,

    role CHAR(6) NOT NULL DEFAULT 'CLIENT' 
        CHECK (role IN ('CLIENT', 'ADMIN')),

    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    
    verify_token UUID DEFAULT gen_random_uuid()
);

