import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientDetailDialog extends JDialog {

    private Patient patient;

    public PatientDetailDialog(Frame owner, Patient patient) {
        super(owner, patient.getName() + " 환자 상세 리포트", true);
        this.patient = patient;

        // 탭과 테이블이 들어가므로 크기를 넉넉하게 잡습니다.
        setSize(500, 650);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // 1. [상단] 환자 기본 정보 헤더
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255)); // 연한 하늘색 배경
        headerPanel.add(new JLabel("  👤 환자명: " + patient.getName()));
        headerPanel.add(new JLabel("  🎂 생년월일: " + patient.getBirthDate()));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        add(headerPanel, BorderLayout.NORTH);

        // 2. [중앙] 탭 패널 (일간 / 주간)
        JTabbedPane tabbedPane = new JTabbedPane();

        // 탭 1: 일간 리포트 (오늘의 상태)
        tabbedPane.addTab("📆 일간 리포트", createDailyPanel());

        // 탭 2: 주간 리포트 (통계 + 히스토리 + 소견)
        tabbedPane.addTab("📊 주간 리포트", createWeeklyPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // 3. [하단] 닫기 버튼
        JPanel bottomPanel = new JPanel();
        JButton btnClose = new JButton("닫기");
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // =================================================================================
    // 탭 1: 일간 리포트 패널 생성 (최신 1건 데이터 상세 표시)
    // =================================================================================
    private JPanel createDailyPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        double score = 0;
        String level = "-";
        String date = "-";
        String bp = "-", sugar = "-", smoke = "-";

        try {
            // 최신 위험도 정보 가져오기
            Risk r = patient.getLatestRisk();
            score = r.getTotalScore();
            level = r.getRiskLevel();
            date = r.getAssessedAt().toLocalDate().toString();

            // 최신 건강 데이터 가져오기
            HealthData h = patient.getLatestHealthData();
            bp = String.valueOf(h.getMaxBloodPressure());
            sugar = String.valueOf(h.getBloodSugar());
            smoke = h.isSmokingStatus() ? "예" : "아니오";

        } catch (Exception e) {
            level = "데이터 없음";
        }

        // (1) 기준일 표시
        JLabel lblDate = new JLabel("기준일: " + date);
        lblDate.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(lblDate);

        // (2) 위험도 분석 결과
        JPanel pnlRisk = new JPanel(new GridLayout(2, 1));
        pnlRisk.setBorder(BorderFactory.createTitledBorder("🚨 오늘의 위험도 분석"));
        pnlRisk.setBackground(Color.WHITE);

        JLabel lblScore = new JLabel(String.format("총점: %.1f점", score));
        JLabel lblLevel = new JLabel("등급: " + level);

        if ("위험".equals(level)) {
            lblLevel.setForeground(Color.RED);
            lblLevel.setFont(new Font("SansSerif", Font.BOLD, 15));
        } else {
            lblLevel.setForeground(new Color(0, 100, 0)); // 초록색
        }
        pnlRisk.add(lblScore);
        pnlRisk.add(lblLevel);
        panel.add(pnlRisk);

        // (3) 상세 수치 데이터
        JPanel pnlData = new JPanel(new GridLayout(3, 1));
        pnlData.setBorder(BorderFactory.createTitledBorder("🩺 측정 데이터"));
        pnlData.add(new JLabel("• 최고 혈압: " + bp + " mmHg"));
        pnlData.add(new JLabel("• 공복 혈당: " + sugar + " mg/dL"));
        pnlData.add(new JLabel("• 흡연 여부: " + smoke));
        panel.add(pnlData);

        // (4) 빈 공간 채우기용 (레이아웃 균형)
        panel.add(new JLabel(""));

        return panel;
    }

    // =================================================================================
    // 탭 2: 주간 리포트 패널 생성 (요약 통계 + 상세 기록 테이블 + 소견 메모)
    // =================================================================================
    private JPanel createWeeklyPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10)); // 컴포넌트 간 수직 간격 10
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. [상단] 주간 요약 통계 (HTML 텍스트)
        String summaryHtml = patient.getWeeklySummary();
        JLabel lblSummary = new JLabel(summaryHtml);

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("📈 주간 요약 (최근 7건 평균)"));
        statsPanel.add(lblSummary, BorderLayout.CENTER);
        statsPanel.setPreferredSize(new Dimension(0, 100)); // 높이 고정

        // 2. [중앙] 상세 기록 테이블 (JTable)
        String[] columns = {"날짜", "혈압", "혈당", "흡연", "활동량"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 테이블 수정 불가
            }
        };

        // 최근 7건 데이터 가져와서 테이블에 채우기
        List<HealthData> recentData = patient.getRecentHealthData(7);
        for (HealthData h : recentData) {
            String date = h.getRecordDate().toString();
            String bp = String.valueOf(h.getMaxBloodPressure());
            String sugar = String.format("%.1f", h.getBloodSugar());
            String smoke = h.isSmokingStatus() ? "O" : "X";
            String activity = String.valueOf(h.getActivityLevel());

            tableModel.addRow(new Object[]{date, bp, sugar, smoke, activity});
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(22);

        // 테이블 셀 내용 가운데 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("📋 상세 기록 히스토리"));

        // 3. [하단] 주치의 소견 메모 작성 영역
        JPanel memoPanel = new JPanel(new BorderLayout(0, 5));
        memoPanel.setBorder(BorderFactory.createTitledBorder("👨‍⚕️ 주치의 주간 소견 작성"));

        JTextArea taComment = new JTextArea(4, 20); // 4줄 높이
        taComment.setFont(new Font("Monospaced", Font.PLAIN, 13));
        taComment.setLineWrap(true);

        // 기존에 저장된 소견이 있으면 불러오기
        if (patient.getDoctorWeeklyMemo() != null && !patient.getDoctorWeeklyMemo().isEmpty()) {
            taComment.setText(patient.getDoctorWeeklyMemo());
        } else {
            taComment.setText("특이사항 없음. 지속적인 관찰 요망."); // 기본 문구
        }

        JScrollPane memoScroll = new JScrollPane(taComment);

        // 소견 저장 버튼
        JButton btnSave = new JButton("💾 소견 저장하기");
        btnSave.setBackground(new Color(200, 255, 200)); // 연한 초록색
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 13));

        btnSave.addActionListener(e -> {
            String content = taComment.getText();
            patient.setDoctorWeeklyMemo(content); // Patient 객체에 저장
            JOptionPane.showMessageDialog(this, "주간 소견이 정상적으로 저장되었습니다.\n(환자 대시보드에도 즉시 반영됩니다)");
        });

        memoPanel.add(memoScroll, BorderLayout.CENTER);
        memoPanel.add(btnSave, BorderLayout.SOUTH);

        // 패널 조립
        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER); // 테이블이 남은 공간 차지
        panel.add(memoPanel, BorderLayout.SOUTH);

        return panel;
    }
}