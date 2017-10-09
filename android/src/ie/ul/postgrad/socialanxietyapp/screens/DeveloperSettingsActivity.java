package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.account.WelcomeActivity;
import ie.ul.postgrad.socialanxietyapp.adapter.SettingsListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.sync.SyncManager;

public class DeveloperSettingsActivity extends AppCompatActivity {

    private String[] menu = new String[]{"Log Out", "Test Mode", "Test Chest", "Erase Data", "Test XP", "Test Remote Server", "Open player search"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.settings));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        App.setStatusBarColor(this);

        ListView settingsList = (ListView) findViewById(R.id.settings_list);
        SettingsListAdapter settingsAdapter = new SettingsListAdapter(this, menu);
        settingsList.setAdapter(settingsAdapter);

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //App.getInstance().getGoogleApiHelperInstance().disconnect();
                        break;
                    case 1:
                        if (GameManager.TESTING) {
                            GameManager.TESTING = false;
                            App.showToast(getApplicationContext(), "Test mode off.");
                        } else {
                            GameManager.TESTING = true;
                            App.showToast(getApplicationContext(), "Test mode on.");
                        }
                        break;
                    case 2:
                        GameManager.getInstance().awardTestChest(getApplicationContext());
                        App.showToast(getApplicationContext(), "Test chest given to player.");

                        break;
                    case 3:
                        GameManager.getInstance().eraseData();
                        Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case 4:
                        GameManager.getInstance().awardXP(getApplicationContext(), 136);
                        GameManager.getInstance().awardMoney(100);
                        App.showToast(getApplicationContext(), "136XP given to player.");
                        break;
                    case 5:
                        SyncManager.getInstance().forceSync();
                        App.showToast(getApplicationContext(), "Testing remote server, check netbeans...");
                        break;
                    case 6:

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://193.1.99.30:8080/AnxietyAppServer/"));
                        startActivity(browserIntent);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
