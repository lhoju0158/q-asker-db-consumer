package com.icc.db_consumer.repository;

import com.icc.db_consumer.entity.TokenUsage;
import org.springframework.data.jpa.repository.JpaRepository;

/** 토큰 사용량 read-model Repository. */
public interface TokenUsageRepository extends JpaRepository<TokenUsage, Long> {

  /** 멱등 검사: 동일 requestId가 이미 적재됐는지 확인한다(at-least-once 재전송 대비). */
  boolean existsByRequestId(String requestId);
}
