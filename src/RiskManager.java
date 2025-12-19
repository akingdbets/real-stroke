public class RiskManager {

    // 생성자 (이제 아무것도 주입받을 필요 없음)
    public RiskManager() {
    }

    public Risk calculateRisk(HealthData data, Parameter param, String userId) {
        double score = calculateScore(data, param);
        String level = determineLevel(score, param);

        Risk riskResult = new Risk(score, level);

        // [핵심] 위험 상황이면 Notification 객체 생성!
        if ("위험".equals(level) || "WARNING".equals(level)) {
            String msg = "🚨 [위험 수치 감지] 총점: " + String.format("%.1f", score) + "\n"
                    + "분석 결과가 '위험'입니다. 즉시 리포트를 확인하세요.";

            // 1. Notification 객체 생성 (속성: 메시지, 타입)
            Notification noti = new Notification(msg, NotificationType.RISK);

            // 2. 알림 표시
            noti.display();
        }

        return riskResult;
    }

    // --- 기존 계산 로직 (주호님/동윤님 코드 유지) ---
    private String determineLevel(double score, Parameter param) {
        if (score >= param.getRiskThreshold()) {
            return "위험";
        } else {
            return "정상";
        }
    }

    private double calculateScore(HealthData data, Parameter param) {
        double score = 0.0;
        if (data.isSmokingStatus()) score += param.getSmokingScore();
        if (data.getBloodSugar() > param.getStdBloodSugar()) {
            score += (data.getBloodSugar() - param.getStdBloodSugar()) * param.getBloodSugarWeight();
        }
        if (data.getMaxBloodPressure() > param.getStdMaxBloodPressure()) {
            score += (data.getMaxBloodPressure() - param.getStdMaxBloodPressure()) * param.getBloodPressureWeight();
        }
        if (data.getActivityLevel() < 3) score += param.getInactivityScore();
        if (data.isMedicationTaken()) score *= 0.8;
        return Math.max(0, Math.min(100, score));
    }
}