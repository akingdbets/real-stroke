import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

public class HealthDataInputUI extends JFrame {

    private Patient currentPatient;
    private RiskManager riskManager;
    // Parameter parameter; // [제거] 입력 단계에서는 분석 기준이 필요 없음

    // UI 컴포넌트
    private JTextField tfMaxBP;
    private JTextField tfBloodSugar, tfBodyComp;
    private JCheckBox chkSmoking;
    private JComboBox<Integer> comboActivity;
    private JTextArea taResult;

    // 생성자 파라미터에서 Parameter 제거
    public HealthDataInputUI(Patient patient, RiskManager riskManager) {
        this.currentPatient = patient;
        this.riskManager = riskManager;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("건강 데이터 입력 (저장 전용)");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        // 입력 패널
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("최고 혈압 (mmHg):"));
        tfMaxBP = new JTextField();
        inputPanel.add(tfMaxBP);

        inputPanel.add(new JLabel("혈당 수치 (mg/dL):"));
        tfBloodSugar = new JTextField();
        inputPanel.add(tfBloodSugar);

        inputPanel.add(new JLabel("체성분 (BMI):"));
        tfBodyComp = new JTextField();
        inputPanel.add(tfBodyComp);

        inputPanel.add(new JLabel("활동량 레벨 (1~5):"));
        Integer[] activityLevels = {1, 2, 3, 4, 5};
        comboActivity = new JComboBox<>(activityLevels);
        inputPanel.add(comboActivity);

        inputPanel.add(new JLabel("현재 흡연 여부:"));
        chkSmoking = new JCheckBox("흡연 중");
        inputPanel.add(chkSmoking);

        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));

        // 버튼 텍스트 변경
        JButton btnSubmit = new JButton("데이터 저장하기");
        btnSubmit.addActionListener(new SubmitAction());

        taResult = new JTextArea(8, 20);
        taResult.setEditable(false);
        taResult.setFont(new Font("Monospaced", Font.PLAIN, 14));

        add(inputPanel, BorderLayout.NORTH);
        add(btnSubmit, BorderLayout.CENTER);
        add(new JScrollPane(taResult), BorderLayout.SOUTH);
    }

    private class SubmitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // 1. 입력값 파싱
                int maxBP = Integer.parseInt(tfMaxBP.getText());
                float bloodSugar = Float.parseFloat(tfBloodSugar.getText());
                String bodyComp = tfBodyComp.getText();
                int activity = (Integer) comboActivity.getSelectedItem();
                boolean isSmoking = chkSmoking.isSelected();

                // 2. HealthData 객체 생성
                HealthData data = new HealthData(
                        UUID.randomUUID().toString().substring(0, 8),
                        "user001", // 실제 앱에선 로그인 세션 ID 사용
                        isSmoking,
                        bloodSugar,
                        bodyComp,
                        activity,
                        maxBP
                );

                // 3. Patient에게 데이터 저장 요청 (분석 X)
                // 제공해주신 Patient 코드에 맞춰 riskManager도 전달
                boolean isSaved = currentPatient.inputHealthData(data, riskManager);

                // 4. 결과 피드백
                if (isSaved) {
                    printSuccessMessage(data);
                    clearFields(); // 입력창 초기화
                } else {
                    JOptionPane.showMessageDialog(HealthDataInputUI.this, "저장에 실패했습니다.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(HealthDataInputUI.this,
                        "숫자 형식이 올바르지 않습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printSuccessMessage(HealthData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("✅ [시스템] 데이터 저장 완료\n");
        sb.append("──────────────────────\n");
        sb.append(String.format("• 기록일자: %s\n", java.time.LocalDate.now()));
        sb.append(String.format("• 혈압: %d mmHg\n", data.getMaxBloodPressure()));
        sb.append(String.format("• 혈당: %.1f mg/dL\n", data.getBloodSugar()));
        sb.append(String.format("• 활동량 레벨: %d mg/dL\n", data.getActivityLevel()));
        sb.append(String.format("• 현재 흡연 여부: %b \n", data.isSmokingStatus()));
        sb.append("──────────────────────\n");
        sb.append("※ 위험도 분석은 별도 메뉴를 이용해주세요.");

        taResult.setText(sb.toString());
        taResult.setForeground(new Color(0, 100, 0)); // 짙은 녹색
    }

    // 편의 기능: 저장 후 입력창 비우기
    private void clearFields() {
        tfMaxBP.setText("");
        tfBloodSugar.setText("");
        tfBodyComp.setText("");
        chkSmoking.setSelected(false);
        comboActivity.setSelectedIndex(0);
    }
}