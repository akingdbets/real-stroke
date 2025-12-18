import java.time.LocalDate;

public class HealthData {
    private String healthDataID;
    private String userID;
    private LocalDate recordDate;

    private boolean smokingStatus;
    private float bloodSugar;
    private String bodyComposition;
    private int activityLevel;
    private int maxBloodPressure; // 최고 혈압만 유지
    private boolean medicationTaken;

    public HealthData(String healthDataID, String userID, boolean smokingStatus, float bloodSugar,
                      String bodyComposition, int activityLevel, int maxBloodPressure) {
        this.healthDataID = healthDataID;
        this.userID = userID;
        this.recordDate = LocalDate.now();
        this.smokingStatus = smokingStatus;
        this.bloodSugar = bloodSugar;
        this.bodyComposition = bodyComposition;
        this.activityLevel = activityLevel;
        this.maxBloodPressure = maxBloodPressure;
        this.recordDate = LocalDate.now(); // 기본은 오늘 날짜

        // 초기값은 false (아직 복용 체크 안 함)
        this.medicationTaken = false;
    }

    // [기능 추가] 약물 복용 여부를 업데이트하는 함수
    public void updateMedicationStatus(boolean isTaken) {
        this.medicationTaken = isTaken;
        System.out.println(">> 약물 복용 상태가 업데이트 되었습니다: " + isTaken);
    }

    // [추가] 테스트용: 날짜를 강제로 변경하는 메서드
    public void setRecordDate(LocalDate date) {
        this.recordDate = date;
    }

    // Getters
    public boolean isSmokingStatus() { return smokingStatus; }
    public float getBloodSugar() { return bloodSugar; }
    public int getActivityLevel() { return activityLevel; }
    public int getMaxBloodPressure() { return maxBloodPressure; }
    public boolean isMedicationTaken() { return medicationTaken; }
    public LocalDate getRecordDate() {return recordDate;}
}