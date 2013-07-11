package com.github.pps.util;

import weiboclient4j.WeiboClient;

public class WeiboClientFactory {
    private static WeiboClient client;

    public static WeiboClient getInstacne() {
        if (client == null) {
            client = new WeiboClient("your app id", "your app secret");
        }
        return client;
    }
}
