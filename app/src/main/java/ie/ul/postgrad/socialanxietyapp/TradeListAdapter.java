package ie.ul.postgrad.socialanxietyapp;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ie.ul.postgrad.socialanxietyapp.game.ItemFactory;

/**
 * Created by Robert on 20-Feb-17.
 */

public class TradeListAdapter extends BaseAdapter {

    private String[] result;
    private Context context;
    private String[] deals;
    private static LayoutInflater inflater = null;

    public TradeListAdapter(Context context, String[] names, String[] deals) {
        // TODO Auto-generated constructor stub
        result = names;
        this.deals = deals;

        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
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
        TextView tv2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.fragment_trade_item, null);
        holder.tv = (TextView) rowView.findViewById(R.id.trader_title);
        holder.tv2 = (TextView) rowView.findViewById(R.id.trade_deal);
        holder.tv.setText(result[position]);
        holder.tv2.setText(deals[position]);
        return rowView;
    }
}
