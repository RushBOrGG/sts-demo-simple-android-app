package com.aliyun.sts.demo.sts.impl;

import com.aliyun.sts.demo.sts.FederationToken;
import com.aliyun.sts.demo.sts.StsService;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;

/**
 * @author ding.lid
 */
public class StsServiceImpl implements StsService {
    private volatile String regionId = REGION_CN_HANGZHOU;

    // 目前只有"cn-hangzhou"这个region可用, 不要使用填写其他region的值
    public static final String REGION_CN_HANGZHOU = "cn-hangzhou";

    // 当前 STS API 版本
    public static final String STS_API_VERSION = "2015-04-01";

    static AssumeRoleResponse assumeRole(String accessKeyId, String accessKeySecret,
                                         String roleArn, String roleSessionName, String policy,
                                         ProtocolType protocolType) throws ClientException {
        try {
            // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
            IClientProfile profile = DefaultProfile.getProfile(REGION_CN_HANGZHOU, accessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);

            // 创建一个 AssumeRoleRequest 并设置请求参数
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setVersion(STS_API_VERSION);
            request.setMethod(MethodType.POST);
            request.setProtocol(protocolType);

            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy);

            // 发起请求，并得到response
            final AssumeRoleResponse response = client.getAcsResponse(request);

            return response;
        } catch (ClientException e) {
            throw e;
        }
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Override
    public FederationToken assumeRole(
            final String accessKeyId, final String accessKeySecret,
            final String roleArn, final String policy, final long expireSeconds) {
        return assumeRole0(accessKeyId, accessKeySecret, roleArn, policy, expireSeconds);
    }

    FederationToken assumeRole0(
            final String accessKeyId, final String accessKeySecret,
            final String roleArn, final String policy, final long expireSeconds) {

        // AssumeRole API 请求参数: RoleArn, RoleSessionName, Polciy, and DurationSeconds
        // 参考： https://docs.aliyun.com/#/pub/ram/sts-api-reference/actions&assume_role

        // RoleSessionName 是临时Token的会话名称，自己指定用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
        // 但是注意RoleSessionName的长度和规则，不要有空格，只能有'-' '_' 字母和数字等字符
        // 具体规则请参考API文档中的格式要求
        String roleSessionName = "alice-001";

        // 如何定制你的policy?
        // 参考: https://docs.aliyun.com/#/pub/ram/ram-user-guide/policy_reference&struct_def
        // OSS policy 例子: https://docs.aliyun.com/#/pub/oss/product-documentation/acl&policy-configure
        // OSS 授权相关问题的FAQ: https://docs.aliyun.com/#/pub/ram/faq/oss&basic
//        String policy = "{\n" +
//                "    \"Version\": \"1\", \n" +
//                "    \"Statement\": [\n" +
//                "        {\n" +
//                "            \"Action\": [\n" +
//                "                \"oss:GetBucket\", \n" +
//                "                \"oss:GetObject\" \n" +
//                "            ], \n" +
//                "            \"Resource\": [\n" +
//                "                \"acs:oss:*:177530****529849:mybucket\", \n" +
//                "                \"acs:oss:*:177530****529849:mybucket/*\" \n" +
//                "            ], \n" +
//                "            \"Effect\": \"Allow\"\n" +
//                "        }\n" +
//                "    ]\n" +
//                "}";

        // 此处必须为 HTTPS
        ProtocolType protocolType = ProtocolType.HTTPS;

        try {
            final AssumeRoleResponse response = assumeRole(accessKeyId, accessKeySecret,
                    roleArn, roleSessionName, policy, protocolType);

            System.out.println("Expiration: " + response.getCredentials().getExpiration());
            System.out.println("Access Key Id: " + response.getCredentials().getAccessKeyId());
            System.out.println("Access Key Secret: " + response.getCredentials().getAccessKeySecret());
            System.out.println("Security Token: " + response.getCredentials().getSecurityToken());

            FederationToken result = new FederationToken();
            result.setRequestId(response.getRequestId());
            result.setAccessKeyId(response.getCredentials().getAccessKeyId());
            result.setAccessKeySecret(response.getCredentials().getAccessKeySecret());
            result.setSecurityToken(response.getCredentials().getSecurityToken());
            result.setExpiration(response.getCredentials().getExpiration());
            return result;
        } catch (ClientException e) {
            System.out.println("Failed to get a token.");
            System.out.println("Error code: " + e.getErrCode());
            System.out.println("Error message: " + e.getErrMsg());
            return null;
        }
    }
}
