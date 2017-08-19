package bili.com.app.bili;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by liulongbing on 17/8/17.
 */

public class BilibiliApp extends Application{

    public static BilibiliApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        //内存泄漏检测工具
        LeakCanary.install(this);

        //Stetho调试工具
        Stetho.initialize(Stetho.newInitializerBuilder(this)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }

    public static BilibiliApp getInstance(){
        return mInstance;
    }


}
