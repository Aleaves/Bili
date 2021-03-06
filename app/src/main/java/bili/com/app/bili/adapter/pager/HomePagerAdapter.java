package bili.com.app.bili.adapter.pager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import bili.com.app.bili.R;
import bili.com.app.bili.module.home.live.HomeLiveFragment;
import bili.com.app.bili.module.home.recommend.HomeRecommendedFragment;

/**
 * Created by liulongbing on 17/8/21.
 */

public class HomePagerAdapter extends FragmentPagerAdapter{

    private String[] TITLES;
    private Fragment[] fragments;

    public HomePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        TITLES = context.getResources().getStringArray(R.array.sections);
        fragments = new Fragment[TITLES.length];
    }

    @Override
    public Fragment getItem(int position) {
        if(fragments[position]==null){
            switch (position){
                case 0:
                    fragments[position] = HomeLiveFragment.newInstance();
                    break;
                case 1:
                    fragments[position] = HomeRecommendedFragment.newInstance();
                    break;
                case 2:
                    fragments[position] = HomeLiveFragment.newInstance();
                    break;
                case 3:
                    fragments[position] = HomeLiveFragment.newInstance();
                    break;
                case 4:
                    fragments[position] = HomeLiveFragment.newInstance();
                    break;
                case 5:
                    fragments[position] = HomeLiveFragment.newInstance();
                    break;
            }
        }
        return fragments[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

}

