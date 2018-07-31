package com.cz10000.mytestcustommap.inter;


import com.cz10000.mytestcustommap.bean.Translation;
import com.cz10000.mytestcustommap.bean.Translation1;
import com.cz10000.mytestcustommap.bean.Translation2;
import com.cz10000.mytestcustommap.bean.Translation3;


import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 *  创建用于描述网络请求的接口
 * Created by cz10000_001 on 2018/5/23.
 */

public interface GetRequest_Interface {
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20world")
    Observable<Translation> getCall() ;
    // 注解里传入 网络请求 的部分URL地址
    // Retrofit把网络请求的URL分成了两部分：一部分放在Retrofit对象里，另一部分放在网络请求接口里
    // 如果接口里的url是一个完整的网址，那么放在Retrofit对象里的URL可以忽略
    // 采用Observable<...>接口
    // getCall()是接受网络请求数据的方法

    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20register")
    Observable<Translation1> getCall1() ;
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20login")
    Observable<Translation2>  getCall2() ;
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20china")
    Observable<Translation3> getCall3() ;
}
