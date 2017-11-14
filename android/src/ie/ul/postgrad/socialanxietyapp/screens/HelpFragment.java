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
        ((JarvisButton) rootView.findViewById(R.id.jarvis)).setJarvisStyle(pageNum);
        //((ImageView) rootView.findViewById(R.id.help_image)).setImageResource(getResources().getIdentifier("help_" + String.format("%04d", pageNum), "drawable", getActivity().getPackageName()));
        return rootView;
    }
}