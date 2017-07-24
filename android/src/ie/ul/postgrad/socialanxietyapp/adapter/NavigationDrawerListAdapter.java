package ie.ul.postgrad.socialanxietyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.screens.MapsActivity;
import ie.ul.postgrad.socialanxietyapp.screens.PlayerAvatarActivity;

/**
 * Created by Robert on 18/03/2017.
 */

public class NavigationDrawerListAdapter extends BaseAdapter {

    private String[] titles;
    private int[] iconIds;
    private static LayoutInflater inflater = null;
    private Context context;

    //Menu item indexes.
    public static final int PROFILE = 0;
    public static final int INVENTORY = 1;
    public static final int WEAPONS = 2;
    public static final int QUESTS = 3;
    public static final int CRAFTING = 4;
    public static final int ACHIEVEMENTS = 5;
    public static final int SETTINGS = 6;

    public NavigationDrawerListAdapter(Context context, String[] titles, int[] iconIds) {
        this.titles = titles;
        this.iconIds = iconIds;

        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return titles.length + 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;

        if (position == PROFILE) {
            rowView = inflater.inflate(R.layout.fragment_nav_profile_display, null);
            ((TextView) rowView.findViewById(R.id.name_display)).setText(GameManager.getInstance().getPlayer().getName());
            ((TextView) rowView.findViewById(R.id.level_display)).setText("Level: " + GameManager.getInstance().getPlayer().getLevel() + "");

            (rowView.findViewById(R.id.profile_pic_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Intent i = new Intent(context, PlayerAvatarActivity.class);
                    context.startActivity(i);*/
                    Intent i = new Intent(context, PlayerAvatarActivity.class); //LibGDX Activity!
                    //i.putExtra(AndroidLauncher.screenString, MainGame.AVATAR_SCREEN);
                    context.startActivity(i);

                    if (context instanceof MapsActivity) {
                        ((MapsActivity) context).onBackPressed(); //close the drawer
                    }
                }
            });

        } else {

            rowView = inflater.inflate(R.layout.fragment_nav_menu_item, null);
            holder.tv = (TextView) rowView.findViewById(R.id.title);
            holder.img = (ImageView) rowView.findViewById(R.id.image);
            holder.tv.setText(titles[position - 1]);
            holder.img.setImageResource(iconIds[position - 1]);
        }
        return rowView;
    }
}
