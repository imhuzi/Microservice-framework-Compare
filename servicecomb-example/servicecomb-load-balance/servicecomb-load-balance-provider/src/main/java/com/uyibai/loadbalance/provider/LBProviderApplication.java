package com.uyibai.loadbalance.provider;

import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.foundation.common.utils.Log4jUtils;

/**
 * @author: LiYang
 * @create: 2020-01-02 14:54
 * @Description:
 **/
public class LBProviderApplication {

    public static void main(String[] args) throws Exception {
        Log4jUtils.init();
        BeanUtils.init();
    }

}
