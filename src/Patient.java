import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Patient {
    private String userID;
    private String name;
    private String birthDate;
    private String doctorWeeklyMemo = "";

    // Patient "has" HealthData (1:N 관계 구현)
    private List<HealthData> healthDataList;
    private List<Risk> riskList;

    public Patient(String userID, String name, String birthDate) {
        this.userID = userID;
        this.name = name;
        this.birthDate = birthDate;
        this.healthDataList = new ArrayList<>();
        this.riskList = new ArrayList<>();
        this.doctorWeeklyMemo = ""; // 초기값은 빈 문자열
    }

    /**
     * Feature 1: 건강 데이터 입력 기능
     * 데이터를 입력받아 저장하고, 즉시 위험도를 분석하여 결과를 반환합니다.
     */
    public boolean inputHealthData(HealthData data, RiskManager riskManager) {
        // 1. 데이터 저장 (메모리에 저장)
        this.healthDataList.add(data);
        System.out.println("[System] " + this.name + "님의 건강 데이터가 저장되었습니다.");

        return true;
    }

    public void performRiskAnalysis(RiskManager manager, Parameter param) {
        if (healthDataList.isEmpty()) {
            System.out.println("분석할 데이터가 없습니다.");
            return;
        }

        // 가장 최근 데이터로 분석 요청
        HealthData recentData = healthDataList.get(healthDataList.size() - 1);

        // Manager에게 분석 위임 -> 결과(Risk)를 받음
        Risk newRisk = manager.calculateRisk(recentData, param, this.userID);

        // 내 리스트에 추가
        this.riskList.add(newRisk);

        System.out.println("[Patient] 위험도 분석 완료 및 저장: " + newRisk.toString());
    }

    public HealthData getLatestHealthData() {
        if (healthDataList.isEmpty()) {
            throw new NoSuchElementException("저장된 건강 데이터가 없습니다.");
        }
        return healthDataList.get(healthDataList.size() - 1);
    }

    public Risk getLatestRisk() {
        if (riskList.isEmpty()) {
            throw new NoSuchElementException("분석된 위험도 이력이 없습니다.");
        }
        return riskList.get(riskList.size() - 1);
    }

    public void printMyHistory() {
        System.out.println("=== " + this.name + "님의 건강 기록 ===");
        for (HealthData data : healthDataList) {
            System.out.println(data.toString());
        }
    }

    //주간 리포트용: 최근 7회 데이터 평균 계산
    public String getWeeklySummary() {
        if (healthDataList.isEmpty()) {
            return "데이터가 충분하지 않습니다.";
        }

        int count = 0;
        double sumSysBP = 0;
        double sumSugar = 0;
        int maxLookBack = 7; // 최근 7개 데이터만

        // 리스트의 뒤에서부터(최신순) 최대 7개 가져오기
        for (int i = healthDataList.size() - 1; i >= 0; i--) {
            HealthData h = healthDataList.get(i);
            sumSysBP += h.getMaxBloodPressure();
            sumSugar += h.getBloodSugar();
            count++;
            if (count >= maxLookBack) break;
        }

        double avgBP = sumSysBP / count;
        double avgSugar = sumSugar / count;

        return String.format(
                "<html>" +
                        "<b>[최근 %d건 기록 평균]</b><br>" +
                        "• 평균 수축기 혈압: %.1f mmHg<br>" +
                        "• 평균 혈당: %.1f mg/dL<br>" +
                        "</html>",
                count, avgBP, avgSugar
        );
    }

    //소견 저장 및 불러오기 메서드 (Getter/Setter)
    public String getDoctorWeeklyMemo() {
        return doctorWeeklyMemo;
    }

    public void setDoctorWeeklyMemo(String memo) {
        this.doctorWeeklyMemo = memo;
        System.out.println("[System] " + this.name + "님의 주간 소견이 업데이트되었습니다.");
    }

    public String getName() {
        return name;
    }
    public String getBirthDate() { return birthDate; }
}