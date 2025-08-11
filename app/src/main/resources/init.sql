-- data base
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  user_name VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  role VARCHAR(10) NOT NULL DEFAULT 'CLIENT' CHECK (role IN ('CLIENT', 'ADMIN')),
  is_active BOOLEAN NOT NULL DEFAULT FALSE,
  verify_token VARCHAR(255),
  avatar TEXT,
  created_at DATE NOT NULL,
  otp VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS boards (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  owner_id UUID NOT NULL,
  created_at DATE NOT NULL,
  CONSTRAINT fk_boards_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS columns (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(255) NOT NULL,
  created_at DATE NOT NULL,
  board_id UUID NOT NULL,
  CONSTRAINT fk_columns_board FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cards (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(255) NOT NULL,
  description TEXT,
  is_done BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATE NOT NULL,
  deadline TIMESTAMP,
  column_id UUID NOT NULL,
  CONSTRAINT fk_cards_column FOREIGN KEY (column_id) REFERENCES columns(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS board_members (
  board_id UUID NOT NULL,
  user_id UUID NOT NULL,
  joined_at TIMESTAMP NOT NULL,
  PRIMARY KEY (board_id, user_id),
  CONSTRAINT fk_board_members_board FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
  CONSTRAINT fk_board_members_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS card_comments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  card_id UUID NOT NULL,
  user_id UUID NOT NULL,
  user_avatar_url TEXT,
  content TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  CONSTRAINT fk_card_comments_card FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE,
  CONSTRAINT fk_card_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS invitation (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  board_id UUID NOT NULL,
  invited_user_id UUID NOT NULL,
  inviter_user_id UUID NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'reject', 'accept')),
  sent_at TIMESTAMP NOT NULL,
  responded_at TIMESTAMP,
  CONSTRAINT fk_invitation_board FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
  CONSTRAINT fk_invitation_invited_user FOREIGN KEY (invited_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_invitation_inviter_user FOREIGN KEY (inviter_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT chk_invited_not_inviter CHECK (invited_user_id <> inviter_user_id)
);

