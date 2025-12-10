import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

public class HealthDataInputUI extends JFrame {

    private Patient currentPatient;
    private RiskManager riskManager;

    // UI 컴포넌트
    private JTextField tfMaxBP;
    private JTextField tfBloodSugar, tfBodyComp;
    private JCheckBox chkSmoking;
    private JComboBox<Integer> comboActivity;
    private JTextArea taResult;

    public HealthDataInputUI(Patient patient, RiskManager riskManager) {
        this.currentPatient = patient;
        this.riskManager = riskManager;

        setTitle("건강 데이터 입력");
        setSize(400, 500);

        // [수정 1] 창 닫기 버튼(X)을 누르면 창이 꺼지도록 설정 (기존: DO_NOTHING_ON_CLOSE)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null); // 화면 중앙 배치
        setLayout(new BorderLayout());

        // 1. 입력 패널 구성
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

        // 2. 버튼 패널 (저장 / 닫기)
        JPanel btnPanel = new JPanel(new FlowLayout());

        JButton btnSubmit = new JButton("저장하기");
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(200, 230, 255)); // 연한 파랑
        btnSubmit.addActionListener(new SubmitAction());

        // [수정 2] '닫기' 버튼 추가 (저장하지 않고 돌아가기)
        JButton btnCancel = new JButton("닫기");
        btnCancel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnCancel.addActionListener(e -> dispose()); // 현재 창 닫기

        btnPanel.add(btnSubmit);
        btnPanel.add(btnCancel);

        // 3. 결과/상태 로그 영역
        taResult = new JTextArea(5, 20);
        taResult.setEditable(false);
        taResult.setText("데이터를 입력하고 저장 버튼을 누르세요.");
        taResult.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(inputPanel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.CENTER);
        add(new JScrollPane(taResult), BorderLayout.SOUTH);
    }

    private class SubmitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // 1. 입력값 파싱
                String strMaxBP = tfMaxBP.getText().trim();
                String strBloodSugar = tfBloodSugar.getText().trim();

                if (strMaxBP.isEmpty() || strBloodSugar.isEmpty()) {
                    JOptionPane.showMessageDialog(HealthDataInputUI.this,
                            "혈압과 혈당은 필수 입력값입니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int maxBP = Integer.parseInt(strMaxBP);
                float bloodSugar = Float.parseFloat(strBloodSugar);
                String bodyComp = tfBodyComp.getText();
                int activity = (Integer) comboActivity.getSelectedItem();
                boolean isSmoking = chkSmoking.isSelected();

                // 2. HealthData 객체 생성
                HealthData data = new HealthData(
                        UUID.randomUUID().toString().substring(0, 8),
                        currentPatient.getName(), // ID 대신 이름 사용 (임시)
                        isSmoking,
                        bloodSugar,
                        bodyComp,
                        activity,
                        maxBP
                );

                // 3. 데이터 저장 요청
                boolean isSaved = currentPatient.inputHealthData(data, riskManager);

                // 4. 결과 처리
                if (isSaved) {
                    JOptionPane.showMessageDialog(HealthDataInputUI.this,
                            "데이터가 성공적으로 저장되었습니다.\n메인 화면으로 돌아갑니다.");

                    // [수정 3] 저장 성공 시 창을 닫고 메인으로 복귀
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(HealthDataInputUI.this, "저장에 실패했습니다.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(HealthDataInputUI.this,
                        "숫자 형식이 올바르지 않습니다.\n혈압과 혈당은 숫자로 입력해주세요.",
                        "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}