package extension;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ExecutionTimeExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Override
    public void beforeTestExecution(final ExtensionContext context) throws Exception {
        getStore(context).put("_TIME_", System.nanoTime());
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {
        final long duration = System.nanoTime() - getStore(context).get("_TIME_", long.class);
        System.out.println(context.getRequiredTestMethod().getName() + " - " + TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS));
    }

    private ExtensionContext.Store getStore(final ExtensionContext context) {
        return context.getStore(
                ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}