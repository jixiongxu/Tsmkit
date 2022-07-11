package com.mtp.tsmkit_core.executor;

import com.mtp.tsmkit_core.kit.IAutoExecutorWrapper;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultAutoExecutor implements IAutoExecutorWrapper {

    protected DefaultThreadFactory factory;
    protected ThreadPoolExecutor AutoExecutor;
    private static final int PROCESSORS_COUNT;

    static {
        int number = Runtime.getRuntime().availableProcessors();
        PROCESSORS_COUNT = number < 1 ? 4 : number;
    }

    @Override
    public ThreadPoolExecutor get() {
        return AutoExecutor;
    }

    public DefaultAutoExecutor() {
        AutoExecutor = new ThreadPoolExecutor(2, PROCESSORS_COUNT, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        factory = new DefaultThreadFactory();
        AutoExecutor.setThreadFactory(factory);
    }

    @Override
    public boolean isAutoThread() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        return group == factory.group;
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;

        private DefaultThreadFactory() {
            this.group = new ThreadGroup("DefaultAutoExecutor_Group");
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r);
            t.setName("DefaultAutoExecutor_" + t.hashCode());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }
}
