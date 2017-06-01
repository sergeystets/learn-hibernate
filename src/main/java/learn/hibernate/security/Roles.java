package learn.hibernate.security;

public enum Roles {
    USER("MAIN"),
    ADMIN("ADMIN");

    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}

