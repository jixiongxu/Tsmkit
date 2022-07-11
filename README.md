# TsmKit线程切换框架

### 背景
#### 1.1 Android开发的特殊背景
很多Android开发者都知道，我们APP的运行都是在主线程中通过Handler去postMessage达到事件的传递，比如说生命周期的切换，点击事件的传递和驱动，以及接收/发送广播等操作。那么这个时候就会有要求我们的主线程不能发生堵塞，无论是因为CPU过忙或者某些方法block/sleep都会导致出现ANR。但是实际情况下我们会有许多的操作是耗时操作，这个时候就需要用到子线程去做这些耗时操作，这些耗时操作完成后，有可能又要切换到主线程或者其他线程中继续执行。如果用Android的方式去切换线程，代码如下：
```java

  new Thread(new Runnable() {
            @Override
            public void run() {
                doSomething();
                Handler handler  = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mainThreadRunning();
                    }
                });
            }
        }).start();        
        
```
可以看到代码量还是非常的多，因此催生出本文描述的进程切换框架TsmKit。

#### 1.2 市场情况
现在我们能找到的比较热门的线程切换框架大致有两种：
**1、RxJava**
```java
Observable<String> observable = Observable.create(
        (ObservableEmitter<String> emitter) -> {
            log("emit thread=" + Thread.currentThread().getName());
            emitter.onNext("A");
        }
);
observable = observable.subscribeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .observeOn(Schedulers.single());
observable.subscribe((String s) -> log("onNext1:" + s + " thread=" + Thread.currentThread().getName()));
```
我们使用subscribeOn和observeOn后，分别可以指定我们各个阶段的运行的线程，但是同时我们可以看到，代码量并没有跟安卓原生切换少很多。这是因为我们的RxJava虽然具有线程切换的能力，但是这个能力并不是他所强项，他的强项是优化我们的开发流程，让我们的代码呈现出响应式、链条式编程思想，方便开发者去理解每个阶段的工作。


**2、Kotlin协程**
```java
GlobalScope.launch {
        doSomething()
    }
    private suspend fun doSomething() {
    }
```
协程的代码简单了许多，但是相对于TsmKit还是有比较多的繁琐，于此同时，协程在Java上并不支持。（由于Kotlin笔者并没有接触太多，更深入的比较久不再进行，同时这里的描述如果不准确也请提出意见）


### TsmKit使用
由于现在还没有发布到Maven，所以只能自建本地Maven。
可以通过下载[ Tsmkit ](https://github.com/jixiongxu/TsmKit) 下载完run tsmkit_support/build.gradle 的 uploadArchives方法完成本地Maven创建即可。

使用方式可以参考MainActivity.java

```java
    @TsmKit(dispatcher = Type.IO)
    public void test() {
        Log.d("tsmkit", "run on:" + Thread.currentThread().getName());
    }
```

需要注意的是TsmKit目前不支持private方法，不支持有return的方法。

### 实现原理
通过对源码的字节码进行侵入插桩，改变原有方法的执行逻辑。插桩前后对比如下：

```java
// 插桩前
    @TsmKit(dispatcher = Type.AndroidMain)
    public void test3(String var) {
        Log.d("xujixiong", "run on:" + Thread.currentThread().getName() + "msg:" + var);
    }

```

```java
// 插桩后
 @TsmKit(dispatcher = Type.AndroidMain)
 public void test3(String var) {
        int var2 = TsmKitManager.getInstance().currentRunOn();
        if (var2 != 0) {
            test3TsmRunnableImp var3 = new test3TsmRunnableImp(var);
            var3.setTarget(this);
            TsmKitManager.getInstance().executeAndroid(var3);
        } else {
            Log.d("xujixiong", "run on:" + Thread.currentThread().getName() + "msg:" + var);
        }
    }

```

从上面的插桩代码可以看出，对当前运行的线程进行了判断，如果当前所运行的线程是我们注解中所指定的线程，那么走到else继续执行原有代码，否则将test3TsmRunnableImp抛出去执行。test3TsmRunnableImp这个类也是我们插桩生成的，具体如下：   

```java
package com.mtp.tsmkit;

public class test3TsmRunnableImp implements Runnable {
    private String var_tsm0;
    private MainActivity target;

    public test3TsmRunnableImp(String var1) {
        this.var_tsm0 = var1;
    }

    public void setTarget(MainActivity var1) {
        this.target = var1;
    }

    public void run() {
        this.target.test3(this.var_tsm0);
    }
}

```

主要看到run方法，其实还是去调用我们目标函数，只是抛出去的目的是为了切换到指定线程执行。
具体的执行过程和判断当前线程方式可以看 **DefaultITsmKitDispatcher**（也很重要）代码。



### 总结
Tsmkit很大程度可以减少我们的代码量，并且我们没有使用到反射等消耗性能的操作。但是由于使用到字节码插桩技术，可能会在一些Java版本水土不服，由于还没使用到真正的项目当中，我们无法保证该框架的稳定性和兼容性。


### 下载地址
**[TsmKit Github](https://github.com/jixiongxu/TsmKit)**


欢迎大家一建三连




磕头
