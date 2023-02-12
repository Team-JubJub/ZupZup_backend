package domain.order.type;

public enum OrderStatus {

    // 확정, 완료, 취소, 신규
    CONFIRM("주문 확정"),
    PARTIAL("부분 확정"),
    CANCEL("주문 취소"),
    COMPLETE("주문 완료"),
    NEW("신규 주문");

    private final String status;

    OrderStatus(String status) { this.status = status; }

    public String getStatus() { return status; }
}
