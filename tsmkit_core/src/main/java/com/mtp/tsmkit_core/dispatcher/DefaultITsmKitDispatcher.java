package com.mtp.tsmkit_core.dispatcher;

import com.mtp.tsmkit_core.kit.IAutoExecutorWrapper;
import com.mtp.tsmkit_core.kit.ITsmKitDispatcher;
import com.mtp.tsmkit_core.util.RunEnum;
import com.mtp.tsmkit_core.executor.DefaultAutoExecutor;
import com.mtp.tsmkit_core.executor.DefaultCPUExecutor;
import com.mtp.tsmkit_core.executor.DefaultIOExecutor;

public class DefaultITsmKitDispatcher implements ITsmKitDispatcher {

    private static final DefaultIOExecutor IO = new DefaultIOExecutor();
    private static final DefaultCPUExecutor CPU = new DefaultCPUExecutor();
    private IAutoExecutorWrapper SELF = new DefaultAutoExecutor();

    @Override
    public final void executeIO(Runnable target) {
        IO.get().execute(target);
    }

    @Override
    public final void executeCompute(Runnable target) {
        CPU.get().execute(target);
    }

    @Override
    public void executeAndroid(Runnable target) {
        try {
            throw new Exception("not support executeAndroid on DefaultITsmKitDispatcher");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeAuto(Runnable target) {
        SELF.get().execute(target);
    }

    @Override
    public int currentRunOn() {
        if (IO.isIOThread()) {
            return RunEnum.RUN_ON_IO;
        }
        if (CPU.isCPUThread()) {
            return RunEnum.RUN_ON_CPU;
        }
        if (SELF.isAutoThread()) {
            return RunEnum.RUN_ON_AUTO;
        }
        return RunEnum.RUN_ON_MAIN;
    }

    @Override
    public void setAutoExecutor(IAutoExecutorWrapper executor) {
        SELF.get().shutdown();
        SELF = executor;
    }
}
