# Idempotent integration with spring-boot
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

idempotent-spring-boot-starter 是分布式系统间调用的幂等实现。

## 使用

### Maven

在pom.xml中加入nexus资源库

```xml
<repositories>
    <repository>
        <id>nexus</id>
        <name>nexus</name>
        <url>http://maven.zhuangjinjin.cn/repository/public</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

在pom.xml中加入依赖

```xml
<dependency>
   <groupId>io.github.ukuz</groupId>
   <artifactId>idempotent-spring-boot-starter</artifactId>
   <version>1.1.0</version>
 </dependency>
```

### Gradle

在build.gradle中加入nexus资源库

```groovy
    repositories {
        mavenLocal()
        maven {url 'http://maven.zhuangjinjin.cn/repository/public'}
        mavenCentral()
    }
```

在build.gradle加入依赖

```groovy
dependencies {
    ...
    compile 'io.github.ukuz:idempotent-spring-boot-starter:1+'
}
```

### Springboot 注解

在Application类上添加`@EnableIdempotent`注解

```java
@SpringBootApplication
@EnableIdempotent
public class FooApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(FooApplication.class).run(args);
    }

}
```

在Controller类上添加`@IdempotentEndPoint`或者在方法上加`@IdempotentEndPoint`，若在类上加`@IdempotentEndPoint`，就想当于所有对外的接口都加上`@IdempotentEndPoint`。

> 注意: 这里只拦截`@RestController`注解的Controller类；还有`@GetMapping`注解的方法，就算加了`@IdempotentEndPoint`也不会触发幂等校验，所以要保证`@GetMapping`注解的方法天然幂等。

```java
@RestController
@RequestMapping("/bar/")
public class BarController {

    @PutMapping("pay")
    @IdempotentEndPoint
    public int pay(@RequestParam int payMoney) {
        int result = balance.addAndGet(-1 * payMoney);
        return result;
    }
}
```

### Springboot 外部化配置

在application.properties中设置相关信息

```properties
#如果没设置，默认采用Redis存储已处理的请求
idempotent.store=redis
#如果没设置，默认幂等的校验过期时间是600秒
idempotent.expire-time=600
```

### 服务调用方

每次发起一次新的请求，需要带上一个`X_REQ_SEQ_ID`请求头参数，值可以用全局唯一ID(UUID)。

## 扩展

### 存储扩展

如果不想采用Redis存储已处理的请求，而打算采用其他存储方案。可以自定义，需要如下步骤

* 自定义一个类实现`io.github.ukuz.idempotent.spring.boot.autoconfigure.core.Store`接口
* 在`META-INF/services`目录下创建一个`io.github.ukuz.idempotent.spring.boot.autoconfigure.core.Store`文件，内容格式`${key}=${value}`，其中`${value}`为实现类的全路径
* 在`application.properties`中添加`idempotent.store=${key}`，这里的`key`就是上一步填写的`key`





