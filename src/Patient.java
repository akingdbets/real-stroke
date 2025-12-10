import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Patient {
    private String userID;
    private String name;
    private String birthDate;

    // Patient "has" HealthData (1:N 관계 구현)
    private List<HealthData> healthDataList;
    private List<Risk> riskList;

    public Patient(String userID, String name, String birthDate) {
        this.userID = userID;
        this.name = name;
        this.birthDate = birthDate;
        this.healthDataList = new ArrayList<>();
        this.riskList = new ArrayList<>();
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

    public String getName() {
        return name;
    }
    public String getBirthDate() { return birthDate; }
}