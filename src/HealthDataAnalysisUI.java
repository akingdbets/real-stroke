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
    private JTextArea taResult;
    private JButton btnAnalyze;

    public HealthDataAnalysisUI(Patient patient, RiskManager riskManager, Parameter parameter) {
        this.currentPatient = patient;
        this.riskManager = riskManager;
        this.parameter = parameter;

        setTitle("뇌졸중 위험도 분석 센터");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 상단: 분석 대상 데이터 요약 정보
        JPanel infoPanel = new JPanel(new GridLayout(5, 1));
        infoPanel.setBorder(BorderFactory.createTitledBorder("분석 대상 (최신 기록)"));

        lblDataDate = new JLabel("기록 일시: -");
        lblBp = new JLabel("최고 혈압: -");
        lblSugar = new JLabel("혈당: -");
        lblSmoke = new JLabel("흡연 여부: -");

        infoPanel.add(lblDataDate);
        infoPanel.add(lblBp);
        infoPanel.add(lblSugar);
        infoPanel.add(lblSmoke);

        // 2. 중앙: 분석 버튼 (순서 변경: 이 부분을 먼저 생성해야 합니다!)
        JPanel btnPanel = new JPanel();
        btnAnalyze = new JButton("⚠️ 위험도 분석 실행");
        btnAnalyze.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAnalyze.setBackground(new Color(230, 230, 250));
        btnAnalyze.addActionListener(new AnalyzeAction());
        btnPanel.add(btnAnalyze);

        // [중요 수정] 버튼 생성 후에 데이터를 로드해야 에러가 안 납니다.
        loadLatestData();

        // 3. 하단: 결과 리포트
        taResult = new JTextArea(8, 20);
        taResult.setEditable(false);
        taResult.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(taResult);
        scrollPane.setBorder(BorderFactory.createTitledBorder("분석 리포트"));

        add(infoPanel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    // 환자의 최근 데이터를 화면에 뿌려주는 헬퍼 메서드
    private void loadLatestData() {
        try {
            HealthData data = currentPatient.getLatestHealthData();
            // 실제 앱에서는 data.getRecordDate() 등을 사용
            lblDataDate.setText("기록 일시: " + java.time.LocalDate.now());
            lblBp.setText("최고 혈압: " + data.getMaxBloodPressure() + " mmHg");
            lblSugar.setText("혈당: " + data.getBloodSugar() + " mg/dL");
            lblSmoke.setText("흡연 여부: " + (data.isSmokingStatus() ? "예" : "아니오"));

            // 여기서 btnAnalyze를 쓰기 때문에, 버튼이 먼저 생성되어 있어야 함
            if (btnAnalyze != null) {
                btnAnalyze.setEnabled(true);
            }
        } catch (NoSuchElementException | IndexOutOfBoundsException e) {
            lblDataDate.setText("저장된 데이터가 없습니다.");
            if (btnAnalyze != null) {
                btnAnalyze.setEnabled(false);
            }
        }
    }

    // 분석 버튼 리스너
    private class AnalyzeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // 1. [Behavior] Patient에게 분석 메시지 전송
                currentPatient.performRiskAnalysis(riskManager, parameter);

                // 2. [View] 결과값 가져와서 출력
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
            sb.append("   즉시 전문의와 상담하시기 바랍니다.\n");
            sb.append("   (담당자에게 알림이 전송되었습니다)\n");
            taResult.setForeground(Color.RED);
        } else {
            sb.append("✅ [정상] 건강 상태가 양호합니다.\n");
            sb.append("   현재 생활 습관을 유지하세요.\n");
            taResult.setForeground(new Color(0, 100, 0)); // 짙은 녹색
        }
        taResult.setText(sb.toString());
    }
}