package bili.com.app.bili.network;

import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import bili.com.app.bili.BilibiliApp;
import bili.com.app.bili.BuildConfig;
import bili.com.app.bili.network.api.BiliApiService;
import bili.com.app.bili.network.api.BiliAppService;
import bili.com.app.bili.network.api.GankService;
import bili.com.app.bili.network.api.LiveService;
import bili.com.app.bili.network.auxiliary.ApiConstants;
import bili.com.app.bili.utils.CommonUtil;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liulongbing on 17/8/21.
 */

public class RetrofitHelper {

    private static OkHttpClient mOkHttpClient;

    static {
        initOkHttpClient();
    }

    public static LiveService getLiveAPI() {
        return createApi(LiveService.class, ApiConstants.LIVE_BASE_URL);
    }

    public static BiliAppService getBiliAppAPI() {
        return createApi(BiliAppService.class, ApiConstants.APP_BASE_URL);
    }

    public static GankService getGankApi(){
        return createApi(GankService.class,ApiConstants.GANK_BASE_URL);
    }

    public static BiliApiService getBiliAPI() {
        return createApi(BiliApiService.class, ApiConstants.API_BASE_URL);
    }

    public static void initOkHttpClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if(mOkHttpClient == null){
            synchronized (RetrofitHelper.class) {
                if (mOkHttpClient == null) {
                    Cache cache = new Cache(new File(BilibiliApp.getInstance().getCacheDir(),"HttpCache"),1024*1024*10);
                    CacheInterceptor cacheInterceptor = new CacheInterceptor();
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            .addInterceptor(cacheInterceptor)
                            .addNetworkInterceptor(cacheInterceptor)
                            .addNetworkInterceptor(new StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20,TimeUnit.SECONDS)
                            .readTimeout(20,TimeUnit.SECONDS)
                            .addInterceptor(new UserAgentInterceptor())
                            .build();
                }
            }
        }
    }

    /**
     * 为okhttp添加缓存，这里是考虑到服务器不支持缓存时，从而让okhttp支持缓存
     */
    private static class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            // 有网络时 设置缓存超时时间1个小时
            int maxAge = 60*60;
            // 无网络时，设置超时为1天
            int maxStale = 60 * 60 * 24;
            Request request = chain.request();
            if (CommonUtil.isNetworkAvailable(BilibiliApp.getInstance())) {
                //有网络时只从网络获取
                request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
            } else {
                //无网络时只从缓存中读取
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response response = chain.proceed(request);
            if (CommonUtil.isNetworkAvailable(BilibiliApp.getInstance())) {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }

    private static class UserAgentInterceptor implements Interceptor{

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", ApiConstants.COMMON_UA_STR)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }

    }

    private static <T> T createApi(Class<T> clazz,String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

}
