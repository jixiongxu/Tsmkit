package com.mtp.tsmkit_core.kit;

import java.util.concurrent.ThreadPoolExecutor;

public interface IAutoExecutorWrapper {

    ThreadPoolExecutor get();

    boolean isAutoThread();
}
