package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;

public class HelpActivity extends AppCompatActivity {

    public static final String INFO_KEY = "info_key";
    public static final String WELCOME_INFO = "welcome";
    public static final String WEAPON_INTRO_INFO = "weapon_intro";
    public static final String MAP_INFO = "map";
    public static final String VILLAGE_INFO = "village";
    public static final String BLACKSMITH_INFO = "blacksmith";
    public static final String LEVEL_INFO = "levelup";
    public static final String CHEST_INFO = "chest";
    public static final String BATTLE_INFO = "battle";
    public static final String CRAFT_INFO = "craft";
    public static final String ANXIETY_INFO = "sad";
    public static final String REVIEW_KEY = "review_key";
    public static final String TRANSPARENT_KEY = "transparent_key";
    private String infoType;
    private boolean review;
    private boolean transparent;
    private ViewPager mPager;
    private static final int NUM_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveBundleInfo();
        setTheme();
        setContentView(R.layout.activity_help);
        setBGColor();
        setupViewPager();
    }

    private void retrieveBundleInfo() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            infoType = bundle.getString(INFO_KEY, WELCOME_INFO);
            review = bundle.getBoolean(REVIEW_KEY, false);
            transparent = bundle.getBoolean(TRANSPARENT_KEY, false);
        } else {
            infoType = WELCOME_INFO;
            review = false;
            transparent = false;
        }
    }

    private void setTheme() {
        if (!transparent) {
            setTheme(R.style.Theme_NoActionBar);
        }
    }

    private void setBGColor() {
        if (!transparent) {
            App.setStatusBarColor(this);
            findViewById(R.id.background_view).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.title_color));
        }
    }

    private void setupViewPager() {
        mPager = findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 3 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            HelpFragment helpFragment = new HelpFragment();
            helpFragment.setPageNum(position);
            helpFragment.setInfoType(infoType);
            helpFragment.setReview(review);
            return helpFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
