Andriod App
=====================================

本`App`模拟了一个依赖`OSS`提供的`STS`功能来实现的简单网盘demo，演示了开发者通过`STS`功能的`Polic`y授权机制，为不同的终端用户分配不同权限的`Token`。某个终端开发者持有分配给他的`Token`以后，是不能用这个`Token`逾权访问其他终端用户的资源的。

这个demo假设角色有：

1. 终端用户A
1. 终端用户B
1. `ISV`开发者
1. `OSS`

应用的运行过程和效果如下：

1. `ISV`开发者在`OSS`上某`Bucket`下建立了`userA/`和`userB/`目录，分别作为A和B的存储空间。
2. 在`APP Server`上搭建基于`STS lib`的加签服务，当A或者B在手机端登陆到`App Server`上时，分别为他们签出不同`Policy`(在这里为授权到不同的目录)的`Token`，返回到手机端。
3. A或B拿到`Token`以后，因为这个`Token`对应不同的`Policy`，所以A或B只能用这个`Policy`访问自己的`OSS`资源。

运行配置
---------------------

为了实现这个效果，你需要做以下的配置。


### `Bucket`目录配置

假设`BucketName`为`sampleBucket`，那么需要在这个`sampleBucket`上至少存放两个目录：

```bash
sampleBucket
`-- userA
|   |-- folder1
|   `-- folder2
|       `-- 1.file
`-- userB
    |-- folder1
    |   `-- 1.file
    `-- folder2
        |-- 2.file
        |-- 3.file
        `-- 4.file
```

**注意** 目录`userA`和`userB`的名字要一致，且严格放在`sampleBucket`根目录下，目录下的文件可以任意。

### App端配置

执行`server`端的配置并运行以后，

在 `./andriod-app/Androidmanifext.xml` 中：


```xml
<meta-data android:name="ServerAddress" android:value="10.1.29.208:8080"/>
<meta-data android:name="EndPoint" android:value="oss-cn-beijing.aliyuncs.com"/>
<meta-data android:name="BucketName" android:value="sts-android-demo"/>
```

把各`android:value`中依次填入`server`端`ip:port/Region域名/你的BucketName`，

然后在`Android`应用主目录下执行:

```bash
gradle build
```

执行成功后即可在 `app/build/outputs/apk/` 目录下找到打包成功的`apk`文件，安装到手机即可进行测试。

FAQ
--------------------

### `The difference between the request time and the current time is too large`

`OSS`操作出现上面的错误，是因为手机上的时间 与 `OSS`服务器相差太大。在模拟器上运行比较容易出现这样的问题。

- 如果是真机，设置一下时间。
- 如果模拟器，可以重启一下，这样就会和开发机的时间一致了。

完整的信息如下。

```bash
07-01 03:42:15.480  25470-26132/com.example.oss_sdk_demo W/System.err﹕ responseCode: RequestTimeTooSkewed
07-01 03:42:15.480  25470-26132/com.example.oss_sdk_demo W/System.err﹕ responseMessage: The difference between the request time and the current time is too large.
07-01 03:42:15.480  25470-26132/com.example.oss_sdk_demo W/System.err﹕ [ 07-01 03:42:15.480 25470:26132 W/System.err ]
            at com.alibaba.sdk.android.oss.util.OSSToolKit.buildOssException(OSSToolKit.java:578)
07-01 03:42:15.480  25470-26132/com.example.oss_sdk_demo W/System.err﹕ at com.alibaba.sdk.android.oss.util.OSSToolKit.handleExceptionalResponse(OSSToolKit.java:614)
07-01 03:42:15.480  25470-26132/com.example.oss_sdk_demo W/System.err﹕ at com.alibaba.sdk.android.oss.storage.OSSBucket.listObjectsInBucket(OSSBucket.java:167)
07-01 03:42:15.480  25470-26132/com.example.oss_sdk_demo W/System.err﹕ at com.example.oss_sdk_demo.task.ListObjectInBucketTask.doInBackground(ListObjectInBucketTask.java:39)
07-01 03:42:15.480  25470-26132/com.example.oss_sdk_demo W/System.err﹕ at com.example.oss_sdk_demo.task.ListObjectInBucketTask.doInBackground(ListObjectInBucketTask.java:21)
...
```

相关资料
--------------------

- [OSS移动SDK文档](http://docs.aliyun.com/#/pub/oss/sdk/android-sdk&preface)
- [OSS Policy文档](http://docs.aliyun.com/#/pub/oss/api-reference/access-control&granting-permissions)
- [STS文档](http://docs.aliyun.com/#/pub/ram)
