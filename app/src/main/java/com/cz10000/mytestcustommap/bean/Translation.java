package com.cz10000.mytestcustommap.bean;

import android.util.Log;

/**
 * 创建接收 服务器返回的类
 * Created by cz10000_001 on 2018/5/23.
 */

public class Translation {
    private int status;

    private content content;
    private static class content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;
    }

    //定义 输出返回数据 的方法
    public void show() {
        Log.d("RxJava", content.out );
    }

    public String show1(){
        return "第一次翻译:"+content.out ;
    }


}
