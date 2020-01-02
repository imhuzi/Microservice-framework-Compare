package com.uyibai.simple;

import com.uyibai.common.simple.register.ISimpleCallSchema;
import com.uyibai.common.simple.register.Person;
import org.apache.servicecomb.provider.pojo.RpcReference;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

/**
 * @author: LiYang
 * @create: 2020-01-02 12:27
 * @Description:
 * 通过网关调用服务提供者所提供的接口
 **/
@RestSchema(schemaId = "RestSchemaId")
@RequestMapping("/")
public class GatewayCallDemo {

    @RpcReference(microserviceName = "myProvider", schemaId = "SpringmvcHello")
    private static ISimpleCallSchema springmvcHello;

    @RpcReference(microserviceName = "myProvider", schemaId = "JaxrsHello")
    private static ISimpleCallSchema jaxrsHello;

    @RpcReference(microserviceName = "myProvider", schemaId = "PojoHello")
    private static ISimpleCallSchema pojoHello;

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
