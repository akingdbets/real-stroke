import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    private Patient patient;
    private RiskManager riskManager;
    private Parameter parameter;

    public MainDashboard(Patient patient, RiskManager riskManager, Parameter parameter) {
        this.patient = patient;
        this.riskManager = riskManager;
        this.parameter = parameter;

        setTitle("뇌졸중 재발 방지 관리 시스템");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 메인 창을 닫으면 프로그램 종료
        setLayout(new GridLayout(3, 1, 10, 10)); // 3행 1열 그리드

        // 1. 환자 정보 표시 (간단히)
        JLabel lblWelcome = new JLabel(patient.getName() + "님, 환영합니다.", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblWelcome);

        // 2. 입력 창 열기 버튼
        JButton btnOpenInput = new JButton("📝 건강 데이터 입력하기");
        btnOpenInput.addActionListener(e -> openInputUI());
        add(btnOpenInput);

        // 3. 분석 창 열기 버튼
        JButton btnOpenAnalysis = new JButton("📊 위험도 분석 결과 보기");
        btnOpenAnalysis.addActionListener(e -> openAnalysisUI());
        add(btnOpenAnalysis);

        // 여백 주기
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void openInputUI() {
        // 새 입력 창 생성 (DISPOSE_ON_CLOSE로 설정해야 메인 창이 안 꺼짐)
        HealthDataInputUI inputUI = new HealthDataInputUI(patient, riskManager);
        inputUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputUI.setVisible(true);
    }

    private void openAnalysisUI() {
        // 새 분석 창 생성
        HealthDataAnalysisUI analysisUI = new HealthDataAnalysisUI(patient, riskManager, parameter);
        // 분석 창은 이미 내부에서 DISPOSE_ON_CLOSE로 되어 있어서 그대로 두면 됨
        analysisUI.setVisible(true);
    }
}