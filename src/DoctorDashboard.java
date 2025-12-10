import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DoctorDashboard extends JFrame {
    private Doctor doctor;
    private JTable patientTable;
    private DefaultTableModel tableModel;

    public DoctorDashboard(Doctor doctor) {
        this.doctor = doctor;

        setTitle("주치의 전용 패널 - " + doctor.getName() + " 선생님");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 상단: 정렬 및 제어 패널
        JPanel topPanel = new JPanel();
        JButton btnSortRisk = new JButton("🚨 위험도 순 정렬");
        JButton btnSortName = new JButton("가나다 순 정렬");
        JButton btnRefresh = new JButton("새로고침");

        btnSortRisk.addActionListener(e -> updateTable(true));   // 위험도 정렬
        btnSortName.addActionListener(e -> updateTable(false));  // 이름 정렬
        btnRefresh.addActionListener(e -> updateTable(false));

        topPanel.add(new JLabel("목록 정렬: "));
        topPanel.add(btnSortRisk);
        topPanel.add(btnSortName);
        topPanel.add(btnRefresh);
        add(topPanel, BorderLayout.NORTH);

        // 2. 중앙: 환자 리스트 테이블
        String[] columns = {"환자 이름", "현재 위험도", "위험 등급", "최근 기록일"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 수정 불가
            }
        };

        patientTable = new JTable(tableModel);
        add(new JScrollPane(patientTable), BorderLayout.CENTER);

        // [핵심 기능 2] 테이블 더블 클릭 시 상세 리포트 팝업
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

        // 초기 데이터 로드
        updateTable(false);
    }

    // 테이블 데이터 갱신 메서드
    private void updateTable(boolean sortByRisk) {
        // 테이블 초기화
        tableModel.setRowCount(0);

        List<Patient> list;
        if (sortByRisk) {
            list = doctor.getPatientsSortedByRisk();
        } else {
            list = doctor.getManagedPatients();
            // 이름순 정렬 필요 시 Collections.sort 추가 가능
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
                // 데이터 없음
            }

            Object[] rowData = {p.getName(), riskScore, riskLevel, date};
            tableModel.addRow(rowData);
        }
    }

    // 상세 화면 열기
    private void openPatientDetail(String patientName) {
        // 이름으로 환자 찾기 (실제로는 ID로 찾는 게 안전함)
        for (Patient p : doctor.getManagedPatients()) {
            if (p.getName().equals(patientName)) {
                PatientDetailDialog dialog = new PatientDetailDialog(this, p);
                dialog.setVisible(true);
                return;
            }
        }
    }
}