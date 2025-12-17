import javax.swing.*;
import java.awt.*;

public class PatientDetailDialog extends JDialog {

    private Patient patient;

    public PatientDetailDialog(Frame owner, Patient patient) {
        super(owner, patient.getName() + " 환자 상세 리포트", true);
        this.patient = patient;

        setSize(450, 600); // 크기 조금 더 확보
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // 1. 상단: 환자 기본 정보
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.add(new JLabel("  👤 환자명: " + patient.getName()));
        headerPanel.add(new JLabel("  🎂 생년월일: " + patient.getBirthDate()));
        add(headerPanel, BorderLayout.NORTH);

        // 2. 중앙: 탭 패널
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📆 일간 리포트", createDailyPanel());
        tabbedPane.addTab("📊 주간 리포트", createWeeklyPanel()); // 수정된 메서드 호출
        add(tabbedPane, BorderLayout.CENTER);

        // 3. 하단: 닫기 버튼
        JPanel bottomPanel = new JPanel();
        JButton btnClose = new JButton("닫기");
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- [Tab 1] 일간 리포트 (기존 유지) ---
    private JPanel createDailyPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        double score = 0;
        String level = "-";
        String date = "-";
        String bp = "-", sugar = "-", smoke = "-";

        try {
            Risk r = patient.getLatestRisk();
            score = r.getTotalScore();
            level = r.getRiskLevel();
            date = r.getAssessedAt().toLocalDate().toString();

            HealthData h = patient.getLatestHealthData();
            bp = String.valueOf(h.getMaxBloodPressure());
            sugar = String.valueOf(h.getBloodSugar());
            smoke = h.isSmokingStatus() ? "예" : "아니오";
        } catch (Exception e) {
            level = "데이터 없음";
        }

        JLabel lblDate = new JLabel("기준일: " + date);
        lblDate.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(lblDate);

        JPanel pnlRisk = new JPanel(new GridLayout(2, 1));
        pnlRisk.setBorder(BorderFactory.createTitledBorder("🚨 오늘의 위험도 분석"));
        pnlRisk.setBackground(Color.WHITE);

        JLabel lblScore = new JLabel(String.format("총점: %.1f점", score));
        JLabel lblLevel = new JLabel("등급: " + level);
        if ("위험".equals(level)) {
            lblLevel.setForeground(Color.RED);
            lblLevel.setFont(new Font("SansSerif", Font.BOLD, 15));
        } else {
            lblLevel.setForeground(new Color(0, 100, 0));
        }
        pnlRisk.add(lblScore);
        pnlRisk.add(lblLevel);
        panel.add(pnlRisk);

        JPanel pnlData = new JPanel(new GridLayout(3, 1));
        pnlData.setBorder(BorderFactory.createTitledBorder("🩺 측정 데이터"));
        pnlData.add(new JLabel("• 최고 혈압: " + bp + " mmHg"));
        pnlData.add(new JLabel("• 공복 혈당: " + sugar + " mg/dL"));
        pnlData.add(new JLabel("• 흡연 여부: " + smoke));
        panel.add(pnlData);

        return panel;
    }

    // --- [Tab 2] 주간 리포트 (수정됨: 저장 기능 추가) ---
    private JPanel createWeeklyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // (1) 주간 통계 표시
        String summaryHtml = patient.getWeeklySummary();
        JLabel lblSummary = new JLabel(summaryHtml);
        lblSummary.setVerticalAlignment(SwingConstants.TOP);

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("📈 주간 평균 추이 (최근 7건)"));
        statsPanel.add(lblSummary, BorderLayout.CENTER);
        statsPanel.setPreferredSize(new Dimension(0, 100)); // 높이 고정

        // (2) 소견 작성 영역
        JPanel memoPanel = new JPanel(new BorderLayout(0, 5));
        memoPanel.setBorder(BorderFactory.createTitledBorder("👨‍⚕️ 주치의 주간 소견"));

        JTextArea taComment = new JTextArea();
        taComment.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // [중요] 기존에 저장된 소견이 있다면 불러와서 세팅
        if (patient.getDoctorWeeklyMemo() != null && !patient.getDoctorWeeklyMemo().isEmpty()) {
            taComment.setText(patient.getDoctorWeeklyMemo());
        } else {
            taComment.setText("특이사항 없음. 지속적인 관찰 요망."); // 기본 문구
        }

        JScrollPane scrollPane = new JScrollPane(taComment);

        // (3) 저장 버튼
        JButton btnSave = new JButton("💾 소견 저장하기");
        btnSave.setBackground(new Color(200, 255, 200)); // 연한 초록색
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 13));

        // [중요] 저장 버튼 클릭 이벤트
        btnSave.addActionListener(e -> {
            String content = taComment.getText();
            patient.setDoctorWeeklyMemo(content); // Patient 객체에 저장
            JOptionPane.showMessageDialog(this, "주간 소견이 정상적으로 저장되었습니다.");
        });

        memoPanel.add(scrollPane, BorderLayout.CENTER);
        memoPanel.add(btnSave, BorderLayout.SOUTH);

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(memoPanel, BorderLayout.CENTER);

        return panel;
    }
}