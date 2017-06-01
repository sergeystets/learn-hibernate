package learn.hibernate.security;

/**
 * @author Sergii Stets
 *         Date 31.05.2017
 */
public enum MenuCode {

    EQ("EQ"),
    RF("RF"),
    BATCH("BATCH");

    private final String value;

    MenuCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
