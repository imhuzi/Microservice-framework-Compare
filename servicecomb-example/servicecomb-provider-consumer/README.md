# 简单的服务调用 - servicecomb-provider-consumer

#### 项目目录结构

```
|- servicecomb-provider-consumer  # servicecomb 注册发现demo
|--- servicecomb-consumer        # 调用register中所提供的接口
|--- servicecomb-provider        # 提供接口为consumer调用
```

##### 服务提供者提供的服务契约有两种形式
- 放置在"resources/microservices"或者"resources/application"目录的契约文件(替代服务中心)
- 隐式契约形式（本次代码中使用的都是通过Annotation声明的隐式契约）

#### 1. 新建服务提供者项目

##### 1. 服务的父工程中添加自己创建的[servicecomb-common-schema的依赖](../pom.xml)，为服务调用提供接口依赖

##### 2. 新建servicecomb-provider项目，并添加servicecomb相关依赖，pom文件为
```
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
```
##### 3. 处理[log4j](src/main/resources/log4j2.xml)配置文件

##### 4. servicecomb-common-schema项目中新增服务调用接口
```java
/**
 * 为servicecomb-registered-found项目提供接口
 **/
public interface ICallSchema {


    String sayHi(String name);

    String sayHello(Person person);

}
```

##### 5. servicecomb-provider项目resources下新建microservice.yaml
```yaml
APPLICATION_ID: servicecomb
service_description:
  name: myProvider  # servicecomb 注册到注册中心的服务名，其他项目可以根据该name获取相应暴露对象
  version: 0.0.1
  environment: development
servicecomb:
  service:
    registry:
      address: http://127.0.0.1:30100 # 注册中心地址
  rest:
    address: 0.0.0.0:8080
  highway:
    address: 0.0.0.0:7070
```

##### 6. servicecomb三种微服务开发方式
###### 1. Springmvc
```java
@RpcSchema(schemaId = "SpringmvcHello")
@RequestMapping(path = "/springmvchello", produces = MediaType.APPLICATION_JSON)
public class SpringmvcHello implements ICallSchema {

    @Override
    @RequestMapping(path = "/sayhi", method = RequestMethod.POST)
    public String sayHi(@RequestParam(name = "name") String name) {
        return "Spring mvc Hello " + name;
    }

    @Override
    @RequestMapping(path = "/sayhello", method = RequestMethod.POST)
    public String sayHello(@RequestBody Person person) {
        return "Spring mvc Hello person " + person.getName();
    }
}
```
###### 2. Jaxrs
```java
@RpcSchema(schemaId = "JaxrsHello")
@Path("/jaxrshello")
@Produces(MediaType.APPLICATION_JSON)
public class JaxrsHello implements ICallSchema {

  @Path("/sayhi")
  @POST
  @Override
  public String sayHi(String name) {
    return "Jaxrs Hello " + name;
  }

  @Path("/sayhello")
  @POST
  @Override
  public String sayHello(Person person) {
    return "Jaxrs Hello person " + person.getName();
  }
}
```

###### 3. 透明RPC
```java
@RpcSchema(schemaId = "PojoHello")
public class PojoHello implements ICallSchema {

  @Override
  public String sayHi(String name) {
    return "Pojo Hello " + name;
  }

  @Override
  public String sayHello(Person person) {
    return "Pojo Hello person " + person.getName();
  }
}
```
##### 7. 启动启动类
```java
public class ProviderApplication {

    public static void main(String[] args) throws Exception {
        Log4jUtils.init();
        BeanUtils.init();
    }

}

```



#### 2. 新建服务消费者项目，增加log4j2.xml

##### 1. 新增microservice.yaml
```yaml
APPLICATION_ID: servicecomb
service_description:
  name: consumer
  version: 0.0.1
  environment: development
servicecomb:
  service:
    registry:
      address: http://127.0.0.1:30100
  isolation:
    Consumer:
      enabled: false
  references:
    myProvider: # servicecomb.references.${目标服务名}.version-rule
      version-rule: 0.0.1 # servicecomb.references.version-rule 同时支持全局和微服务级的两级控制
```
##### 2. 启动启动类
```java
@Component
public class ConsumerApplication {
    
    // microserviceName 为服务提供者项目中的 ${service_description.name}

    // 对应服务提供者【SpringmvcHello.class】中配置的schemaId
    @RpcReference(microserviceName = "myProvider", schemaId = "SpringmvcHello")
    private static ICallSchema springmvcHello;

    // 对应服务提供者【JaxrsHello.class】中配置的schemaId
    @RpcReference(microserviceName = "myProvider", schemaId = "JaxrsHello")
    private static ICallSchema jaxrsHello;

    // 对应服务提供者【PojoHello.class】中配置的schemaId
    @RpcReference(microserviceName = "myProvider", schemaId = "PojoHello")
    private static ICallSchema pojoHello;


    public static void main(String[] args) {

        BeanUtils.init();

        Person person = new Person();
        person.setName("LiYang");

        System.out.println(springmvcHello.sayHi(" springmvc"));
        System.out.println(springmvcHello.sayHello(person));

        System.out.println(jaxrsHello.sayHi(" jaxrs"));
        System.out.println(jaxrsHello.sayHello(person));

        System.out.println(pojoHello.sayHi("pojo"));
        System.out.println(pojoHello.sayHello(person));

    }

}

```
##### 3. 启动成功输出

```
Spring mvc Hello person LiYang
Jaxrs Hello  jaxrs
Jaxrs Hello person LiYang
Pojo Hello pojo
Pojo Hello person LiYang
```

#### 3.编写一个简单的网关服务调用服务提供者所提供的接口，并将返回的内容填充在网关服务的html页面中

##### 1. 为服务消费方添加一个可以被网关服务调用的Rest接口,返回调用的服务提供者所提供的接口返回的内容
```java
@RestSchema(schemaId = "RestSchemaId")
@RequestMapping("/")
public class GatewayCallDemo {

    @RpcReference(microserviceName = "myProvider", schemaId = "SpringmvcHello")
    private static ICallSchema springmvcHello;

    @RpcReference(microserviceName = "myProvider", schemaId = "JaxrsHello")
    private static ICallSchema jaxrsHello;

    @RpcReference(microserviceName = "myProvider", schemaId = "PojoHello")
    private static ICallSchema pojoHello;

    @GetMapping(path = "/gateway")
    public HashMap<String, Object> testCall() {

        Person person = new Person();
        person.setName("LiYang");

        HashMap<String, Object> hashMap = new HashMap<String, Object>();

        hashMap.put("springmvc-sayHi", springmvcHello.sayHi(" springmvc"));
        hashMap.put("springmvc-sayHello", springmvcHello.sayHello(person));

        hashMap.put("jaxrs-sayHi", jaxrsHello.sayHi(" jaxrs"));
        hashMap.put("jaxrs-sayHello", jaxrsHello.sayHello(person));

        hashMap.put("pojo-sayHi", pojoHello.sayHi(" pojo"));
        hashMap.put("pojo-sayHello", pojoHello.sayHello(person));

        return hashMap;
    }
}

```
##### 2. 提供接口之后需要为servicecomb-consumer项目配置一个可以被服务访问的端口
```yaml
rest:
    address: 0.0.0.0:7777
```

##### 3.新建一个[网关服务](../servicecomb-gateway/README.md)

##### 4.网关服务中处理pom文件与新建Springboot配置文件与Servicecomb的配置文件

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<!--add rest transport-->
<dependency>
    <groupId>org.apache.servicecomb</groupId>
    <artifactId>transport-rest-vertx</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.servicecomb</groupId>
    <artifactId>spring-boot-starter-servicecomb</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.servicecomb</groupId>
    <artifactId>spring-boot-starter-discovery</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.servicecomb</groupId>
    <artifactId>spring-cloud-zuul</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.servicecomb</groupId>
    <artifactId>spring-cloud-zuul-zipkin</artifactId>
</dependency>
```

```yaml
# application.yaml
zuul:
  routes:
    myConsumer: /myConsumer/**  # zuul.routes.${serviceName} 为网关服务需要调用的服务名称，配置在【servicecomb-consumer/resources/】中的name属性

# disable netflix eurkea since it's not used for service discovery
ribbon:
  eureka:
    enabled: false

server:
  port: 8889

servicecomb:
  tracing:
    enabled: false
```

```yaml
# microservice.yaml
# all interconnected microservices must belong to an application wth the same ID
APPLICATION_ID: servicecomb
service_description:
# name of the declaring microservice
  name: gateway
  version: 0.0.1
  environment: development
servicecomb:
  service:
    registry:
      address: http://127.0.0.1:30100

```

##### 5. 编写网关服务启动类
```java

@SpringBootApplication
@EnableZuulProxy
@EnableServiceComb
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

```

##### 6. 编写一个简单的html页面，点击按钮调用服务提供者所提供的接口
> 其中接口 "/myConsumer/gateway" 中的【myConsumer】为服务消费方的服务名称，同时也是application.yaml文件中的zuul.routes.${serviceName}
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Test Call Gateway Servicecomb Provider Server</title>
    <script src="https://code.jquery.com/jquery-3.2.1.min.js" crossorigin="anonymous"></script>
</head>
<body>
<div style="display: flex;justify-content: center;align-items: center;">
    <button id="call">Call Gateway Servicecomb Provider Server</button>
    <div id="result"></div>
</div>
</body>
<script>
    $("#call").click(function () {
        $('#result').html("")
        $.ajax({
          url: "/myConsumer/gateway",
          type: "GET",
          success: function (data) {
            Object.keys(data).forEach(function(key){
                $("#result").append("<div>【"+key+"】 = 【" + data[key] + "】</div>");
            });
          },
        });
      });

</script>
</html>

```


##### 7. 启动网关服务、启动服务提供者服务、启动服务消费服务
> 进入页面后点击按钮，服务调用成功后会讲以下内容填充至页面

```
【jaxrs-sayHello】 = 【Jaxrs Hello person LiYang】
【springmvc-sayHello】 = 【Spring mvc Hello person LiYang】
【jaxrs-sayHi】 = 【Jaxrs Hello jaxrs】
【pojo-sayHi】 = 【Pojo Hello pojo】
【springmvc-sayHi】 = 【Spring mvc Hello springmvc】
【pojo-sayHello】 = 【Pojo Hello person LiYang】
```
