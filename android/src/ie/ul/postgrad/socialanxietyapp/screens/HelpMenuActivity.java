package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.HelpListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;

import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.BATTLE_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.BLACKSMITH_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.CHEST_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.CRAFT_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.INFO_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.LEVEL_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.MAP_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.REVIEW_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.TRANSPARENT_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.VILLAGE_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.WELCOME_INFO;

public class HelpMenuActivity extends AppCompatActivity {

    private ListView helpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_menu);
        String[] helpItems = getResources().getStringArray(R.array.help_list_items);
        ArrayList<String> helpArr = new ArrayList<>();
        helpArr.addAll(Arrays.asList(helpItems));
        helpList = findViewById(R.id.help_list);
        helpList.setAdapter(new HelpListAdapter(this, helpArr));


        helpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent helpActivity = new Intent(getApplicationContext(), HelpActivity.class);
                helpActivity.putExtra(REVIEW_KEY, true);
                helpActivity.putExtra(TRANSPARENT_KEY, false);

                switch (position) {

                    case 0:
                        helpActivity.putExtra(INFO_KEY, WELCOME_INFO);
                        break;
                    case 1:
                        helpActivity.putExtra(INFO_KEY, MAP_INFO);
                        break;
                    case 2:
                        helpActivity.putExtra(INFO_KEY, VILLAGE_INFO);
                        break;
                    case 3:
                        helpActivity.putExtra(INFO_KEY, BLACKSMITH_INFO);
                        break;
                    case 4:
                        helpActivity.putExtra(INFO_KEY, LEVEL_INFO);
                        break;
                    case 5:
                        helpActivity.putExtra(INFO_KEY, CHEST_INFO);
                        break;
                    case 6:
                        helpActivity.putExtra(INFO_KEY, BATTLE_INFO);
                        break;
                    case 7:
                        helpActivity.putExtra(INFO_KEY, CRAFT_INFO);
                        break;

                }
                SoundManager.getInstance(getApplicationContext()).playSound(SoundManager.Sound.CLICK);
                startActivity(helpActivity);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.help));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        App.setStatusBarColor(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                SoundManager.getInstance(this).playSound(SoundManager.Sound.BACK);
                break;
        }
        return true;
    }
}
