package com.psmo.service

/**
 * 활동 점수와 레벨 매핑 규칙을 정의한다.
 * - 점수는 0 이상만 허용한다.
 * - 구간은 향후 운영 정책에 따라 손쉽게 변경할 수 있도록 상수 리스트로 관리한다.
 */
object ActivityLevelPolicy {
    /**
     * 레벨-최소점수 테이블. 1~100 레벨.
     * 공식: Score = 50 * (level - 1) * level
     * Level 1: 0
     * Level 2: 100
     * Level 3: 300
     * ...
     * Level 100: 495,000
     */
    private val thresholds: List<Pair<Int, Int>> = (1..100).map { level ->
        level to 50 * (level - 1) * level
    }

    fun levelForScore(score: Int): Int {
        val safeScore = score.coerceAtLeast(0)
        return thresholds
            .lastOrNull { (_, minScore) -> safeScore >= minScore }
            ?.first
            ?: 1
    }
}
