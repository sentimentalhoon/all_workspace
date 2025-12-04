-- 채팅 메시지 저장 테이블
CREATE TABLE IF NOT EXISTS chat_messages (
	id BIGSERIAL PRIMARY KEY,
	user_id BIGINT NOT NULL,
	display_name VARCHAR(150),
	username VARCHAR(150),
	content TEXT NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_chat_messages_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_chat_messages_created_at ON chat_messages (created_at DESC);

COMMENT ON TABLE chat_messages IS '실시간 채팅 메시지 로그';
COMMENT ON COLUMN chat_messages.user_id IS '발신자 users.id';
COMMENT ON COLUMN chat_messages.display_name IS '발신자 표시 이름 (스냅샷)';
COMMENT ON COLUMN chat_messages.username IS '발신자 Telegram username (스냅샷)';
COMMENT ON COLUMN chat_messages.content IS '메시지 본문';
COMMENT ON COLUMN chat_messages.created_at IS '메시지 작성 시각';
