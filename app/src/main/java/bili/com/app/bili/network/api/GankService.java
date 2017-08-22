package bili.com.app.bili.network.api;

import bili.com.app.bili.entity.gank.GirlData;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by liulongbing on 17/8/22.
 */

public interface GankService {
    @GET("data/福利/10/{page}")
    Observable<GirlData> getGirls(@Path("page") int page);
}
