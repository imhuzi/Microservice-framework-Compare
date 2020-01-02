# 公共网关服务 - servicecomb-gateway

* demo子项目共用该网关服务，为不同子项目提供html页面

1. [index.html](./src/main/resources/static/index.html)提供通过网关实现服务发现与调用功能，配置文件中添加myConsumer的Zuul的路由规则，index.html提供调用事例
2. [flowcontrol.html](./src/main/resources/static/flowcontrol.html)提供通过网关实现服务调用的流量控制功能，配置文件中添加flowControlProvider的Zuul的路由规则，flowcontrol.html提供调用事例
