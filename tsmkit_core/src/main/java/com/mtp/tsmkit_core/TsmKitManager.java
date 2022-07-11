package com.mtp.tsmkit_core;

import com.mtp.tsmkit_core.dispatcher.AndroidITsmKitDispatcher;
import com.mtp.tsmkit_core.kit.IAutoExecutorWrapper;
import com.mtp.tsmkit_core.kit.ITsmKitDispatcher;

public final class TsmKitManager implements ITsmKitDispatcher {

    private volatile static TsmKitManager manager;
    private static ITsmKitDispatcher iTsmKitDispatcherImp;

    private TsmKitManager() {
        iTsmKitDispatcherImp = new AndroidITsmKitDispatcher();
    }

    public static TsmKitManager getInstance() {
        if (manager == null) {
            synchronized (TsmKitManager.class) {
                if (manager == null) {
                    manager = new TsmKitManager();
                }
            }
        }
        return manager;
    }

    @Override
    public void executeIO(Runnable target) {
        iTsmKitDispatcherImp.executeIO(target);
    }

    @Override
    public void executeCompute(Runnable target) {
        iTsmKitDispatcherImp.executeCompute(target);
    }

    @Override
    public void executeAndroid(Runnable target) {
        iTsmKitDispatcherImp.executeAndroid(target);
    }

    @Override
    public void executeAuto(Runnable target) {
        iTsmKitDispatcherImp.executeAuto(target);
    }

    @Override
    public int currentRunOn() {
        return iTsmKitDispatcherImp.currentRunOn();
    }

    @Override
    public void setAutoExecutor(IAutoExecutorWrapper executor) {
        iTsmKitDispatcherImp.setAutoExecutor(executor);
    }
}
