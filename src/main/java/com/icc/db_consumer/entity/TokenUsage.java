package com.icc.db_consumer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 호출 토큰 사용량 read-model. ai.cost.raw 컨슈머가 호출 1건 = 1행으로 멱등 적재한다(request_id UNIQUE). 사용자별 사용량은
 * (user_id, occurred_at) 인덱스를 이용한 SUM(total_tokens) 조회로 산출한다. ddl-auto=validate이므로 컬럼은 V1
 * 마이그레이션과 정확히 일치시킨다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "token_usage")
public class TokenUsage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 호출 단위 멱등 키 (발행 측 requestId) */
  @Column(nullable = false, unique = true, columnDefinition = "CHAR(36)")
  private String requestId;

  /** 과금/추적 주체 (로그인 userId 또는 비로그인 clientId/anon) */
  @Column(nullable = false)
  private String userId;

  private Long quizSetId;

  @Column(nullable = false)
  private String model;

  private String modelVersion;

  @Column(nullable = false)
  private long inputTokens;

  @Column(nullable = false)
  private long cachedTokens;

  @Column(nullable = false)
  private long thinkingTokens;

  @Column(nullable = false)
  private long outputTokens;

  /** 토큰 4종 합계 (사용량 SUM 조회 편의를 위해 적재 시점에 저장) */
  @Column(nullable = false)
  private long totalTokens;

  /** 호출 결과 상태 (SUCCESS/ERROR 등, 신규값 대비 String 보관) */
  @Column(nullable = false)
  private String status;

  /** 실패 시 오류 코드 (성공 시 null) */
  private String errorCode;

  /** 호출 발생 시각 (발행 측 기준) */
  @Column(nullable = false)
  private Instant occurredAt;

  /** 컨슈머 적재 시각 */
  @Column(nullable = false)
  private Instant consumedAt;
}
