andriod-app
=====================================


### 说明

本APP模拟了一个依赖OSS提供的STS功能来实现的简单网盘demo，演示了开发者通过STS功能的policy授权机制，为不同的终端用户分配不同权限的token。某个终端开发者持有分配给他的token以后，是不能用这个token逾权访问其他终端用户的资源的。

这个demo假设角色有：终端用户A，终端用户B，ISV开发者，OSS。

ISV开发者在OSS上某Bucket下建立了userA/和userB/目录，分别作为A和B的存储空间，然后，在APP Server上搭建基于STS lib的加签服务，当A或者B在手机端登陆到APP Server上时，分别为他们签出不同Policy(在这里为授权到不同的目录)的Token，返回到手机端。A或B拿到Token以后，因为这个token对应不同的policy，所以他们只能用这个policy访问自己的资源。

为了实现这个效果，你需要做以下这些配置：

### Bucket目录配置

假设BucketName为sampleBucket，那么需要在这个sampleBucket上至少存放两个目录：

```
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

**注意** 目录userA和userB的名字要一致，且严格放在sampleBucket根目录下，目录下的文件可以任意。

### App端配置

执行server端的配置并运行以后，

在 `./andriod-app/sts-poc-demo/Androidmanifext.xml` 中：


```
<meta-data android:name="ServerAddress" android:value="10.1.29.208:8080"></meta-data>
<meta-data android:name="EndPoint" android:value="oss-cn-beijing.aliyuncs.com"></meta-data>
<meta-data android:name="BucketName" android:value="sts-poc-demo"></meta-data>
```

把各android:value中依次填入server端ip:port/Region域名/你的BucketName，

然后在./sts-poc-demo目录下执行:

```
gradle build
```

执行成功后即可在 `./sts-poc-demo/app/build/outputs/apk/` 目录下找到打包成功的apk文件，安装到手机即可进行测试。
