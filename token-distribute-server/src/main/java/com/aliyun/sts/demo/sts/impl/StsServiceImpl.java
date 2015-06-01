package com.aliyun.sts.demo.sts.impl;

import com.aliyun.sts.demo.sts.FederationToken;
import com.aliyun.sts.demo.sts.StsException;
import com.aliyun.sts.demo.sts.StsService;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.GetFederationTokenRequest;
import com.aliyuncs.sts.model.v20150401.GetFederationTokenResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

/**
 * @author ding.lid
 */
public class StsServiceImpl implements StsService {
    public static final String REGION_CN_HANGZHOU = "cn-hangzhou";
    public static final String STS_POP_API_VERSION = "2015-04-01";
    public static final String STS_API_VERSION = "1";

    private volatile String regionId = REGION_CN_HANGZHOU;

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Override
    public FederationToken getFederationToken(
            final String accessKeyId, final String accessKeySecret,
            final String grantee, final String policy, final long expireSeconds) {
        return getFederationToken0(accessKeyId, accessKeySecret, grantee, policy, expireSeconds, ProtocolType.HTTPS);
    }

    FederationToken getFederationToken0(
            final String accessKeyId, final String accessKeySecret,
            final String grantee, final String policy, final long expireSeconds, ProtocolType protocolType) {
        try {
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);

            final GetFederationTokenRequest request = new GetFederationTokenRequest();
            request.setVersion(STS_POP_API_VERSION);
            request.setMethod(MethodType.POST);
            request.setProtocol(protocolType);

            request.setStsVersion(STS_API_VERSION);
            request.setName(grantee);
            request.setPolicy(policy);
            request.setDurationSeconds(expireSeconds);

            final GetFederationTokenResponse response = client.getAcsResponse(request);

            final FederationToken federationToken = new FederationToken();
            federationToken.setRequestId(response.getRequestId());
            federationToken.setFederatedUser(response.getFederatedUser().getArn());
            federationToken.setAccessKeyId(response.getCredentials().getAccessKeyId());
            federationToken.setAccessKeySecret(response.getCredentials().getAccessKeySecret());
            federationToken.setSecurityToken(response.getCredentials().getSecurityToken());
            final String expiration = response.getCredentials().getExpiration();
            final DateTime dateTime = ISODateTimeFormat.dateTime()
                    .withZone(DateTimeZone.UTC).parseDateTime(expiration);
            federationToken.setExpiration(dateTime.toDate());

            return federationToken;
        } catch (StsException e) {
            throw e;
        } catch (ClientException e) {
            throw new StsException("Error to getFederationToken", e.getErrCode(), e.getErrCode(), e);
        } catch (Exception e) {
            throw new StsException("Error to getFederationToken", null, e.getMessage(), e);
        }
    }
}
