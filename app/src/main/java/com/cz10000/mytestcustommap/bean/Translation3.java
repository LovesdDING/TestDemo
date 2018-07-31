package com.cz10000.mytestcustommap.bean;

import android.util.Log;

/**
 * Created by cz10000_001 on 2018/5/24.
 */

public class Translation3 {
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
        return "第2次翻译:"+content.out ;
    }
}
