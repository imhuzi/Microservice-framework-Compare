# 简单的服务调用 - servicecomb-provider-consumer

#### 项目目录结构

|- servicecomb-provider-consumer  # servicecomb 注册发现demo
|--- servicecomb-consumer        # 调用register中所提供的接口
|--- servicecomb-provider        # 提供接口为consumer调用


##### 服务提供者提供的服务契约有两种形式
- 放置在"resources/microservices"或者"resources/application"目录的契约文件(替代服务中心)
- 隐式契约形式（本次代码中使用的都是通过Annotation声明的隐式契约）

#### 1. 新建服务提供者项目

##### 1. 服务的父工程中添加自己创建的[servicecomb-common-schema的依赖](../pom.xml)，为服务调用提供接口依赖

##### 2. 新建servicecomb-provider项目，并添加servicecomb相关依赖，pom文件为
```xml
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
