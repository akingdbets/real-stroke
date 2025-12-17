import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.NoSuchElementException;

public class HealthDataAnalysisUI extends JFrame {

    private Patient currentPatient;
    private RiskManager riskManager;
    private Parameter parameter;

    private JLabel lblDataDate, lblBp, lblSugar, lblSmoke;
    private JTextArea taResult;      // 알고리즘 분석 결과
    private JTextArea taDoctorMemo;  // [추가] 주치의 소견 표시창
    private JButton btnAnalyze;

    public HealthDataAnalysisUI(Patient patient, RiskManager riskManager, Parameter parameter) {
        this.currentPatient = patient;
        this.riskManager = riskManager;
        this.parameter = parameter;

        setTitle("뇌졸중 위험도 분석 센터");
        setSize(420, 600); // 내용을 다 보여주기 위해 높이 증가
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 상단: 최신 데이터 요약
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBorder(BorderFactory.createTitledBorder("분석 대상 (최신 기록)"));
        infoPanel.setBackground(new Color(245, 245, 255));

        lblDataDate = new JLabel("기록 일시: -");
        lblBp = new JLabel("최고 혈압: -");
        lblSugar = new JLabel("혈당: -");
        lblSmoke = new JLabel("흡연 여부: -");

        infoPanel.add(lblDataDate);
        infoPanel.add(lblBp);
        infoPanel.add(lblSugar);
        infoPanel.add(lblSmoke);

        // 2. 중앙: 분석 버튼
        JPanel btnPanel = new JPanel();
        btnAnalyze = new JButton("⚠️ 위험도 정밀 분석 실행");
        btnAnalyze.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAnalyze.setBackground(new Color(255, 230, 230));
        btnAnalyze.addActionListener(new AnalyzeAction());
        btnPanel.add(btnAnalyze);

        // 먼저 데이터 로드 (버튼 활성화 여부 결정)
        loadLatestData();

        // 3. 하단: 결과 리포트 영역 (SplitPane 사용)
        // 위쪽: 알고리즘 분석 결과 / 아래쪽: 주치의 소견

        // (A) 알고리즘 결과창
        taResult = new JTextArea();
        taResult.setEditable(false);
        taResult.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollResult = new JScrollPane(taResult);
        scrollResult.setBorder(BorderFactory.createTitledBorder("🤖 AI 위험도 분석 리포트"));

        // (B) 주치의 소견창 [신규 추가]
        taDoctorMemo = new JTextArea();
        taDoctorMemo.setEditable(false); // 환자는 읽기만 가능
        taDoctorMemo.setFont(new Font("Monospaced", Font.PLAIN, 13));
        taDoctorMemo.setForeground(new Color(0, 0, 150)); // 파란색 글씨
        taDoctorMemo.setLineWrap(true);

        // 소견 데이터 불러오기
        String savedMemo = currentPatient.getDoctorWeeklyMemo();
        if (savedMemo == null || savedMemo.isEmpty()) {
            taDoctorMemo.setText("(아직 등록된 주치의 소견이 없습니다.)");
        } else {
            taDoctorMemo.setText(savedMemo);
        }

        JScrollPane scrollMemo = new JScrollPane(taDoctorMemo);
        scrollMemo.setBorder(BorderFactory.createTitledBorder("👨‍⚕️ 주치의 선생님의 코멘트"));

        // 화면 분할 (위:아래 = 5:5 비율)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollResult, scrollMemo);
        splitPane.setDividerLocation(200); // 분할선 위치
        splitPane.setResizeWeight(0.5);

        add(infoPanel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.CENTER); // 버튼은 중앙 상단
        add(splitPane, BorderLayout.SOUTH); // 리포트 영역은 하단 전체

        // 레이아웃 보정 (Center 영역이 너무 작아지는 것 방지)
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(btnPanel, BorderLayout.NORTH);
        centerContainer.add(splitPane, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);
    }

    private void loadLatestData() {
        try {
            HealthData data = currentPatient.getLatestHealthData();
            lblDataDate.setText("기록 일시: " + java.time.LocalDate.now());
            lblBp.setText("최고 혈압: " + data.getMaxBloodPressure() + " mmHg");
            lblSugar.setText("혈당: " + data.getBloodSugar() + " mg/dL");
            lblSmoke.setText("흡연 여부: " + (data.isSmokingStatus() ? "예" : "아니오"));

            if (btnAnalyze != null) btnAnalyze.setEnabled(true);
        } catch (NoSuchElementException | IndexOutOfBoundsException e) {
            lblDataDate.setText("저장된 데이터가 없습니다.");
            if (btnAnalyze != null) btnAnalyze.setEnabled(false);
        }
    }

    private class AnalyzeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                currentPatient.performRiskAnalysis(riskManager, parameter);
                Risk result = currentPatient.getLatestRisk();
                printReport(result);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(HealthDataAnalysisUI.this,
                        "분석 중 오류 발생: " + ex.getMessage());
            }
        }
    }

    private void printReport(Risk risk) {
        StringBuilder sb = new StringBuilder();
        sb.append("──────── 결과 상세 ────────\n");
        sb.append(String.format("총점: %.1f / 100.0\n", risk.getTotalScore()));
        sb.append(String.format("등급: %s\n", risk.getRiskLevel()));
        sb.append("──────────────────────\n");

        if ("위험".equals(risk.getRiskLevel())) {
            sb.append("🚨 [위험] 기준치를 초과했습니다.\n");
            sb.append("   즉시 전문의와 상담하세요.\n");
            taResult.setForeground(Color.RED);
        } else {
            sb.append("✅ [정상] 건강 상태가 양호합니다.\n");
            sb.append("   현재 습관을 유지하세요.\n");
            taResult.setForeground(new Color(0, 100, 0));
        }
        taResult.setText(sb.toString());
    }
}