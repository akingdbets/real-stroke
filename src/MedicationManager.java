public class MedicationManager {

    private Medication targetMedication;


    public void manageSet(Medication medication) {
        this.targetMedication = medication;
        System.out.println("[Manager] 복약 관리 설정 완료: " + medication.getMedicineName());
    }

    // 원래는 스케줄러를 시작하는 거지만, 여기서는 '시뮬레이션 시작'으로 구현
    public void startManagement(String simulatedTime) {
        // 설정된 약이 없으면 중단
        if (this.targetMedication == null) {
            System.out.println("(!) 설정된 약이 없습니다. manageSet을 먼저 호출하세요.");
            return;
        }

        System.out.println("[Simulation] 관리 시작 (현재 시각: " + simulatedTime + ")");

        if (simulatedTime.equals(this.targetMedication.getScheduledTime())) {
            String msg = "💊 [복약 시간] '" + this.targetMedication.getMedicineName() +
                    "' 복용 시간입니다.\n(식후 30분 이내 복용 권장)";

            // Notification 객체 생성 및 알림 발생
            Notification noti = new Notification(msg, NotificationType.MEDICATION);
            noti.display();
        } else {
            System.out.println(">> 아직 복용 시간이 아닙니다.");
        }
    }
}