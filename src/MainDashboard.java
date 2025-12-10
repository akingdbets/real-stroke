import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    private Patient patient;
    private RiskManager riskManager;
    private Parameter parameter;
    private Runnable logoutAction; // [추가]

    // [수정] 생성자에 logoutAction 추가
    public MainDashboard(Patient patient, RiskManager riskManager, Parameter parameter, Runnable logoutAction) {
        this.patient = patient;
        this.riskManager = riskManager;
        this.parameter = parameter;
        this.logoutAction = logoutAction; // 저장

        setTitle("환자용 대시보드 - " + patient.getName());
        setSize(400, 350); // 높이 조금 늘림
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 환영 문구
        JLabel lblWelcome = new JLabel(patient.getName() + "님 (" + patient.getBirthDate() + ")", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblWelcome, BorderLayout.NORTH);

        // 2. 버튼 패널
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));

        JButton btnOpenInput = new JButton("📝 건강 데이터 입력하기");
        JButton btnOpenAnalysis = new JButton("📊 위험도 분석 결과 보기");
        JButton btnLogout = new JButton("로그아웃"); // [추가]

        btnOpenInput.addActionListener(e -> openInputUI());
        btnOpenAnalysis.addActionListener(e -> openAnalysisUI());

        // [추가] 로그아웃 버튼 동작
        btnLogout.addActionListener(e -> {
            this.dispose();     // 현재 창 닫기
            logoutAction.run(); // 로그인 창 다시 열기
        });

        centerPanel.add(btnOpenInput);
        centerPanel.add(btnOpenAnalysis);
        centerPanel.add(btnLogout); // 패널에 추가

        add(centerPanel, BorderLayout.CENTER);
    }

    // ... openInputUI, openAnalysisUI 메서드는 기존과 동일 ...
    private void openInputUI() {
        HealthDataInputUI inputUI = new HealthDataInputUI(patient, riskManager);
        inputUI.setLocationRelativeTo(this);
        inputUI.setVisible(true);
    }

    private void openAnalysisUI() {
        HealthDataAnalysisUI analysisUI = new HealthDataAnalysisUI(patient, riskManager, parameter);
        analysisUI.setLocationRelativeTo(this);
        analysisUI.setVisible(true);
    }
}