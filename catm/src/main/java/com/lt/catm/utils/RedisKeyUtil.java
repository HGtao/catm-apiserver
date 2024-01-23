package com.lt.catm.utils;


import com.lt.catm.common.Constants;

public class RedisKeyUtil {

    private static final String PREFIX = Constants.APP_NAME;

    public static String getPrivateKeyCacheKey(String kid) {
        // 构建缓存PrivateKey的redis key: 构建规则: 项目名:功能模块:数据名称:唯一标识
        return PREFIX + ":login:private_key:" + kid;
    }
}


