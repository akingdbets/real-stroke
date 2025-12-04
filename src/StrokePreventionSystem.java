import javax.swing.SwingUtilities;

public class StrokePreventionSystem {

    public static void main(String[] args) {
        // --- 파라미터 튜닝 예시 ---
        // 1. 흡연: 피우면 무조건 20점
        // 2. 혈당 가중치: 1당 0.5점 (예: 기준보다 40 높으면 20점)
        // 3. 혈압 가중치: 1당 1.0점 (예: 기준보다 20 높으면 20점)
        // 4. 운동부족: 10점
        // 5. 기준값: 혈당 126, 혈압 140
        // 6. 위험 총점 기준: 60점 이상이면 알림

        Parameter params = new Parameter(
                20.0,   // smokingScore
                0.5,    // bloodSugarWeight (곱해질 값이라 작게 설정)
                1.0,    // bloodPressureWeight (혈압이 더 치명적이므로 높게)
                10.0,   // inactivityScore
                126.0f, // stdBloodSugar
                140,    // stdMaxBloodPressure
                60.0    // totalRiskThreshold
        );

        NotificationService notiService = new ConsoleNotificationService();
        RiskManager riskManager = new RiskManager(notiService);
        Patient patient = new Patient("user001", "이순신");

        System.out.println("[System] 시스템을 구동합니다.");

        // 2. 메인 대시보드 실행
        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard(patient, riskManager, params);
            dashboard.setVisible(true);
        });
    }
}