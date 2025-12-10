import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StrokePreventionSystem {

    public static void main(String[] args) {
        // 1. 설정 및 매니저 초기화
        Parameter params = new Parameter(20.0, 0.5, 1.0, 10.0, 126.0f, 140, 60.0);
        NotificationService notiService = new ConsoleNotificationService();
        RiskManager riskManager = new RiskManager(notiService);

        // 2. 데이터베이스 역할을 할 리스트 생성
        List<Doctor> doctorList = new ArrayList<>();
        List<Patient> patientList = new ArrayList<>();

        // 3. 주치의 데이터 생성 (생년월일 추가됨)
        Doctor d1 = new Doctor("doc001", "김닥터", "800101");
        doctorList.add(d1);

        // 4. 환자 데이터 생성 (생년월일 추가됨)
        // 환자 A: 홍길동 (고위험)
        Patient p1 = new Patient("user001", "홍길동", "900505");
        HealthData h1 = new HealthData(UUID.randomUUID().toString(), "user001",
                true, 150.0f, "비만", 1, 160);
        p1.inputHealthData(h1, riskManager);
        p1.performRiskAnalysis(riskManager, params);

        d1.addPatient(p1);
        patientList.add(p1);

        // 환자 B: 이순신 (정상)
        Patient p2 = new Patient("user002", "이순신", "450428");
        HealthData h2 = new HealthData(UUID.randomUUID().toString(), "user002",
                false, 90.0f, "정상", 4, 110);
        p2.inputHealthData(h2, riskManager);
        p2.performRiskAnalysis(riskManager, params);

        d1.addPatient(p2);
        patientList.add(p2);

        System.out.println("[System] 시스템 데이터 초기화 완료. 로그인 화면을 실행합니다.");

        // 5. 로그인 화면 실행 (대시보드 바로 실행 X)
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI(doctorList, patientList, riskManager, params);
            loginUI.setVisible(true);
        });
    }
}