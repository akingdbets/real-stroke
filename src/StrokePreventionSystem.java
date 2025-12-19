import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Random;

public class StrokePreventionSystem {

    public static void main(String[] args) {
        // 1. 설정 및 매니저 초기화
        Parameter params = new Parameter(20.0, 0.5, 1.0, 10.0, 126.0f, 140, 60.0);

        RiskManager riskManager = new RiskManager();

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
            int bp = 145 + random.nextInt(21);
            float sugar = 140.0f + random.nextInt(21);

            HealthData h = new HealthData(UUID.randomUUID().toString(), "user001",
                    true, sugar, "비만", 1, bp);

            h.setRecordDate(LocalDate.now().minusDays(i));

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
            int bp = 110 + random.nextInt(16);
            float sugar = 85.0f + random.nextInt(16);

            HealthData h = new HealthData(UUID.randomUUID().toString(), "user002",
                    false, sugar, "정상", 4, bp);

            h.setRecordDate(LocalDate.now().minusDays(i));

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