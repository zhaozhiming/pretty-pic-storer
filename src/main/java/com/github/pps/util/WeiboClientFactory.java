package com.github.pps.util;

import weiboclient4j.WeiboClient;

public class WeiboClientFactory {
    private static WeiboClient client;

    public static WeiboClient getInstacne(String appKey, String appSecret) {
        if (client == null) {
            client = new WeiboClient(appKey, appSecret);
        }
        return client;
    }
}
