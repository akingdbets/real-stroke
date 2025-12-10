import javax.swing.*;
import java.util.UUID;

public class StrokePreventionSystem {

    public static void main(String[] args) {
        // 1. 파라미터 설정 (기존과 동일)
        Parameter params = new Parameter(20.0, 0.5, 1.0, 10.0, 126.0f, 140, 60.0);
        NotificationService notiService = new ConsoleNotificationService();
        RiskManager riskManager = new RiskManager(notiService);

        // 2. 주치의 생성
        Doctor doctor = new Doctor("doc001", "김닥터");

        // 3. 테스트 데이터 생성 (InitDb 역할)
        // 환자 A (고위험)
        Patient p1 = new Patient("user001", "홍길동");
        HealthData h1 = new HealthData(UUID.randomUUID().toString(), "user001",
                true, 150.0f, "비만", 1, 160); // 고혈압, 고혈당, 흡연
        p1.inputHealthData(h1, riskManager); // 분석 실행 및 저장
        p1.performRiskAnalysis(riskManager, params);
        doctor.addPatient(p1);

        // 환자 B (정상)
        Patient p2 = new Patient("user002", "이순신");
        HealthData h2 = new HealthData(UUID.randomUUID().toString(), "user002",
                false, 90.0f, "정상", 4, 110); // 정상 수치
        p2.inputHealthData(h2, riskManager);
        p2.performRiskAnalysis(riskManager, params);
        doctor.addPatient(p2);

        // 환자 C (데이터 없음)
        Patient p3 = new Patient("user003", "강감찬");
        doctor.addPatient(p3);

        System.out.println("[System] 주치의 및 환자 데이터 초기화 완료.");

        // 4. GUI 실행 (주치의 패널 & 환자 대시보드 동시 실행 for Test)
        SwingUtilities.invokeLater(() -> {
            // (A) 주치의 패널 실행
            DoctorDashboard doctorDashboard = new DoctorDashboard(doctor);
            doctorDashboard.setLocation(100, 100);
            doctorDashboard.setVisible(true);

            // (B) 환자(홍길동) 대시보드도 같이 띄우기 (테스트용)
            MainDashboard patientDashboard = new MainDashboard(p1, riskManager, params);
            patientDashboard.setLocation(750, 100);
            patientDashboard.setVisible(true);
        });
    }
}