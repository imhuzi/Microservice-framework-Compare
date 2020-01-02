package com.uyibai.simple;

import com.uyibai.common.simple.register.ISimpleCallSchema;
import com.uyibai.common.simple.register.Person;
import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.provider.pojo.RpcReference;
import org.springframework.stereotype.Component;

/**
 * @author: LiYang
 * @create: 2019-12-28 22:07
 * @Description:
 * 服务调用者调用服务所提供的接口
 **/
@Component
public class ConsumerApplication {

    @RpcReference(microserviceName = "myProvider", schemaId = "SpringmvcHello")
    private static ISimpleCallSchema springmvcHello;

    @RpcReference(microserviceName = "myProvider", schemaId = "JaxrsHello")
    private static ISimpleCallSchema jaxrsHello;

    @RpcReference(microserviceName = "myProvider", schemaId = "PojoHello")
    private static ISimpleCallSchema pojoHello;


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
