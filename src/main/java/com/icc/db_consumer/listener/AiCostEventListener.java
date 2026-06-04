package com.icc.db_consumer.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icc.db_consumer.event.AiCostEvent;
import com.icc.db_consumer.service.TokenUsageSink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * ai.cost.raw 토픽 구독자. JSON 문자열 메시지를 AiCostEvent로 역직렬화해 read-model에 적재한다.
 *
 * <p>역직렬화 실패(포이즌 메시지)는 예외를 던져 재시도되며, 재시도 소진 시 기본 에러 핸들러가 로그 후 스킵한다. DLQ는 추후(Phase 2) 도입한다.
 * 멱등 적재는 TokenUsageSink가 담당하므로 at-least-once 재처리는 안전하다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiCostEventListener {

  /** 현재 지원하는 메시지 양식 버전 */
  private static final int SUPPORTED_SCHEMA_VERSION = 1;

  private final ObjectMapper objectMapper;
  private final TokenUsageSink sink;

  @KafkaListener(topics = "${ai.cost.topic}")
  public void onMessage(String payload) {
    AiCostEvent event;
    try {
      event = objectMapper.readValue(payload, AiCostEvent.class);
    } catch (Exception e) {
      log.error("[ai.cost.raw 역직렬화 실패] payload={}", payload, e);
      throw new IllegalStateException("ai.cost.raw 역직렬화 실패", e);
    }

    if (event.schemaVersion() != SUPPORTED_SCHEMA_VERSION) {
      log.warn(
          "[미지원 schemaVersion={}] requestId={} — 우선 적재 진행",
          event.schemaVersion(),
          event.requestId());
    }

    sink.persist(event);
  }
}
