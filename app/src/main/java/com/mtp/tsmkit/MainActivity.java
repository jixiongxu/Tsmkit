package com.mtp.tsmkit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mtp.tsmkit.kotlin.MainDev;
import com.mtp.tsmkit_core.annotation.TsmKit;
import com.mtp.tsmkit_core.annotation.RunType;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_Log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test("66666", 55);
        test2();
        test3();
        test4();
        testStatic();

        MainDev mainDev = new MainDev();
        mainDev.hello();
    }

    @TsmKit(dispatcher = RunType.AndroidMain)
    public void setMsg(String msg) {
        TextView viewById = findViewById(R.id.tv_msg);
        viewById.setText(msg);
    }

    @TsmKit(dispatcher = RunType.IO)
    public void setMsg(String msg, String ms) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @TsmKit(dispatcher = RunType.IO)
    public void test(String value, int index) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "test run on:" + Thread.currentThread().getName() + "---value:" + value + "--index:" + index);
        setMsg("hello TsmKit");
    }

    @TsmKit(dispatcher = RunType.CPU)
    public static void testStatic() {
        Log.d(TAG, "testStatic:" + Thread.currentThread().getName());
    }

    @TsmKit(dispatcher = RunType.CPU)
    public void test2() {
        Log.d(TAG, "test2 run on:" + Thread.currentThread().getName());
    }

    @TsmKit(dispatcher = RunType.AndroidMain)
    public void test3() {
        Log.d(TAG, "test3 run on:" + Thread.currentThread().getName());
    }

    @TsmKit(dispatcher = RunType.Auto)
    public void test4() {
        Log.d(TAG, "test4 run on:" + Thread.currentThread().getName());
    }

}