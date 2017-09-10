package ie.ul.postgrad.socialanxietyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 20-Feb-17.
 * <p>
 * List adapter for displaying weapon index.
 */

public class WeaponIndexAdapter extends ArrayAdapter<WeaponItem> {


    public WeaponIndexAdapter(Context context, ArrayList<WeaponItem> weapons) {
        super(context, R.layout.fragment_weapon_index_item, weapons);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        WeaponItem weaponItem = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_weapon_index_item, parent, false);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.descText = (TextView) convertView.findViewById(R.id.item_desc);
            viewHolder.damageText = (TextView) convertView.findViewById(R.id.damage_field);
            viewHolder.durabilityText = (TextView) convertView.findViewById(R.id.health_field);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.imgType = (ImageView) convertView.findViewById(R.id.item_image_type);
            viewHolder.bg = (LinearLayout) convertView.findViewById(R.id.item_bg);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (weaponItem != null) {
            viewHolder.nameText.setText(weaponItem.getName());
            viewHolder.descText.setText(weaponItem.getDescription());
            viewHolder.damageText.setText("DMG: " + weaponItem.getDamage());
            viewHolder.durabilityText.setText("DU: " + weaponItem.getCurrHealth() + "/" + weaponItem.getMaxHealth());
            viewHolder.img.setImageResource(getContext().getResources().getIdentifier("weapon" + String.format("%04d", weaponItem.getId()), "drawable", getContext().getPackageName()));
            viewHolder.imgType.setImageResource(weaponItem.getTypeDrawableRes());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView nameText;
        TextView descText;
        TextView damageText;
        TextView durabilityText;
        ImageView img;
        ImageView imgType;
        LinearLayout bg;
    }
}
