import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginUI extends JFrame {

    // 데이터 검증 및 대시보드 전달용 객체들
    private List<Doctor> doctorList;
    private List<Patient> patientList;
    private RiskManager riskManager;
    private Parameter parameter;

    // UI 컴포넌트
    private JRadioButton rbPatient, rbDoctor;
    private JTextField tfName, tfBirth;

    public LoginUI(List<Doctor> doctors, List<Patient> patients,
                   RiskManager riskManager, Parameter parameter) {
        this.doctorList = doctors;
        this.patientList = patients;
        this.riskManager = riskManager;
        this.parameter = parameter;

        setTitle("뇌졸중 예방 시스템 - 로그인");
        setSize(350, 280); // 크기 약간 조정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 배치
        setLayout(new BorderLayout());

        // 1. 역할 선택 패널
        JPanel pnlRole = new JPanel();
        pnlRole.setBorder(BorderFactory.createTitledBorder("역할 선택"));

        ButtonGroup bg = new ButtonGroup();
        rbPatient = new JRadioButton("환자 (Patient)", true);
        rbDoctor = new JRadioButton("주치의 (Doctor)");

        bg.add(rbPatient);
        bg.add(rbDoctor);
        pnlRole.add(rbPatient);
        pnlRole.add(rbDoctor);

        // 2. 정보 입력 패널
        JPanel pnlInput = new JPanel(new GridLayout(2, 2, 5, 10));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        pnlInput.add(new JLabel("이름:"));
        tfName = new JTextField();

        pnlInput.add(new JLabel("생년월일(6자리):"));
        tfBirth = new JTextField(); // 예: 900505

        pnlInput.add(tfName);
        pnlInput.add(tfBirth);

        // 3. 로그인 버튼
        JPanel pnlButton = new JPanel();
        JButton btnLogin = new JButton("로그인");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(100, 40));
        btnLogin.addActionListener(e -> processLogin());
        pnlButton.add(btnLogin);
        pnlButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        add(pnlRole, BorderLayout.NORTH);
        add(pnlInput, BorderLayout.CENTER);
        add(pnlButton, BorderLayout.SOUTH);
    }

    private void processLogin() {
        String inputName = tfName.getText().trim();
        String inputBirth = tfBirth.getText().trim();

        if (inputName.isEmpty() || inputBirth.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름과 생년월일을 모두 입력해주세요.",
                    "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 로그아웃 시 실행할 동작 정의 (공통) ---
        // "현재 창이 닫히고 나면, 새로운 LoginUI를 만들어서 보여줘라"
        Runnable logoutAction = () -> {
            SwingUtilities.invokeLater(() -> {
                new LoginUI(doctorList, patientList, riskManager, parameter).setVisible(true);
            });
        };

        if (rbPatient.isSelected()) {
            // [환자 로그인 시도]
            Patient foundPatient = null;
            for (Patient p : patientList) {
                if (p.getName().equals(inputName) && p.getBirthDate().equals(inputBirth)) {
                    foundPatient = p;
                    break;
                }
            }

            if (foundPatient != null) {
                // 로그인 성공 -> 환자 대시보드 실행 (logoutAction 전달)
                MainDashboard dashboard = new MainDashboard(foundPatient, riskManager, parameter, logoutAction);
                dashboard.setLocationRelativeTo(null);
                dashboard.setVisible(true);
                this.dispose(); // 로그인 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "일치하는 환자 정보가 없습니다.\n(이름과 생년월일을 확인하세요)",
                        "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            // [주치의 로그인 시도]
            Doctor foundDoctor = null;
            for (Doctor d : doctorList) {
                if (d.getName().equals(inputName) && d.getBirthDate().equals(inputBirth)) {
                    foundDoctor = d;
                    break;
                }
            }

            if (foundDoctor != null) {
                // 로그인 성공 -> 주치의 대시보드 실행 (logoutAction 전달)
                DoctorDashboard dashboard = new DoctorDashboard(foundDoctor, logoutAction);
                dashboard.setLocationRelativeTo(null);
                dashboard.setVisible(true);
                this.dispose(); // 로그인 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "일치하는 주치의 정보가 없습니다.\n(이름과 생년월일을 확인하세요)",
                        "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}