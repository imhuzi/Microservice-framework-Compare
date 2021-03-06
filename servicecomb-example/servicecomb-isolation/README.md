

# 服务治理 - 解决或缓解服务雪崩

> 降级策略是当服务请求异常时，微服务所采用的异常处理策略。
  降级策略有三个相关的技术概念：“隔离”、“熔断”、“容错”：
  “隔离”是一种异常检测机制，常用的检测方法是请求超时、流量过大等。一般的设置参数包括超时时间、同时并发请求个数等。
  “熔断”是一种异常反应机制，“熔断”依赖于“隔离”。熔断通常基于错误率来实现。一般的设置参数包括统计请求的个数、错误率等。
  “容错”是一种异常处理机制，“容错”依赖于“熔断”。熔断以后，会调用“容错”的方法。一般的设置参数包括调用容错方法的次数等。
  把这些概念联系起来：当"隔离"措施检测到N次请求中共有M次错误的时候，"熔断"不再发送后续请求，调用"容错"处理函数。这个技术上的定义，是和Netflix Hystrix一致的，通过这个定义，非常容易理解它提供的配置项，参考：https://github.com/Netflix/Hystrix/wiki/Configuration。当前ServiceComb提供两种容错方式，分别为返回null值和抛出异常。


