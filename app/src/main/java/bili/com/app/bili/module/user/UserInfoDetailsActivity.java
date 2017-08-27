package bili.com.app.bili.module.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import bili.com.app.bili.R;
import bili.com.app.bili.base.RxBaseActivity;

import static bili.com.app.bili.utils.ConstantUtil.EXTRA_MID;

/**
 * Created by liulongbing on 17/8/24.
 */

public class UserInfoDetailsActivity extends RxBaseActivity{

    private static final String EXTRA_USER_NAME = "extra_user_name", EXTRA_MID = "extra_mid", EXTRA_AVATAR_URL = "extra_avatar_url";

    public static void launch(Activity activity, String name, int mid, String avatar_url) {
        Intent intent = new Intent(activity, UserInfoDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_USER_NAME, name);
        intent.putExtra(EXTRA_MID, mid);
        intent.putExtra(EXTRA_AVATAR_URL, avatar_url);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initToolBar() {

    }


}
