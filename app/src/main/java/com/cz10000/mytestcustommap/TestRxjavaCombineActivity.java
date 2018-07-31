package com.cz10000.mytestcustommap;

import android.database.Observable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.nio.charset.CharsetDecoder;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

public class TestRxjavaCombineActivity extends AppCompatActivity {

    private final String TAG = TestRxjavaCombineActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_rxjava_combine);


        initTestRxjava() ;
    }

    private void initTestRxjava() {
        final EditText etName = findViewById(R.id.name) ;
        final EditText etAge = findViewById(R.id.age) ;
        final EditText etJob = findViewById(R.id.job) ;

        final Button btn = findViewById(R.id.list) ;


        /*
         * 步骤2：为每个EditText设置被观察者，用于发送监听事件
         * 说明：
         * 1. 此处采用了RxBinding：RxTextView.textChanges(name) = 对对控件数据变更进行监听（功能类似TextWatcher），需要引入依赖：  compile 'com.jakewharton.rxbinding2:rxbinding:2.0.0'
         * 2. 传入EditText控件，点击任1个EditText撰写时，都会发送数据事件 = Function3（）的返回值（下面会详细说明）
         * 3. 采用skip(1)原因：跳过 一开始EditText无任何输入时的空值
         **/
        io.reactivex.Observable<CharSequence> nameObservable = RxTextView.textChanges(etName).skip(1) ;
        io.reactivex.Observable<CharSequence> ageObservable = RxTextView.textChanges(etAge).skip(1) ;
        io.reactivex.Observable<CharSequence> jobObservable = RxTextView.textChanges(etJob).skip(1) ;

        io.reactivex.Observable.combineLatest(nameObservable, ageObservable, jobObservable, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) throws Exception {
                //规定表单详情输入 不能为空
                boolean isNameValid = !TextUtils.isEmpty(etName.getText()) ;
                // 除了设置为空，也可设置长度限制
                // boolean isUserNameValid = !TextUtils.isEmpty(name.getText()) && (name.getText().toString().length() > 2 && name.getText().toString().length() < 9);
                boolean isAgeValid = !TextUtils.isEmpty(etAge.getText()) ;
                boolean isJobValid = !TextUtils.isEmpty(etJob.getText()) ;


                return isNameValid&&isAgeValid&&isJobValid ;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d(TAG, "accept: 按钮是否可点击："+aBoolean);
                        btn.setEnabled(aBoolean);
            }
        }) ;

    }



}
