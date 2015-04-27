package com.aliyun.sts.demo.sts;

import java.io.Serializable;
import java.util.Date;

/**
 * FederationToken.
 *
 * @author ding.lid
 * @since 1.0.0
 */
public class FederationToken implements Serializable {
    private static final long serialVersionUID = -4807070499625359649L;

    private String requestId;
    private String federatedUser;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private Date expiration;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFederatedUser() {
        return federatedUser;
    }

    public void setFederatedUser(String federatedUser) {
        this.federatedUser = federatedUser;
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

    /**
     * Token的过期时间。
     */
    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    @Override
    public String toString() {
        return "FederationToken{" +
                "requestId='" + requestId + '\'' +
                ", federatedUser='" + federatedUser + '\'' +
                ", accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", securityToken='" + securityToken + '\'' +
                ", expiration=" + expiration +
                '}';
    }
}
