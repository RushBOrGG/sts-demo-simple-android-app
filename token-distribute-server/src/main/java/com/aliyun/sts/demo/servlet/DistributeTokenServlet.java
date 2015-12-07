package com.aliyun.sts.demo.servlet;

import com.alibaba.fastjson.JSON;
import com.aliyun.sts.demo.sts.FederationToken;
import com.aliyun.sts.demo.sts.StsService;
import com.aliyun.sts.demo.sts.impl.StsServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ding.lid
 */
public class DistributeTokenServlet extends HttpServlet {

    StsService stsService;
    Properties aliyunServiceConfig;

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            stsService = new StsServiceImpl();
            aliyunServiceConfig = new Properties();

            final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("/aliyun-service-config.properties");
            aliyunServiceConfig.load(resourceAsStream);
            resourceAsStream.close();

            System.out.println(getClass().getName() + " initialized!");
        } catch (Exception e) {
            throw new ServletException("Fail to init " + getClass().getName(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String userName = req.getParameter("user-name");

        // 只有 RAM用户（子账号）才能调用 AssumeRole 接口
        // 阿里云主账号的AccessKeys不能用于发起AssumeRole请求
        // 请首先在RAM控制台创建一个RAM用户，并为这个用户创建AccessKeys
        // 参考：https://docs.aliyun.com/#/pub/ram/ram-user-guide/user_group_management&create_user
        final String accessKeyId = aliyunServiceConfig.getProperty("aliyun.accessKeyId");
        final String accessKeySecret = aliyunServiceConfig.getProperty("aliyun.accessKeySecret");

        final String userId = aliyunServiceConfig.getProperty("aliyun.userId");
        final String bucketName = aliyunServiceConfig.getProperty("aliyun.oss.bucketName");

        // RoleArn 需要在 RAM 控制台上获取
        // 参考: https://docs.aliyun.com/#/pub/ram/ram-user-guide/role&user-role
        final String roleArn = aliyunServiceConfig.getProperty("aliyun.oss.roleArn");
        final int ONE_HOUR = 60 * 60;

        final FederationToken federationToken = stsService.assumeRole(
                accessKeyId,
                accessKeySecret,
                roleArn, getPolicy(userName, bucketName), ONE_HOUR
        );

        resp.setHeader("Content-type", "application/json");
        final PrintWriter writer = resp.getWriter();
        writer.write(JSON.toJSONString(federationToken));
    }

    String getPolicy(String userName, String bucketName) {
        return String.format(
                "{\n" +
                "    \"Version\": \"1\", \n" +
                "    \"Statement\": [\n" +
                // 限制只能执行指定prefix的罗列Bucket操作
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"oss:ListObjects\"\n" +
                "            ], \n" +
                "            \"Resource\": [\n" +
                "                \"acs:oss:*:*:%s\"\n" +
                "            ], \n" +
                "            \"Effect\": \"Allow\", \n" +
                "            \"Condition\": { \n" +
                "                \"StringLike\": { \n" +
                "                    \"oss:Prefix\": \"%s/*\"\n" +
                "                }\n" +
                "            }\n" +
                "        }, \n" +
                // 限制只能对Bucket指定目录下的文件进行操作
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"oss:PutObject\", \n" +
                "                \"oss:GetObject\", \n" +
                "                \"oss:DeleteObject\"\n" +
                "            ], \n" +
                "            \"Resource\": [\n" +
                "                \"acs:oss:*:*:%s/*\"\n" +
                "            ], \n" +
                "            \"Effect\": \"Allow\"\n" +
                "        }\n" +
                "    ]\n" +
                "}", bucketName, userName, bucketName);
    }
}
