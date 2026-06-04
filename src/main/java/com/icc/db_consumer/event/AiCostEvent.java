package com.icc.db_consumer.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

/**
 * ai.cost.raw 메시지 본문. 발행 측(api quiz-cost)의 AiCostEventPayload를 컨슈머가 로컬 복제한 것이다.
 *
 * <p>발행 측과 별도 레포라 공유 모듈 없이 record를 복제한다. schemaVersion으로 양식 버전을 구분하며, 알 수 없는 필드는 무시해
 * 전방 호환을 유지한다(필드 추가가 컨슈머를 깨지 않음). status는 enum 대신 String으로 받아 신규 상태값에도 견고하다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AiCostEvent(
    int schemaVersion,
    String requestId,
    String userId,
    Long quizSetId,
    String model,
    String modelVersion,
    long inputTokens,
    long cachedTokens,
    long thinkingTokens,
    long outputTokens,
    long latencyMs,
    String status,
    String errorCode,
    Instant occurredAt) {}
