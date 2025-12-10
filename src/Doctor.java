import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class Doctor {
    private String doctorId;
    private String name;
    private List<Patient> managedPatients; // DB 대신 리스트로 관리

    public Doctor(String doctorId, String name) {
        this.doctorId = doctorId;
        this.name = name;
        this.managedPatients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    // 환자 추가 (연관관계 설정)
    public void addPatient(Patient patient) {
        this.managedPatients.add(patient);
    }

    public List<Patient> getManagedPatients() {
        return managedPatients;
    }

    // [핵심 기능 1] 위험도 순으로 환자 정렬하여 반환
    public List<Patient> getPatientsSortedByRisk() {
        List<Patient> sortedList = new ArrayList<>(this.managedPatients);

        // 내림차순 정렬 (위험도 높은 순)
        Collections.sort(sortedList, new Comparator<Patient>() {
            @Override
            public int compare(Patient p1, Patient p2) {
                double score1 = 0.0;
                double score2 = 0.0;

                try { score1 = p1.getLatestRisk().getTotalScore(); } catch (Exception e) {}
                try { score2 = p2.getLatestRisk().getTotalScore(); } catch (Exception e) {}

                return Double.compare(score2, score1); // 내림차순
            }
        });
        return sortedList;
    }
}