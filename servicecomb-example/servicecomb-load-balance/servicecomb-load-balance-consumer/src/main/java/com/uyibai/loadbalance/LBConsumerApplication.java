package com.uyibai.loadbalance;

import com.uyibai.common.simple.register.ILoadBalanceCallSchema;
import com.uyibai.common.simple.register.ISimpleCallSchema;
import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.provider.pojo.RpcReference;
import org.springframework.stereotype.Component;

/**
 * @author: LiYang
 * @create: 2020-01-02 15:00
 * @Description:
 **/
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
