-- AI 호출 토큰 사용량 read-model (token_usage)
-- ai.cost.raw 컨슈머가 호출 1건 = 1행으로 멱등 적재한다(request_id UNIQUE = at-least-once 재전송 차단).
-- 사용자별 사용량 = (user_id, occurred_at) 인덱스 기반 SUM(total_tokens) 조회로 산출한다.
CREATE TABLE token_usage
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id      CHAR(36)     NOT NULL,
    user_id         VARCHAR(255) NOT NULL,
    quiz_set_id     BIGINT       NULL,
    model           VARCHAR(255) NOT NULL,
    model_version   VARCHAR(255) NULL,
    input_tokens    BIGINT       NOT NULL,
    cached_tokens   BIGINT       NOT NULL,
    thinking_tokens BIGINT       NOT NULL,
    output_tokens   BIGINT       NOT NULL,
    total_tokens    BIGINT       NOT NULL,
    status          VARCHAR(255) NOT NULL,
    error_code      VARCHAR(255) NULL,
    occurred_at     DATETIME(6)  NOT NULL,
    consumed_at     DATETIME(6)  NOT NULL,
    -- 멱등 보장: 동일 호출(requestId) 중복 적재 차단
    UNIQUE KEY uk_token_usage_request (request_id),
    -- 사용자별 기간 사용량 집계(SUM) 조회 대비 인덱스
    INDEX idx_token_usage_user_occurred (user_id, occurred_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
