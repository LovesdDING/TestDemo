package com.cz10000.mytestcustommap;

import android.graphics.Outline;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

/**
 *    实现阴影风格
 *    ：让View  产生阴影的方式
 *
 *    1.控制 elevation
 *    2.使用 outlineProvider
 *    3.使用点9图
 *    4.使用Android 原生的MD风格控件 CardView FloatingActionBar
 *    5.translationZ
 *
 *
 *    Android  引入了Z轴 Z= elevation+translationZ  垂直屏幕的轴  来实现控件的阴影效果
 *
 *
 */
public class TestShadowActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_shadow);


        TextView textView = findViewById(R.id.myTV) ;

//        textView.setOutlineProvider(new ViewOutlineProvider() {
//            @Override
//            public void getOutline(View view, Outline outline) {
//                outline.setRect(5,5,5,5);
//            }
//        });


    }




}
