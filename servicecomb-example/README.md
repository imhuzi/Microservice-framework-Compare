# servicecomb


[Java Chassis系统架构 - Apache ServiceComb](http://servicecomb.apache.org/cn/docs/users/)

#### 1. 开发方式
* SpringMVC
* JAX-RS
* 透明RPC

#### 2. 启动流程
##### 1. 安装服务中心[ServiceCenter](http://servicecomb.apache.org/cn/docs/products/service-center/install/)
- Service-Center注册发现接口基于RESTful标准实现，不受开发语言限制
- [下载启动](http://servicecomb.apache.org/cn/release/service-center-downloads/)或docker直接启动
```
# 下载镜像
docker pull servicecomb/service-center
# 启动镜像
docker run -d -p 30100:30100 servicecomb/service-center
# 查看端口监听
docker ps | grep service-center
```
- 下载启动成功后通过http://127.0.0.1:30103访问服务中心web页,docker 启动无管理页面

- Service-Center中服务发现流程
```
1 服务提供者向Service-Center注册服务信息
2 服务提供者发送心跳，维持在Service-Center中的“UP”状态
3 服务消费者向Service-Center注册服务信息
4 服务消费者从Service-Center发现服务提供者信息
5 服务消费者向服务提供者发送请求，并获取通讯结果
```

##### 2. 官方ServiceComb[示例项目](https://github.com/apache/servicecomb-samples)
> 注意：官方事例[体质指数计算器](https://github.com/apache/servicecomb-samples/tree/1.3.0/java-chassis-samples/bmi)在分支1.3.0上

##### 3. 事例demo
* [服务调用 - servicecomb-provider-consumer](./servicecomb-provider-consumer/README.md)
* [通过网关调用服务 - servicecomb-gateway](./servicecomb-gateway/README.md)
* [负载均衡 - 横向扩展 - servicecomb-load-balance](./servicecomb-load-balance/README.md)
* [流量控制 - 避免微服务过载运行 - servicecomb-qps-flowcontrol](./servicecomb-qps-flowcontrol/README.md)
* [服务治理 - 解决或缓解服务雪崩 - servicecomb-isolation](./servicecomb-isolation/README.md)
* 分布式调用链追踪 - 监控微服务的网络延时并可视化微服务中的数据流转
