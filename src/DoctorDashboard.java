import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DoctorDashboard extends JFrame {
    private Doctor doctor;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private Runnable logoutAction; // [추가] 로그아웃 시 실행할 동작

    // [수정] 생성자에 Runnable logoutAction 추가
    public DoctorDashboard(Doctor doctor, Runnable logoutAction) {
        this.doctor = doctor;
        this.logoutAction = logoutAction;

        setTitle("주치의 전용 패널 - " + doctor.getName() + " 선생님");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 상단: 정렬 및 제어 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnSortRisk = new JButton("🚨 위험도 순");
        JButton btnSortName = new JButton("📝 가나다 순");
        JButton btnRefresh = new JButton("새로고침");
        JButton btnLogout = new JButton("로그아웃"); // [추가] 뒤로가기 버튼

        // 스타일링 (옵션)
        btnSortRisk.setBackground(new Color(255, 200, 200));
        btnLogout.setBackground(Color.LIGHT_GRAY);

        btnSortRisk.addActionListener(e -> updateTable(true));
        btnSortName.addActionListener(e -> updateTable(false));
        btnRefresh.addActionListener(e -> updateTable(false));

        // [추가] 로그아웃 버튼 동작
        btnLogout.addActionListener(e -> {
            this.dispose(); // 현재 창 닫기
            logoutAction.run(); // 로그인 창 다시 열기
        });

        topPanel.add(new JLabel(" 정렬: "));
        topPanel.add(btnSortRisk);
        topPanel.add(btnSortName);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL)); // 구분선
        topPanel.add(btnRefresh);
        topPanel.add(btnLogout); // 상단에 버튼 추가

        add(topPanel, BorderLayout.NORTH);

        // 2. 중앙: 환자 리스트 테이블
        String[] columns = {"환자 이름", "생년월일", "현재 위험도", "위험 등급", "최근 기록일"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(25);
        add(new JScrollPane(patientTable), BorderLayout.CENTER);

        // 테이블 더블 클릭 이벤트
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = patientTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String patientName = (String) tableModel.getValueAt(selectedRow, 0);
                        openPatientDetail(patientName);
                    }
                }
            }
        });

        // 초기 데이터 로드 (이름순)
        updateTable(false);
    }

    // 테이블 데이터 갱신 메서드
    private void updateTable(boolean sortByRisk) {
        tableModel.setRowCount(0);

        // 리스트 복사 (원본 보호)
        List<Patient> list = new java.util.ArrayList<>(doctor.getManagedPatients());

        if (sortByRisk) {
            // [위험도 순 정렬]
            Collections.sort(list, new Comparator<Patient>() {
                @Override
                public int compare(Patient p1, Patient p2) {
                    double s1 = 0, s2 = 0;
                    try { s1 = p1.getLatestRisk().getTotalScore(); } catch(Exception e){}
                    try { s2 = p2.getLatestRisk().getTotalScore(); } catch(Exception e){}
                    return Double.compare(s2, s1); // 내림차순
                }
            });
        } else {
            // [가나다 순 정렬] - String.compareTo 사용
            Collections.sort(list, new Comparator<Patient>() {
                @Override
                public int compare(Patient p1, Patient p2) {
                    return p1.getName().compareTo(p2.getName()); // 오름차순 (가나다)
                }
            });
        }

        for (Patient p : list) {
            String riskScore = "-";
            String riskLevel = "-";
            String date = "-";

            try {
                Risk r = p.getLatestRisk();
                riskScore = String.format("%.1f", r.getTotalScore());
                riskLevel = r.getRiskLevel();
                date = r.getAssessedAt().toLocalDate().toString();
            } catch (Exception e) {
            }

            Object[] rowData = {p.getName(), p.getBirthDate(), riskScore, riskLevel, date};
            tableModel.addRow(rowData);
        }
    }

    private void openPatientDetail(String patientName) {
        for (Patient p : doctor.getManagedPatients()) {
            if (p.getName().equals(patientName)) {
                PatientDetailDialog dialog = new PatientDetailDialog(this, p);
                dialog.setLocationRelativeTo(this); // 부모 창 중앙에 띄우기
                dialog.setVisible(true);
                return;
            }
        }
    }
}