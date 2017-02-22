package ie.ul.postgrad.socialanxietyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import ie.ul.postgrad.socialanxietyapp.game.Player;

public class InventoryActivity extends AppCompatActivity {

    private Player player;
    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_inventory);

        player = MapsActivity.player;

        itemList = (ListView) findViewById(R.id.item_list);
        InventoryListAdapter adapter = new InventoryListAdapter(this, player.getInventory());
        itemList.setAdapter(adapter);
    }


}
