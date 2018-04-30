package com.csxd.cardemo;

import android.content.Context;

import com.iflytek.cloud.util.ResourceUtil;

/**
 * 描述:
 * author:41264
 * createtime:04/30/18
 */


public class OthersUtil {

    public static String getResource(Context context,String filePath) {
        final String resPath = ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, filePath);
        return resPath;
    }
}
