# TsmKit线程切换框架

### 说明
Tsmkit是一个专注于异步工具库，可以使得在可以简单快速的将方法切换到对应的线程中执行。

### 接入：

工程下的build.gradle
```groovy
buildscript {
    repositories {
        maven { url "https://plugins.gradle.org/m2/" }
    }
    dependencies {

    }
}
```

module下的build.gradle
```groovy
apply plugin: "io.github.jixiongxu.tsmkit"

dependencies {
    implementation 'io.github.jixiongxu:tsmkit:1.0.9'
}
```
### 使用
方法在IO线程池中执行
```java
    @TsmKit(dispatcher = RunType.IO)
    public void test2() {
        Log.d(TAG, "test2 run on:" + Thread.currentThread().getName());
    }
```

