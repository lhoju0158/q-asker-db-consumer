package com.icc.db_consumer.service;

import com.icc.db_consumer.entity.TokenUsage;
import com.icc.db_consumer.event.AiCostEvent;
import com.icc.db_consumer.repository.TokenUsageRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ai.cost.raw 이벤트를 토큰 사용량 read-model(token_usage)에 멱등 적재한다.
 *
 * <p>at-least-once 전송을 대비해 requestId 기준으로 이미 적재된 건은 건너뛴다(existsByRequestId 단락 + DB UNIQUE 백업).
 * total_tokens는 토큰 4종 합으로 적재 시점에 계산해 저장한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenUsageSink {

  private final TokenUsageRepository repository;

  @Transactional
  public void persist(AiCostEvent event) {
    if (repository.existsByRequestId(event.requestId())) {
      log.debug("[중복 스킵] 이미 적재된 requestId={}", event.requestId());
      return;
    }

    long totalTokens =
        event.inputTokens()
            + event.cachedTokens()
            + event.thinkingTokens()
            + event.outputTokens();

    repository.save(
        TokenUsage.builder()
            .requestId(event.requestId())
            .userId(event.userId())
            .quizSetId(event.quizSetId())
            .model(event.model())
            .modelVersion(event.modelVersion())
            .inputTokens(event.inputTokens())
            .cachedTokens(event.cachedTokens())
            .thinkingTokens(event.thinkingTokens())
            .outputTokens(event.outputTokens())
            .totalTokens(totalTokens)
            .status(event.status())
            .errorCode(event.errorCode())
            .occurredAt(event.occurredAt())
            .consumedAt(Instant.now())
            .build());
  }
}
