package ie.ul.postgrad.socialanxietyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import ie.ul.postgrad.socialanxietyapp.game.Player;

public class InventoryActivity extends AppCompatActivity {

    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_inventory);

        Bundle bundle = getIntent().getExtras();
        SparseIntArray array = bundle.getParcelable("player_items");

        itemList = (ListView) findViewById(R.id.item_list);
        InventoryListAdapter adapter = new InventoryListAdapter(this, array);
        itemList.setAdapter(adapter);

        if (array.size() == 0) {
            findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
        }
    }
}
