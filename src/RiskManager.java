public class RiskManager {
    private NotificationService notificationService;

    public RiskManager(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public Risk calculateRisk(HealthData data, Parameter param, String userId) {
        double score = calculateScore(data, param);

        String level = determineLevel(score, param);

        Risk riskResult = new Risk(score, level);

        if ("위험".equals(level)) {
            String msg = String.format("위험도(%.1f)가 임계치를 초과했습니다! 정밀 검사가 필요합니다.", score);
            notificationService.sendAlert(userId, msg);
        }

        return riskResult;
    }
    // 등급 판단 로직
    private String determineLevel(double score, Parameter param) {
        if (score >= param.getRiskThreshold()) {
            return "위험";
        } else {
            return "정상";
        }
    }

    // [핵심 변경] 수치 비례 계산 로직
    private double calculateScore(HealthData data, Parameter param) {
        double score = 0.0;

        // 1. 흡연 (여전히 고정 점수 부여가 적합)
        if (data.isSmokingStatus()) {
            score += param.getSmokingScore();
        }

        // 2. 혈당: (내 혈당 - 기준치) * 가중치
        // 예: 내 혈당 150, 기준 126, 가중치 0.5 => (150 - 126) * 0.5 = 12점 추가
        if (data.getBloodSugar() > param.getStdBloodSugar()) {
            double excess = data.getBloodSugar() - param.getStdBloodSugar();
            score += excess * param.getBloodSugarWeight();
        }

        // 3. 혈압: (내 혈압 - 기준치) * 가중치
        // 예: 내 혈압 160, 기준 140, 가중치 1.0 => (160 - 140) * 1.0 = 20점 추가
        if (data.getMaxBloodPressure() > param.getStdMaxBloodPressure()) {
            double excess = data.getMaxBloodPressure() - param.getStdMaxBloodPressure();
            score += excess * param.getBloodPressureWeight();
        }

        // 4. 활동량 (운동 부족 시 고정 점수)
        if (data.getActivityLevel() < 3) {
            score += param.getInactivityScore();
        }

        // 5. 약물 복용 시 보정 (전체 위험도 20% 감소)
        if (data.isMedicationTaken()) {
            score *= 0.8;
        }

        return Math.max(0, Math.min(100, score));
    }
}