import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Random;

public class StrokePreventionSystem {

    public static void main(String[] args) {
        // 1. 설정 및 매니저 초기화
        // 파라미터(기준): 흡연20점, 혈당가중치0.5, 혈압가중치1.0, 운동부족10점, 기준혈당126, 기준혈압140, 위험기준60점
        Parameter params = new Parameter(20.0, 0.5, 1.0, 10.0, 126.0f, 140, 60.0);
        NotificationService notiService = new ConsoleNotificationService();
        RiskManager riskManager = new RiskManager(notiService);

        List<Doctor> doctorList = new ArrayList<>();
        List<Patient> patientList = new ArrayList<>();

        // 2. 주치의 생성
        Doctor d1 = new Doctor("doc001", "김닥터", "800101");
        doctorList.add(d1);

        // 랜덤값 생성을 위한 도구
        Random random = new Random();

        // ==========================================
        // [1] 홍길동 (고위험군) - 일주일치 데이터
        // ==========================================
        Patient p1 = new Patient("user001", "홍길동", "900505");

        for (int i = 6; i >= 0; i--) {
            // 고위험 수치 랜덤 생성
            // 혈압: 145 ~ 165 (위험)
            int bp = 145 + random.nextInt(21);
            // 혈당: 140 ~ 160 (위험)
            float sugar = 140.0f + random.nextInt(21);

            HealthData h = new HealthData(UUID.randomUUID().toString(), "user001",
                    true, sugar, "비만", 1, bp); // 흡연함, 운동부족(1)

            h.setRecordDate(LocalDate.now().minusDays(i)); // 날짜 조작

            p1.inputHealthData(h, riskManager);
            p1.performRiskAnalysis(riskManager, params);
        }
        d1.addPatient(p1);
        patientList.add(p1);


        // ==========================================
        // [2] 이순신 (정상군) - 일주일치 데이터 추가!
        // ==========================================
        Patient p2 = new Patient("user002", "이순신", "450428");

        for (int i = 6; i >= 0; i--) {
            // 정상 수치 랜덤 생성
            // 혈압: 110 ~ 125 (정상)
            int bp = 110 + random.nextInt(16);
            // 혈당: 85 ~ 100 (정상)
            float sugar = 85.0f + random.nextInt(16);

            HealthData h = new HealthData(UUID.randomUUID().toString(), "user002",
                    false, sugar, "정상", 4, bp); // 비흡연, 활동량좋음(4)

            h.setRecordDate(LocalDate.now().minusDays(i)); // 날짜 조작

            p2.inputHealthData(h, riskManager);
            p2.performRiskAnalysis(riskManager, params);
        }

        d1.addPatient(p2);
        patientList.add(p2);

        System.out.println("[System] 홍길동(위험) & 이순신(정상) 일주일치 데이터 생성 완료.");

        // 5. 로그인 화면 실행
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI(doctorList, patientList, riskManager, params);
            loginUI.setVisible(true);
        });
    }
}