package com.cz10000.mytestcustommap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cz10000.mytestcustommap.bean.Translation;
import com.cz10000.mytestcustommap.bean.Translation1;
import com.cz10000.mytestcustommap.bean.Translation2;
import com.cz10000.mytestcustommap.bean.Translation3;
import com.cz10000.mytestcustommap.inter.GetRequest_Interface;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = StartActivity.class.getSimpleName();
    private int i= 0  ;// 设置变量  模拟轮询请求次数

    private int currentRetryCount =0 ; //当前已重试次数
    private int maxConnnectCount = 10 ; //最大重试次数
    private int waitRetryTime = 0 ; //等待重试时间
    private io.reactivex.Observable<Translation1> observable1;
    private io.reactivex.Observable<Translation2> observable2;
    private Button btn9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        initView() ;
    }

    private void initView() {

        Button btn1 = findViewById(R.id.btn1) ;
        Button btn2 = findViewById(R.id.btn2) ;
        Button btn3 = findViewById(R.id.btn3) ;
        Button btn4 = findViewById(R.id.btn4) ;
        Button btn5 = findViewById(R.id.btn5) ;
        Button btn6 = findViewById(R.id.btn6) ;
        Button btn7 = findViewById(R.id.btn7) ;
        Button btn8 = findViewById(R.id.btn8) ;
        btn9 = findViewById(R.id.btn9) ;
        Button btn10 = findViewById(R.id.btn10) ;
        Button btn11 = findViewById(R.id.btn11) ;

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn10.setOnClickListener(this);
        btn11.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                testRx1() ;
                break;
            case R.id.btn2:
                testRxNet1() ;
                break;
            case R.id.btn3:
                testRxNet2() ;
                break;
            case R.id.btn4:
                testRxNetError() ;
                break;
            case R.id.btn5:
                testRxNestdNet() ; //嵌套网络请求
                break;
            case R.id.btn6:
                testRxjavaCache() ; //从磁盘内存缓存取数据
                break;
            case R.id.btn7:
                testRxjavaMerge() ;
                break;
            case R.id.btn8:
                startActivity(new Intent(this,TestRxjavaCombineActivity.class));
                break;
            case R.id.btn9:
                testRxjavaFangdou() ;
                break;
            case R.id.btn10:
                startActivity(new Intent(this,TestRxjavaSearchActivity.class));
                break;
            case R.id.btn11:
                startActivity(new Intent(this,TestShadowActivity.class));
                break;

        }

    }

    /**
     *   实现 功能防抖：
     *    需求： 用户只需要触发功能一次 但由于 外部原因  多次触发 ，出现冗余操作
     *    例如： 按钮点击进行网络请求   由于 外部网络不好 点击 1次后 用户发现无响应  多次点击
     *
     */
    private void testRxjavaFangdou() {
        RxView.clicks(btn9)
         /*
         * 1. 此处采用了RxBinding：RxView.clicks(button) = 对控件点击进行监听，需要引入依赖：compile 'com.jakewharton.rxbinding2:rxbinding:2.0.0'
         * 2. 传入Button控件，点击时，都会发送数据事件（但由于使用了throttleFirst（）操作符，所以只会发送该段时间内的第1次点击事件）
         **/
                .throttleFirst(2,TimeUnit.SECONDS)  //发送2S内第一次点击按钮的事件
                .subscribe(new io.reactivex.Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        Log.d(TAG, "onNext: 发送了网络请求");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: 对Complete事件作出响应");
                    }
                }) ;
    }

    /**
     *   合并数据源 使用merge /zip
     *   以 zip 为例
     *   进行 从不同数据源进行数据请求  合并网络请求的发送
     */
    private void testRxjavaMerge() {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://fy.iciba.com/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build() ;

            GetRequest_Interface getRequest_interface = retrofit.create(GetRequest_Interface.class) ;

        io.reactivex.Observable<Translation> observable1 = getRequest_interface.getCall() ;
        io.reactivex.Observable<Translation3> observable2 = getRequest_interface.getCall3() ;

        io.reactivex.Observable.zip(observable1, observable2, new BiFunction<Translation, Translation3, String>() {
            @Override
            public String apply(Translation translation, Translation3 translation3) throws Exception {
                return translation.show1()+"&"+translation3.show1();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept:最终接收到的数据是： "+s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }) ;

    }

    /**
     * 实现从磁盘 缓存或内存中获取数据
     * 类似于 图片的三级缓存机制
     * 优先从 内存缓存获取数据 如果有 直接获取 如果没有 去磁盘内存获取 ，。。
     * 优点：节省了 二次获取数据的成本 时间 流量
     */
    private void testRxjavaCache() {

        final String memoryCache = null ;
        final String diskCache = "从磁盘获取数据" ;

        //  设置第一个 Observable  检查内存缓存是否有该数据的缓存
        io.reactivex.Observable<String> memory = io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //先判断内存缓存是否有数据  如果有 直接发送 如果没有  则直接发送结束事件
                if(memoryCache!=null){
                    e.onNext(memoryCache);
                }else{
                    e.onComplete();
                }
            }
        }) ;

        //设置 第2个 observable  检查磁盘内存是否又该数据的缓存
        final io.reactivex.Observable<String> disk= io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if(diskCache!=null){
                    e.onNext(diskCache);
                }else{
                    e.onComplete();
                }
            }
        }) ;


        //设置 第3个Observable  通过网络获取数据
        io.reactivex.Observable<String> net = io.reactivex.Observable.just("从网络获取数据") ;


        /*
         * 通过concat（） 和 firstElement（）操作符实现缓存功能
         **/

        // 1. 通过concat（）合并memory、disk、network 3个被观察者的事件（即检查内存缓存、磁盘缓存 & 发送网络请求）
        //    并将它们按顺序串联成队列

        io.reactivex.Observable.concat(memory,disk,net)
                // 2. 通过firstElement()，从串联队列中取出并发送第1个有效事件（Next事件），即依次判断检查memory、disk、network
                .firstElement()
        // 即本例的逻辑为：
        // a. firstElement()取出第1个事件 = memory，即先判断内存缓存中有无数据缓存；由于memoryCache = null，即内存缓存中无数据，所以发送结束事件（视为无效事件）
        // b. firstElement()继续取出第2个事件 = disk，即判断磁盘缓存中有无数据缓存：由于diskCache ≠ null，即磁盘缓存中有数据，所以发送Next事件（有效事件）
        // c. 即firstElement()已发出第1个有效事件（disk事件），所以停止判断。

                //3.观察者订阅
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept: 最终获取的数据来源"+s);
                    }
                }) ;

    }

    /**
     *  实现嵌套网络请求
     *  使用 金山词霸api进行 模拟嵌套网络请求
     *    即 先进行一个网络请求 成功后在进行下一个网络请求
     *    比如 先注册 注册成功 才进行登录操作
     *
     *    为进行 模拟嵌套网络请求    修改请求网络接口  创建两个不用的请求服务类  translation1 translation2
     */
    private void testRxNestdNet() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://fy.iciba.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build() ;

            GetRequest_Interface getRequest_interface = retrofit.create(GetRequest_Interface.class) ;

       observable1 = getRequest_interface.getCall1() ;
       observable2 = getRequest_interface.getCall2() ;

            observable1.subscribeOn(Schedulers.io())  //初始被观察者 切换到io线程 进行网络请求1
                    .observeOn(AndroidSchedulers.mainThread()) //切换到主线程  新观察者 进行网络请求1的 请求处理
                    .doOnNext(new Consumer<Translation1>() {
                        @Override
                        public void accept(Translation1 translation1) throws Exception {
                            Log.d(TAG, "accept: 第一次网络请求成功");
                            translation1.show();
                        }
                    }).observeOn(Schedulers.io())
                    .flatMap(new Function<Translation1, ObservableSource<Translation2>>() {
                        @Override
                        public ObservableSource<Translation2> apply(Translation1 translation1) throws Exception {
                            return observable2;
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Translation2>() {
                        @Override
                        public void accept(Translation2 translation2) throws Exception {
                            Log.d(TAG, "accept:第2次网络请求成功");
                            translation2.show();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            System.out.println("登录失败");
                        }
                    }) ;


    }

    /**
     *   实现网络请求 错误重连机制
     *   进行网络请求的过程 会发生错误，根据返回的错误进行判断是否需要重新发送错误请求
     *    即差错自动重连机制
     *    如果进行重连 且要设计合理的规避算法  设置请求等待时间；设置最大重试次数；
     *
     */
    private void testRxNetError() {
        //1.创建retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build() ;
        //2.创建网络请求的接口实例
        GetRequest_Interface getRequest_interface = retrofit.create(GetRequest_Interface.class) ;
        //3.采用Observable<> 方式对网络进行封装
        io.reactivex.Observable<Translation> observable = getRequest_interface.getCall() ;
        //4.发送网络请求 & 通过retryWhen  重试  只有发生重要的异常时  才进行 retryWhen
        observable.retryWhen(new Function<io.reactivex.Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(io.reactivex.Observable<Throwable> throwableObservable) throws Exception {
               return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                   @Override
                   public ObservableSource<?> apply(Throwable throwable) throws Exception {
                       //输出异常信息

                       //根据异常类型选择是否重试
                       if(throwable instanceof IOException){
                           Log.d(TAG, "apply: io异常 需重试");
                           //  限制重试次数
                           if(currentRetryCount<maxConnnectCount){
                               /**
                                * 需求2：实现重试
                                * 通过返回的Observable发送的事件 = Next事件，从而使得retryWhen（）重订阅，最终实现重试功能
                                *
                                * 需求3：延迟1段时间再重试
                                * 采用delay操作符 = 延迟一段时间发送，以实现重试间隔设置
                                *
                                * 需求4：遇到的异常越多，时间越长
                                * 在delay操作符的等待时间内设置 = 每重试1次，增多延迟重试时间1s
                                */
                               // 设置等待时间
                               currentRetryCount++ ;
                               Log.d(TAG, "apply: 重试次数"+currentRetryCount);
                               waitRetryTime = 1000+currentRetryCount*1000 ;
                               return io.reactivex.Observable.just(1).delay(2000,TimeUnit.MILLISECONDS) ;
                           }else{
                               //若重试次数 >设置重试次数 则不重试
                               return io.reactivex.Observable.error(new Throwable("重试次数已超过"+currentRetryCount+"次，不再重试")) ;
                           }
                       }else{
                            return io.reactivex.Observable.error(new Throwable("发生了非网络异常")) ;
                       }
                   }
               })  ;
            }
        }).subscribeOn(Schedulers.io()) //切换到io线程 进行网络请求
                .observeOn(AndroidSchedulers.mainThread()) //切换到主线程进行请求结果的处理
                .subscribe(new io.reactivex.Observer<Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Translation value) {
                        Log.d(TAG, "onNext: 发送成功");
                        value.show();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                }) ;

    }

    /**
     *  实现网络轮询请求   有条件的
     *  即  当满足 某种条件时  才进行继续轮询请求。比如 ：当返回Error 或者 complete时  才继续轮询
     *   当轮询次数 少于5次时  继续请求 否则停止请求
     */
    private void testRxNet2() {
        //1、 创建Retrofit对象
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://fy.iciba.com/") //设置请求的baseUrl
                    .addConverterFactory(GsonConverterFactory.create()) //  支持gson解析  需要引入依赖
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  //支持rxjava
                    .build() ;
        //2、创建网络请求的接口实例
            GetRequest_Interface getRequest_interface = retrofit.create(GetRequest_Interface.class) ;
        //3、 采用Observable<> 形式 对网络请求进行封装
        io.reactivex.Observable<Translation> observable = getRequest_interface.getCall() ;

        //4、发送网络请求 使用repeatWhen 进行轮询
        observable.repeatWhen(new Function<io.reactivex.Observable<Object>, ObservableSource<?>>() {
            // 在Function函数中，必须对输入的 Observable<Object>进行处理，此处使用flatMap操作符接收上游的数据
            @Override
            public ObservableSource<?> apply(io.reactivex.Observable<Object> objectObservable) throws Exception {
                // 将原始 Observable 停止发送事件的标识（Complete（） /  Error（））转换成1个 Object 类型数据传递给1个新被观察者（Observable）
                // 以此决定是否重新订阅 & 发送原来的 Observable，即轮询
                // 此处有2种情况：
                // 1. 若返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable，即轮询结束
                // 2. 若返回其余事件，则重新订阅 & 发送原来的 Observable，即继续轮询
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        //加入  判断次数 当轮询==5次 后 停止请求
                        if(i>5){
                            return io.reactivex.Observable.error(new Throwable("轮询结束")) ;

                        }
                        //若轮询次数 <4次时  则发送1Next事件 继续进行轮询
                        // 这里加入了delay操作符 作用=延迟一段时间发送 此处设置 2000ms ，以实现轮询间隔设置
                        return io.reactivex.Observable.just(1).delay(2000,TimeUnit.MILLISECONDS);

                    }
                });
            }
        }).subscribeOn(Schedulers.io())  //切换到 io线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread()) // 切换到主线程进行请求结果的处理
                .subscribe(new io.reactivex.Observer<Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Translation value) {
                        value.show();
                        i++ ;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                }) ;
    }

    /**
     * 实现网络请求轮询
     * 客户端隔固定时间主动向服务器发送请求获取信息
     * 通过Rxjava 延时操作符 interval -无限次轮询  intervalRange - 有限次轮询
     *  之前的做法 是通过 handler 结合timer 定时器 来进行， 复杂
     *
     *
     *  以金山词霸 API  接口为例  进行网络请求
     */
    private void testRxNet1() {  //链式调用
         /*
         * 步骤1：采用interval（）延迟发送
         * 注：此处主要展示无限次轮询，若要实现有限次轮询，仅需将interval（）改成intervalRange（）即可
         **/
        io.reactivex.Observable.interval(2,1, TimeUnit.SECONDS)
        // 参数说明：
        // 参数1 = 第1次延迟时间；
        // 参数2 = 间隔时间数字；
        // 参数3 = 时间单位；
        // 该例子发送的事件特点：延迟2s后发送事件，每隔1秒产生1个数字（从0开始递增1，无限个）

                 /*
                  * 步骤2：每次发送数字前发送1次网络请求（doOnNext（）在执行Next事件前调用）
                  * 即每隔1秒产生1个数字前，就发送1次网络请求，从而实现轮询需求
                  **/
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        // a. 创建retrofit对象  使用retrofit进行网络请求
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://fy.iciba.com/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .build() ;

                        //b. 创建 网络请求接口的实例
                        GetRequest_Interface getRequest_interface = retrofit.create(GetRequest_Interface.class) ;

                        // c. 采用Observable<...> 形式 对网络请求进行封装
                       io.reactivex.Observable<Translation> observable = getRequest_interface.getCall() ;
                        //d. 通过线程切换进行网络请求
                        observable.subscribeOn(Schedulers.io())  //切换到io线程 进行网络请求
                                .observeOn(AndroidSchedulers.mainThread())  //切换回到主线程 处理请求结果
                                .subscribe(new io.reactivex.Observer<Translation>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Translation result) {
                                        result.show();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d(TAG, "onError: 请求失败");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                }) ;
                    }
                }).subscribe(new io.reactivex.Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long value) {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: 对Error事件响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onError: 对Complete事件响应");
            }
        }) ;


    }

    /**
     *   测试 rxjava 简单的应用
     */
    private void testRx1() {
        // Rxjava 的流式操作
        io.reactivex.Observable.create(new ObservableOnSubscribe<Integer>() {
            //1.创建被观察者 -->  生产事件
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).subscribe(new io.reactivex.Observer<Integer>() {
            //2.通过订阅 subscribe 连接观察者和被观察者
            //3.创建观察者  定义响应事件的行为
            @Override
            public void onSubscribe(Disposable d) {  //默认最先调用 onSubscribe
                Log.i(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer value) {
                Log.i(TAG, "onNext: "+value+"事件");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        }) ;
    }
}
