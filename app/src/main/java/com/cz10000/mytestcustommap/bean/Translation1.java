package com.cz10000.mytestcustommap.bean;

import android.util.Log;

/**
 * Created by cz10000_001 on 2018/5/24.
 */

public class Translation1 {
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
        Log.d("RxJava 翻译内容", content.out );
    }
}
