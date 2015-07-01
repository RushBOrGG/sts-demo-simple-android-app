sts-demo-simple-android-app
==================================

通过`STS`实现一个`网盘`应用：提供移动应用直接读写`OSS`并做到移动`App`上的不同用户数据读写是隔离。

整个Demo的流程如下：

1. 服务端应用 用于`STS Token`分配，可以为不同`App`用户分配不同的权限。
1. 移动`App`以一个`App`身份 向 服务端应用 申请分配`STS Token`。  
    在网盘应用Demo中，即可以用`STS Token`保证`张三`用户不能读写`李四`的网盘数据。
1. 分配的`STS Token`在`App`上直接使用。分配`Token`之后`App`对`OSS`的读写无需与服务端应用交互。

详细信息参见2个工程的文档：

- [服务端应用工程](token-distribute-server)
- [Android App应用工程](andriod-app/)

更多信息如系统架构参见：[OSS Android SDK开发指南](http://docs.aliyun.com/#/pub/oss/sdk/android-sdk&preface)
