package com.example.oss_sdk_demo.model;


/**
 * @author: zhouzhuo<yecan.xyc@alibaba-inc.com>
 * Apr 30, 2015
 *
 */
public class FederationToken {

    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private long expiration;

    public FederationToken(String ak, String sk, String sToken, long expiredTime) {
        this.accessKeyId = ak;
        this.accessKeySecret = sk;
        this.securityToken = sToken;
        this.expiration = expiredTime;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
