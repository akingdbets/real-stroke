import java.time.LocalDateTime;

public class Risk {
    private LocalDateTime assessedAt; // 분석 시각
    private double totalScore;        // 위험도 점수
    private String riskLevel;         // 위험 등급 (정상/위험)

    public Risk(double totalScore, String riskLevel) {
        this.assessedAt = LocalDateTime.now();
        this.totalScore = totalScore;
        this.riskLevel = riskLevel;
    }

    @Override
    public String toString() {
        return String.format("[%s] 점수: %.1f (%s)", assessedAt, totalScore, riskLevel);
    }

    // Getters
    public double getTotalScore() { return totalScore; }
    public String getRiskLevel() { return riskLevel; }
    public LocalDateTime getAssessedAt() { return assessedAt; }
}