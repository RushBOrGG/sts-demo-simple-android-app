andriod-app
=====================================

### 配置

在 `./andriod-app/sts-poc-demo/Androidmanifext.xml` 中：


```
<meta-data android:name="ServerAddress" android:value="10.1.29.208:8080"></meta-data>
```

把android:value中填入server端ip:port，然后在./sts-poc-demo目录下执行:

```
gradle build
```

执行成功后即可在 `./sts-poc-demo/app/build/outputs/apk/` 目录下找到打包成功的apk文件，安装到手机即可进行测试。
