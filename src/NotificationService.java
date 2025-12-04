// 알림 발송 인터페이스 (나중에 SMS, 이메일 등으로 교체 가능)
public interface NotificationService {
    void sendAlert(String userId, String message);
}

// 콘솔에 출력하는 단순 구현체
class ConsoleNotificationService implements NotificationService {
    @Override
    public void sendAlert(String userId, String message) {
        System.out.println("\n[🚨 긴급 알림] User(" + userId + "): " + message);
        System.out.println(">> 즉시 병원을 방문하거나 보호자에게 연락하세요!\n");
    }
}