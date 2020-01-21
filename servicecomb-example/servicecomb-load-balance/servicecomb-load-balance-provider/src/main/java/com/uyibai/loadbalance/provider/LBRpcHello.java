package com.uyibai.loadbalance.provider;

import com.uyibai.common.simple.register.ILoadBalanceCallSchema;
import org.apache.servicecomb.provider.pojo.RpcSchema;
import org.apache.servicecomb.serviceregistry.RegistryUtils;
import org.apache.servicecomb.serviceregistry.api.registry.MicroserviceInstance;

/**
 * @author: LiYang
 * @create: 2020-01-02 14:55
 * @Description:
 **/
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
