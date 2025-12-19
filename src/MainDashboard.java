import javax.swing.*;
import java.awt.*;


public class MainDashboard extends JFrame {

    private Patient patient;
    private RiskManager riskManager;
    private Parameter parameter;
    private Runnable logoutAction;

    private MedicationManager medManager = new MedicationManager();
    private Medication myMed = new Medication("처방받은 약", "09:00"); // 09:00 복용 약

    public MainDashboard(Patient patient, RiskManager riskManager, Parameter parameter, Runnable logoutAction) {
        this.patient = patient;
        this.riskManager = riskManager;
        this.parameter = parameter;
        this.logoutAction = logoutAction;

        medManager.manageSet(myMed);

        setTitle("환자용 대시보드 - " + patient.getName());
        setSize(400, 420); // 버튼이 늘어나서 세로 길이 확보
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 상단 환영 문구
        JLabel lblWelcome = new JLabel(patient.getName() + "님 (" + patient.getBirthDate() + ")", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblWelcome, BorderLayout.NORTH);

        // 2. 중앙 버튼 패널
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // 버튼 4개라 4줄로 변경
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));

        JButton btnOpenInput = new JButton("📝 건강 데이터 입력하기");
        JButton btnOpenAnalysis = new JButton("📊 위험도 분석 결과 보기");

        // 시뮬레이션 버튼 생성
        JButton btnSimul = new JButton("⏰ [시뮬레이션] 09:00 상황 가정");
        btnSimul.setBackground(new Color(255, 250, 205)); // 노란색 강조

        JButton btnLogout = new JButton("로그아웃");

        // 버튼 동작 연결
        btnOpenInput.addActionListener(e -> openInputUI());
        btnOpenAnalysis.addActionListener(e -> openAnalysisUI());

        btnSimul.addActionListener(e -> {
            medManager.startManagement("09:00");
        });

        btnLogout.addActionListener(e -> {
            this.dispose();
            logoutAction.run();
        });

        centerPanel.add(btnOpenInput);
        centerPanel.add(btnOpenAnalysis);
        centerPanel.add(btnSimul); // 패널에 버튼 추가
        centerPanel.add(btnLogout);

        add(centerPanel, BorderLayout.CENTER);
    }

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