package com.lljackie.WordBookApp;

import java.util.UUID;

class GUID {
    static String getGUID(){
        // 创建 GUID 对象
        UUID uuid = UUID.randomUUID();
        // 得到对象产生的ID
        String a = uuid.toString();
        // 转换为大写
        a = a.toUpperCase();
        // 替换 -
        a = a.replaceAll("-", "");
        return a;
    }
}
