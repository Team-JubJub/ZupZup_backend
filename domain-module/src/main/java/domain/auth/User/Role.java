package domain.auth.User;

public enum Role {

    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    private final String role;

    Role(String role) { this.role = role; }

    public String getRole() { return role; }

}
