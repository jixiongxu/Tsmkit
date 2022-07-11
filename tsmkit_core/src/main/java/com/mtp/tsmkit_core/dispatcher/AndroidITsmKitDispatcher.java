package com.mtp.tsmkit_core.dispatcher;

import android.os.Handler;
import android.os.Looper;

import com.mtp.tsmkit_core.util.RunEnum;

public class AndroidITsmKitDispatcher extends DefaultITsmKitDispatcher {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void executeAndroid(Runnable target) {
        handler.post(target);
    }

    @Override
    public int currentRunOn() {
        Thread thread = Thread.currentThread();
        if (thread == Looper.getMainLooper().getThread()) {
            return RunEnum.RUN_ON_MAIN;
        }
        return super.currentRunOn();
    }
}
