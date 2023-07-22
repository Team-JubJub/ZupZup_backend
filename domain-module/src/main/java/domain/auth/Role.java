package domain.auth;

public enum Role {

    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),
    ROLE_SELLER("ROLE_SELLER");

    private final String role;

    Role(String role) { this.role = role; }

    public String getRole() { return role; }

}
