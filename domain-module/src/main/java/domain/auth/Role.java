package domain.auth;

public enum Role {

    ROLE_ADMIN("관리자"),
    ROLE_USER("유저");

    private final String role;

    Role(String role) { this.role = role; }

    public String getRole() { return role; }

}
