package com.aliyun.sts.demo.sts;

/**
 * STS服务接口。
 *
 * @author ding.lid
 * @see FederationToken
 * @see StsException
 * @since 1.0.0
 */
public interface StsService {
    /**
     * 获取STS的Token。
     *
     * @param accessKeyId     阿里云用户accessKeyId
     * @param accessKeySecret 阿里云用户accessKeySecret
     * @param grantee         Token关联的应用用户名。
     * @param policy          Token对应的Policy，JSON格式。
     *                        即可以对什么产品（如OSS）的哪些资源（如OSS的某个目录或文件）允许或禁止做什么操作（如读或写）
     * @param expireSeconds   STS Token的过期时间，单位秒。
     * @return 输入要求的STS Token。
     * @throws StsException 操作出错
     */
    FederationToken getFederationToken(String accessKeyId, String accessKeySecret, String grantee, String policy, long expireSeconds);
}
