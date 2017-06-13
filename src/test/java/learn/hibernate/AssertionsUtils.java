package learn.hibernate;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ConditionTimeoutException;
import org.hamcrest.Matcher;

public interface AssertionsUtils {

    class WaitingAssert<T> {

        private final Callable<T> function;

        private WaitingAssert(Callable<T> function) {
            this.function = function;
        }

        public static <T> WaitingAssert assertThatInvocationOf(Callable<T> function) {
            return new WaitingAssert<T>(function);
        }

        @SuppressWarnings("SameParameterValue")
        public void willHangFor(int timeout, TimeUnit unit) {
            Throwable thrown = catchThrowable(() -> waitUntilFinish(function, timeout, unit));
            assertNotNull("Expected method to hang for " + timeout + " " + unit + ", instead got some value", thrown);
            assertThat("Expected method to hang for " + timeout + " " + unit,
                    thrown,
                    instanceOf(ConditionTimeoutException.class));
        }

        private static <T> T waitUntilFinish(final Callable<T> function, int timeout, TimeUnit timeUnit) {
            final long timeoutMillis = timeUnit.toMillis(timeout);
            final long pollMillis = timeoutMillis / 2;
            return await().
                    atMost(timeoutMillis, MILLISECONDS).
                    with().pollInterval(pollMillis, MILLISECONDS).
                    until(function, willReturnAnything());
        }

        // Is the same as org.hamcrest.Matchers.anything().
        // Created purely for the sake of readability.
        private static Matcher<Object> willReturnAnything() {
            return anything();
        }
    }
}