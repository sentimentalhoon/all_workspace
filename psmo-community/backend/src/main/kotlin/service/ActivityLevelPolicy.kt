package com.psmo.service

/**
 * 활동 점수와 레벨 매핑 규칙을 정의한다.
 * - 점수는 0 이상만 허용한다.
 * - 구간은 향후 운영 정책에 따라 손쉽게 변경할 수 있도록 상수 리스트로 관리한다.
 */
object ActivityLevelPolicy {
    /**
     * 레벨-최소점수 테이블. 오름차순 정렬 필수.
     */
    private val thresholds: List<Pair<Int, Int>> = listOf(
        1 to 0,
        2 to 100,
        3 to 300,
        4 to 700,
        5 to 1500
    )

    fun levelForScore(score: Int): Int {
        val safeScore = score.coerceAtLeast(0)
        return thresholds
            .lastOrNull { (_, minScore) -> safeScore >= minScore }
            ?.first
            ?: 1
    }
}
