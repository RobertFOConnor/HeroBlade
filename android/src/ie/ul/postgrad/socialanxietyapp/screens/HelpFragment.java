package ie.ul.postgrad.socialanxietyapp.screens;

/**
 * Created by Robert on 09-Nov-17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.R;

public class HelpFragment extends Fragment {

    private int pageNum;

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_help_page, container, false);
        ((TextView) rootView.findViewById(R.id.message_text)).setText(getResources().getStringArray(R.array.help_screens)[pageNum]);

        if(pageNum == 2) {
            rootView.findViewById(R.id.swipe_label).setVisibility(View.GONE);
            rootView.findViewById(R.id.continue_button).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
        //((ImageView) rootView.findViewById(R.id.help_image)).setImageResource(getResources().getIdentifier("help_" + String.format("%04d", pageNum), "drawable", getActivity().getPackageName()));
        return rootView;
    }
}