package com.uyibai.common.simple.register;

/**
 * @author: LiYang
 * @create: 2020-01-02 14:50
 * @Description:
 * 为负载均衡调用事例提供接口
 **/
public interface ILoadBalanceCallSchema {


    /**
     * 根据不同服务实例Id判断是否来自不同服务
     * @return
     */
    String callLoadBalance();

}
