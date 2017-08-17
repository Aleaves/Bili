package bili.com.app.bili;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by liulongbing on 17/8/17.
 */

public class BilibiliApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //内存泄漏检测工具
        LeakCanary.install(this);

        //Stetho调试工具
        Stetho.initialize(Stetho.newInitializerBuilder(this)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }

}
