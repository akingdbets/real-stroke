import javax.swing.JOptionPane;
import java.util.UUID;

public class Notification {
    private String notificationId;
    private String message;
    private NotificationType type;

    public Notification(String message, NotificationType type) {
        // ID는 생성 시 자동으로 랜덤 고유값 부여
        this.notificationId = UUID.randomUUID().toString();
        this.message = message;
        this.type = type;
    }

    // 알림을 화면에 띄우는 기능
    public void display() {
        String title = "";
        int msgType = JOptionPane.INFORMATION_MESSAGE;

        if (this.type == NotificationType.RISK) {
            title = "🚨 긴급 위험 경고";
            msgType = JOptionPane.WARNING_MESSAGE;
        } else if (this.type == NotificationType.MEDICATION) {
            title = "💊 복약 알림";
            msgType = JOptionPane.INFORMATION_MESSAGE;
        }

        // 실제 팝업 띄우기
        System.out.println("[Notification Log] ID:" + notificationId + " / Type:" + type);
        JOptionPane.showMessageDialog(null, message, title, msgType);
    }

    // Getters
    public String getNotificationId() { return notificationId; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
}