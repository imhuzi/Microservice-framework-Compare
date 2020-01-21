package com.uyibai.simple.provider;

import com.uyibai.common.simple.register.ISimpleCallSchema;
import com.uyibai.common.simple.register.Person;
import org.apache.servicecomb.provider.pojo.RpcSchema;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.core.MediaType;

/**
 * @author: LiYang
 * @create: 2019-12-28 21:59
 * @Description:
 **/
@RpcSchema(schemaId = "SpringmvcHello")
@RequestMapping(path = "/springmvchello", produces = MediaType.APPLICATION_JSON)
public class SpringmvcHello implements ISimpleCallSchema {

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
