token-distribute-server
========================================

分配`STS Token`给`App`。演示实现的功能需求：

- 分配的`STS Token`在`App`上直接使用。分配`Token`之后的使用，`App`无需与`Server`交互。
- 可以为不同的`App`用户，分配不同的权限。  
    在网盘应用Demo中，即可以用`STS Token`保证`张三`用户不能读写`李四`的网盘数据。

如何运行
--------------------

### 1. 提供配置文件

`aliyun-service-config.properties`:

```bash
aliyun.accessKeyId = _YOUR_ACCESS_ID_HERE_
aliyun.accessKeySecret = _YOUR_ACCESS_SECRET_HERE_
aliyun.userId = _YOUR_USER_ID_HERE_
aliyun.oss.bucketName = _YOUR_OSS_BUCKET_NAME_HERE_
```

代码工程中已经提供了配置文件`aliyun-service-config.properties.template`，重命名成`aliyun-service-config.properties`，填写上面值。

### 2. 运行服务

```bash
cd token-distribute-server
mvn clean install && mvn jetty:run
```

服务在本机上运行，端口`8080`。可以通过。

可以通过下面的`URL`确认：

<http://localhost:8080/distribute-token.json?user-name=jerry>

返回的是为应用用户分配的`STS Token`内容。

线上使用`STS Token`的注意点
--------------------

这个Demo演示了`STS Token`实现的需求。

- `STS Token`是敏感数据，用于请求`STS Token`的`URL`要走`HTTPS`。
- 如果要保证`STS Token`是`App`用户隔离的，分配`STS Token`的`URL`是要检查`App`上的用户是否登录的。  
    如果不检查`App`用户是否登录，实际总可以分配出这个用户权限。

相关资料
--------------------

- `STS`文档 **TODO**
- `Policy`文档 **TODO**
- `OSS`移动`SDK`文档 **TODO**
