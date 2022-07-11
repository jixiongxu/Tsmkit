package com.mtp.tsmkit_core.kit;


public interface ITsmKitDispatcher {
    void executeIO(Runnable target);

    void executeCompute(Runnable target);

    void executeAndroid(Runnable target);

    void executeAuto(Runnable target);

    int currentRunOn();

    void setAutoExecutor(IAutoExecutorWrapper executor);
}
