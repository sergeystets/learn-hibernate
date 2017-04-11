package learn.hibernate;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.assertj.core.api.Condition;

public interface AssertJConditions {

    /**
     * This condition uses reflection to determine if the two Objects are equal.
     *
     * @param expected expected object
     * @param <T>      type of the expected
     * @return 'equal to' condition
     */
    static <T> Condition<T> equalTo(T expected) {
        return new Condition<T>() {
            @Override
            public boolean matches(T actual) {
                return EqualsBuilder.reflectionEquals(expected, actual);
            }
        };
    }
}
