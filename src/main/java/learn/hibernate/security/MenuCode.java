package learn.hibernate.security;

/**
 * @author Sergii Stets
 *         Date 31.05.2017
 */
public enum MenuCode {

    EQ("EQ"),
    WORK_WITH_WEEKS_OF_SUPPLY("BK_01"), // work with supply tables (EQ-> 04 -> 50 ->01)
    ORDER_PARAMETERS("BK_02"); // work with supply tables (EQ-> 04 -> 50 ->02)

    private final String value;

    MenuCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
