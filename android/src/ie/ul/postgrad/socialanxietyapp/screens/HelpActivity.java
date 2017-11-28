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
import android.view.View;
import android.widget.ImageView;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;

public class HelpActivity extends AppCompatActivity {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */

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
    private String infoType;

    public static final String REVIEW_KEY = "review_key";
    private boolean review;

    public static final String TRANSPARENT_KEY = "transparent_key";
    private boolean transparent;

    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        retrieveBundleInfo();

        if (!transparent) {
            setTheme(R.style.Theme_NoActionBar);
        }

        setContentView(R.layout.activity_help);

        if (!transparent) {
            App.setStatusBarColor(this);
            findViewById(R.id.background_view).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.title_color));
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);
    }

    private void retrieveBundleInfo() {
        Bundle bundle = getIntent().getExtras();

        try {
            infoType = bundle.getString(INFO_KEY, WELCOME_INFO);
            review = bundle.getBoolean(REVIEW_KEY, false);
            transparent = bundle.getBoolean(TRANSPARENT_KEY, false);
        } catch (NullPointerException e) {
            e.printStackTrace();
            infoType = WELCOME_INFO;
            review = false;
            transparent = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
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
