import javax.swing.*;
import java.awt.*;

public class PatientDetailDialog extends JDialog {

    public PatientDetailDialog(Frame owner, Patient patient) {
        super(owner, patient.getName() + " 환자 상세 리포트", true);
        setSize(400, 500);
        setLayout(new GridLayout(6, 1));

        // 1. 환자 기본 정보
        JPanel pnlBasic = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBasic.setBorder(BorderFactory.createTitledBorder("기본 정보"));
        pnlBasic.add(new JLabel("이름: " + patient.getName()));
        add(pnlBasic);

        // 2. 최신 위험도 정보 가져오기
        double score = 0;
        String level = "데이터 없음";
        try {
            Risk r = patient.getLatestRisk();
            score = r.getTotalScore();
            level = r.getRiskLevel();
        } catch (Exception e) {}

        JPanel pnlRisk = new JPanel(new GridLayout(2, 1));
        pnlRisk.setBorder(BorderFactory.createTitledBorder("위험도 분석 결과"));
        JLabel lblScore = new JLabel(String.format("총점: %.1f", score));
        JLabel lblLevel = new JLabel("등급: " + level);

        if ("위험".equals(level)) {
            lblLevel.setForeground(Color.RED);
            lblLevel.setFont(new Font("SansSerif", Font.BOLD, 14));
        }

        pnlRisk.add(lblScore);
        pnlRisk.add(lblLevel);
        add(pnlRisk);

        // 3. 최신 건강 데이터 가져오기
        String bp = "-", sugar = "-", smoke = "-";
        try {
            HealthData h = patient.getLatestHealthData();
            bp = String.valueOf(h.getMaxBloodPressure());
            sugar = String.valueOf(h.getBloodSugar());
            smoke = h.isSmokingStatus() ? "흡연 중" : "비흡연";
        } catch (Exception e) {}

        JPanel pnlHealth = new JPanel(new GridLayout(3, 1));
        pnlHealth.setBorder(BorderFactory.createTitledBorder("최근 건강 데이터"));
        pnlHealth.add(new JLabel("혈압: " + bp + " mmHg"));
        pnlHealth.add(new JLabel("혈당: " + sugar + " mg/dL"));
        pnlHealth.add(new JLabel("흡연: " + smoke));
        add(pnlHealth);

        // 4. 주치의 메모 (간단 구현)
        JPanel pnlMemo = new JPanel(new BorderLayout());
        pnlMemo.setBorder(BorderFactory.createTitledBorder("진료 메모"));
        JTextArea taMemo = new JTextArea("특이사항 없음\n(주치의가 작성 가능)");
        pnlMemo.add(new JScrollPane(taMemo));
        add(pnlMemo);

        // 닫기 버튼
        JButton btnClose = new JButton("닫기");
        btnClose.addActionListener(e -> dispose());
        add(btnClose);
    }
}