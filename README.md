# TsmKit

### 说明
Tsmkit是用于方法的异步库，可以简单快速的将方法切换到对应的线程中执行。

### 接入：

项目工程下的build.gradle
```groovy
buildscript {
    repositories {
        maven { url "https://plugins.gradle.org/m2/" }
    }
    dependencies {
        classpath "io.github.jixiongxu.tsmkit:tsmkit_support:1.0.9"
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

### 注意
**@Tsmkit的方法不支持return**\
**@Tsmkit的方法不支持private**



