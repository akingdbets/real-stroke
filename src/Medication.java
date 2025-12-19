public class Medication {
    private String medicineName;  // 약 이름
    private String scheduledTime; // 복용 시간 (예: "09:00")

    public Medication(String medicineName, String scheduledTime) {
        this.medicineName = medicineName;
        this.scheduledTime = scheduledTime;
    }

    public String getMedicineName() { return medicineName; }
    public String getScheduledTime() { return scheduledTime; }
}