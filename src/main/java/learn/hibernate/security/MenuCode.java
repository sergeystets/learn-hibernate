package learn.hibernate.security;

/**
 * @author Sergii Stets
 *         Date 31.05.2017
 */
public enum MenuCode {

    USER("USER");

    private final String user;

    MenuCode(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
