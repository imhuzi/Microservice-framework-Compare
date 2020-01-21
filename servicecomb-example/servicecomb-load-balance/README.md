

## Servicecomb-load-balance

###servicecomb负载均衡事例

> 该项目包含两个子项目，一个为服务提供者，另一个为服务消费者
> 服务提供者会使用不同的端口启动两次，消费方根据服务名称调用服务，达到均衡调用服务的目的

#### 1. 创建服务提供者项目[servicecomb-load-balance-provider](./servicecomb-load-balance-provider)

##### 1. 编写microservice.yaml、log4j2.xml、pom.xml文件

```yaml
# microservice.yaml中去掉了rest.address 的配置，该配置会使用maven启动命令补充
APPLICATION_ID: servicecomb
service_description:
  name: loadBalanceProvider
  version: 0.0.1
  environment: development
servicecomb:
  service:
    registry:
      address: http://127.0.0.1:30100
```

> POM文件
```
<dependencies>

    <!-- 该依赖为父级项目maven依赖 -->
    <dependency>
        <groupId>com.uyibai.demo</groupId>
        <artifactId>servicecomb-common-schema</artifactId>
    </dependency>

    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>provider-pojo</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>provider-springmvc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>transport-highway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>transport-rest-vertx</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
    </dependency>

</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```
##### 2. 根据ILoadBalanceCallSchema接口编写一个实现方法，返回不同的instanceId，辨别不同服务
```java
@RpcSchema(schemaId = "ILoadBalanceCallSchemaId")
public class LBRpcHello implements ILoadBalanceCallSchema {

    public String callLoadBalance() {
        /**
         * 获取实例ID
         */
        MicroserviceInstance instance = RegistryUtils.getMicroserviceInstance();
        if (instance == null) {
            throw new IllegalStateException(
                    "unable to find any service instances, maybe there is problem registering in service center?");
        }
        return instance.getInstanceId();
    }

}
```

```java
// 服务启动类
public class LBProviderApplication {

    public static void main(String[] args) throws Exception {
        Log4jUtils.init();
        BeanUtils.init();
    }

}
```


#### 2. 创建服务消费者项目[servicecomb-load-balance-consumer](./servicecomb-load-balance-consumer)

##### 1. 编写POM、microservice.yaml、log4j2.xml

```yaml
APPLICATION_ID: servicecomb
service_description:
  name: loadBalanceConsumer
  version: 0.0.1
  environment: development
servicecomb:
  service:
    registry:
      address: http://127.0.0.1:30100
  references:
    loadBalanceProvider:  # 需要根据服务提供者的服务名称进行修改
      version-rule: 0.0.1
```

> POM文件
```
<dependencies>

    <dependency>
        <groupId>com.uyibai.demo</groupId>
        <artifactId>servicecomb-common-schema</artifactId>
        <scope>compile</scope>
    </dependency>

    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>handler-bizkeeper</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>handler-loadbalance</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>transport-highway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>transport-rest-vertx</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>handler-flowcontrol-qps</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>provider-springmvc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>provider-pojo</artifactId>
    </dependency>
    <!--log4j2-->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

##### 2. 编写启动类，并在启动类中调用服务提供者暴露的接口

```java
@Component
public class LBConsumerApplication {

    @RpcReference(microserviceName = "loadBalanceProvider", schemaId = "ILoadBalanceCallSchemaId")
    private static ILoadBalanceCallSchema loadBalance;

    public static void main(String[] args) throws InterruptedException {

        BeanUtils.init();

        while (true){
            /**
             * 持续调用，查看负载情况
             */
            System.out.println(loadBalance.callLoadBalance());
            Thread.sleep(1000);
        }

    }

}

```

#### 3. 分别启动两个服务提供者服务、启动服务消费服务

##### 1. 启动服务提供者服务、使用Maven命令注入不同监听端口启动

```
# servicecomb-load-balance-provider-1
spring-boot:run -Dspring-boot.run.jvmArguments=-Dcse.rest.address=0.0.0.0:7777

# servicecomb-load-balance-provider-2
spring-boot:run -Dspring-boot.run.jvmArguments=-Dcse.rest.address=0.0.0.0:7778
```

##### 2. 启动消费者服务，启动成功后会一直调用服务提供者所提供的接口、并输出不同的InstenceId

```
# 消费服务启动会会从服务中心获取全部服务数据
[2020-01-02 15:20:53,704][INFO][main][MicroserviceVersionRule.java][]update instances, appId=servicecomb, microserviceName=loadBalanceProvider, versionRule=0.0.1.0, latestVersion=0.0.1.0, inputVersionCount=1, inputInstanceCount=2
  0.instanceId=18cbca402d3011ea9c620242ac110002, status=UP, version=0.0.1.0, endpoints=[rest://192.168.74.141:7778], environment=development, framework.name=servicecomb-java-chassis, framework.version=ServiceComb:1.3.0
  1.instanceId=1e68046e2d3011ea9c620242ac110002, status=UP, version=0.0.1.0, endpoints=[rest://192.168.74.141:7777], environment=development, framework.name=servicecomb-java-chassis, framework.version=ServiceComb:1.3.0 org.apache.servicecomb.serviceregistry.consumer.MicroserviceVersionRule.printData(MicroserviceVersionRule.java:145)

# 服务启动成功后会持续输出以上Id
18cbca402d3011ea9c620242ac110002
1e68046e2d3011ea9c620242ac110002
18cbca402d3011ea9c620242ac110002
1e68046e2d3011ea9c620242ac110002
```
