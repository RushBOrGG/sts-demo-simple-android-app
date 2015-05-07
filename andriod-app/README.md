andriod-app
=====================================

### 配置

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
