
# 流量控制 - 避免微服务过载运行 - servicecomb-qps-flowcontrol

> 服务提供者提供接口，公共网关进行调用，公共网关中配置该服务Zuul的路由规则，并编写html文件对该服务进行调用，由于该服务配置了流量控制，所以连续点击会提示错误信息


### 1. 新建servicecomb-flowcontrol-provider项目

##### 1. 填充POM文件
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

    <!-- servicecomb 流量控制所需依赖、必须 -->
    <dependency>
        <groupId>org.apache.servicecomb</groupId>
        <artifactId>handler-flowcontrol-qps</artifactId>
    </dependency>

</dependencies>
```

##### 2. 新建log4j2.xml、microservice.yaml配置文件
```yaml
APPLICATION_ID: servicecomb
service_description:
  name: flowControlProvider
  version: 0.0.1
  environment: development
servicecomb:
  service:
    registry:
      address: http://127.0.0.1:30100
  rest:
    address: 0.0.0.0:8080
  highway:
    address: 0.0.0.0:7070
  handler:
    chain:
      Provider:
        default: qps-flowcontrol-provider
  flowcontrol:
    Provider:
      qps:
        enabled: true
        limit:
          gateway: 1

```
> 注意： provider端限流策略配置中的ServiceName指的是调用该provider的consumer，而schema、operation指的是provider自身的。即provider端限流配置的含义是，限制指定consumer调用本provider的某个schema、operation的流量。

##### 3. 编写rest接口为网关调用
```java
@RestSchema(schemaId = "RestSchemaId")
@RequestMapping("/")
public class FlowControlCallSchema implements IFlowControlCallSchema {

    @GetMapping(path = "/gateway")
    @Override
    public String callFlowControl() {
        return "FlowControlCallSchemaId";
    }
}
```

##### 4. 编写启动类
```java
public class FlowControlProviderApplication {

    public static void main(String[] args) throws Exception {
        Log4jUtils.init();
        BeanUtils.init();
    }

}
```

##### 5. 为网关新增servicecomb-qps-flowcontrol服务路由
```yaml
zuul:
  routes:
    myConsumer: /myConsumer/**
    flowControlProvider: /flowControlProvider/**
```

##### 6. 为网关服务新增一个测试该流量控制的html页面
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Test Call Gateway Servicecomb qps flowcontrol Server</title>
    <script src="https://code.jquery.com/jquery-3.2.1.min.js" crossorigin="anonymous"></script>
</head>
<body>
<div style="display: flex;justify-content: center;align-items: center;">
    <button id="call">Call Gateway Servicecomb qps flowcontrol Server</button>
    <div id="result"></div>
</div>
</body>
<script>
    $("#call").click(function () {
        $('#result').html("")
        $.ajax({
          url: "/flowControlProvider/gateway",
          type: "GET",
          success: function (data) {
            $("#result").append(data);
          },
          error : function(e) {
            alert("出错");
            alert(e.responseText);
          },
        });
      });

</script>
</html>

```

##### 7. 连续点击页面按钮，会提示错误信息
