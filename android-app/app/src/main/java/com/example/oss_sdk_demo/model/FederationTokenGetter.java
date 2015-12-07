package com.example.oss_sdk_demo.model;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;




/**
 * @author: zhouzhuo<yecan.xyc@alibaba-inc.com>
 * Apr 30, 2015
 *
 */
public class FederationTokenGetter {

    private static FederationToken token;
    
    public static FederationToken getToken(String serverAddress, String userId) {
        token = getTokenFromServer(serverAddress, userId);
        return token;
    }
    
    private static FederationToken getTokenFromServer(String serverAddress, String userId) {
        String queryUrl = "http://" + serverAddress + "/distribute-token.json?user-name="+ userId;
        String responseStr = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(queryUrl);
            HttpResponse hr = client.execute(httpGet);
            responseStr = EntityUtils.toString(hr.getEntity());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (responseStr == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            String ak = jsonObject.getString("accessKeyId");
            String sk = jsonObject.getString("accessKeySecret");
            String securityToken = jsonObject.getString("securityToken");
            String expireTime = jsonObject.getString("expiration");
            return new FederationToken(ak, sk, securityToken, expireTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
