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
        final String accessKeyId = aliyunServiceConfig.getProperty("aliyun.accessKeyId");
        final String accessKeySecret = aliyunServiceConfig.getProperty("aliyun.accessKeySecret");
        final String userId = aliyunServiceConfig.getProperty("aliyun.userId");
        final String bucketName = aliyunServiceConfig.getProperty("aliyun.oss.bucketName");
        final int ONE_HOUR = 60 * 60;

        final FederationToken federationToken = stsService.getFederationToken(
                accessKeyId,
                accessKeySecret,
                userName, getPolicy(userId, bucketName), ONE_HOUR
        );

        resp.setHeader("Content-type", "application/json");
        final PrintWriter writer = resp.getWriter();
        writer.write(JSON.toJSONString(federationToken));
    }

    String getPolicy(String userId, String bucketName) {
        return String.format("{\n" +
                "    \"Version\": \"1\", \n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"oss:PutObject\", \n" +
                "                \"oss:GetObject\", \n" +
                "                \"oss:DeleteObject\"\n" +
                "            ], \n" +
                "            \"Resource\": [\n" +
                "                \"acs:oss:*:%s:%s/*\"\n" +
                "            ], \n" +
                "            \"Effect\": \"Allow\"\n" +
                "        }\n" +
                "    ]\n" +
                "}", userId, bucketName);
    }
}
