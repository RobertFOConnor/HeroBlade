package ie.ul.postgrad.socialanxietyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ie.ul.postgrad.socialanxietyapp.game.Item;
import ie.ul.postgrad.socialanxietyapp.game.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.WorldItem;

public class ActionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ACTIVE_ITEM_ID = "id";
    private WorldItem activeItem;
    private Player player;

    private Button actionButton;
    private Button leaveButton;
    private ImageView itemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_action);

        Bundle b = getIntent().getExtras();
        activeItem = (WorldItem) ItemFactory.buildItem( b.getInt(ACTIVE_ITEM_ID) );

        player = MapsActivity.player;

        actionButton = (Button) findViewById(R.id.action_button);
        leaveButton = (Button) findViewById(R.id.leave_button);
        itemView = (ImageView) findViewById(R.id.item_view);

        itemView.setImageResource(activeItem.getImageID());
        ((TextView) findViewById(R.id.item_title)).setText(activeItem.getName());

        actionButton.setOnClickListener(this);
        leaveButton.setOnClickListener(this);
    }

    private void doAction() {
        int hitCount = activeItem.onHit();
        for(int i = 0; i < hitCount; i++) {
            player.getInventory().addItem(activeItem.getDropItemID());
        }

        Toast.makeText(getApplicationContext(), "You recieved " + hitCount + " " + ItemFactory.buildItem(activeItem.getDropItemID()).getName(), Toast.LENGTH_SHORT).show();
    }

    private void leave() {
        finish();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.action_button:
                doAction();
                break;

            case R.id.leave_button:
                leave();
                break;
        }
    }
}
