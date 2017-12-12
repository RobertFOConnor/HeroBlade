package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.FontManager;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;

public class InputNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_name_actvity);
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);
        setupInputButton();
        setupBars();
    }

    private void setupInputButton() {
        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText) findViewById(R.id.name_field)).getText().toString();
                Context context = getApplicationContext();
                if (name.length() > 0) {
                    SharedPreferences prefs = context.getSharedPreferences("ie.ul.postgrad.socialanxietyapp", Context.MODE_PRIVATE);
                    GameManager gm = GameManager.getInstance();
                    gm.initDatabase(context, prefs.getString("firstTimeUID", ""), "Guest", "guest@guest.com", "");
                    Player p = gm.getPlayer();
                    p.setName(name);
                    gm.updatePlayerInDatabase(p);
                    SoundManager.getInstance(context).playSound(SoundManager.Sound.CLICK);
                    Intent i = new Intent(context, AvatarCustomizationActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void setupBars() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        App.setStatusBarColor(this);
    }
}
