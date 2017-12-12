package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import java.util.Collections;
import java.util.List;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.MoodEntry;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.MoodAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;

public class MoodLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_log);
        setupMoodList();
        setupBars();
    }

    private void setupMoodList() {
        List<MoodEntry> list = GameManager.getDbHelper().getMoodEntries();
        Collections.reverse(list);

        RecyclerView moodList = findViewById(R.id.mood_list);
        MoodAdapter moodAdapter = new MoodAdapter(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        moodList.setLayoutManager(mLayoutManager);
        moodList.setAdapter(moodAdapter);

        if (list.size() > 0) {
            findViewById(R.id.empty_message).setVisibility(View.GONE);
        }
    }

    private void setupBars() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Mood Ratings");
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
