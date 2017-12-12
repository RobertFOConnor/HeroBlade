package ie.ul.postgrad.socialanxietyapp.screens;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.R;

import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.ANXIETY_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.BATTLE_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.BLACKSMITH_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.CHEST_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.CRAFT_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.LEVEL_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.MAP_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.VILLAGE_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.WEAPON_INTRO_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.WELCOME_INFO;

/**
 * Created by Robert on 09-Nov-17.
 * <p>
 * Displays 3 screens of tutorial text.
 */

public class HelpFragment extends Fragment {

    private int pageNum;
    private String infoType;
    private boolean review;
    private static final String SURVEY_LINK = "https://www.surveymonkey.com/r/D9SDSJJ";

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_help_page, container, false);

        String[] helpInfoArray;
        switch (infoType) {

            case WELCOME_INFO:
                helpInfoArray = getResources().getStringArray(R.array.help_welcome_screens);
                if (pageNum == 0) {
                    ((ImageView) rootView.findViewById(R.id.help_img)).setImageResource(R.drawable.mood_4);
                    (rootView.findViewById(R.id.help_img)).setVisibility(View.VISIBLE);
                } else if (pageNum == 1) {
                    SharedPreferences prefs = getActivity().getSharedPreferences("ie.ul.postgrad.socialanxietyapp", Context.MODE_PRIVATE);
                    ((TextView) rootView.findViewById(R.id.app_code)).setText(prefs.getString("firstTimeUID", "").substring(0, 5));
                    (rootView.findViewById(R.id.app_code)).setVisibility(View.VISIBLE);
                    ImageView surveyButton = rootView.findViewById(R.id.help_img);
                    surveyButton.setImageResource(R.drawable.survey_button);
                    surveyButton.setVisibility(View.VISIBLE);
                    surveyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SURVEY_LINK));
                            startActivity(browserIntent);
                        }
                    });
                }
                break;

            case MAP_INFO:
                helpInfoArray = getArrText(R.array.help_map_screens);
                break;

            case WEAPON_INTRO_INFO:
                if (pageNum == 1) {
                    ((ImageView) rootView.findViewById(R.id.help_img)).setImageResource(R.drawable.weapon0043);
                    (rootView.findViewById(R.id.help_img)).setVisibility(View.VISIBLE);
                }
                helpInfoArray = getArrText(R.array.help_weapon_screens);
                break;
            case VILLAGE_INFO:
                helpInfoArray = getArrText(R.array.help_village_screens);
                break;
            case BLACKSMITH_INFO:
                helpInfoArray = getArrText(R.array.help_blacksmith_screens);
                break;
            case LEVEL_INFO:
                helpInfoArray = getArrText(R.array.help_levelup_screens);
                break;
            case CHEST_INFO:
                helpInfoArray = getArrText(R.array.help_chests_screens);
                break;
            case BATTLE_INFO:
                helpInfoArray = getArrText(R.array.help_battle_screens);
                if (pageNum == 0) {
                    ((ImageView) rootView.findViewById(R.id.help_img)).setImageResource(R.drawable.tap_game);
                    (rootView.findViewById(R.id.help_img)).setVisibility(View.VISIBLE);
                } else if (pageNum == 1) {
                    ((ImageView) rootView.findViewById(R.id.help_img)).setImageResource(R.drawable.type_adv);
                    (rootView.findViewById(R.id.help_img)).setVisibility(View.VISIBLE);
                }
                break;
            case CRAFT_INFO:
                helpInfoArray = getArrText(R.array.help_craft_screens);
                break;
            case ANXIETY_INFO:
                helpInfoArray = getArrText(R.array.anxiety_help);
                break;

            default:
                helpInfoArray = getArrText(R.array.help_welcome_screens);
                break;
        }
        ((TextView) rootView.findViewById(R.id.message_text)).setText(helpInfoArray[pageNum]);

        if (pageNum == helpInfoArray.length - 1) {//Place finish button on last swipe view.
            rootView.findViewById(R.id.swipe_label).setVisibility(View.GONE);
            rootView.findViewById(R.id.continue_button).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();

                    if (!review) {
                        switch (infoType) {
                            case HelpActivity.WELCOME_INFO:
                                Intent intent = new Intent(getContext(), InputNameActivity.class);
                                startActivity(intent);
                                break;
                            case HelpActivity.MAP_INFO: {
                                Intent i = new Intent(getContext(), TimeToWalkActivity.class);
                                startActivity(i);
                                break;
                            }
                            case HelpActivity.WEAPON_INTRO_INFO: {
                                Intent i = new Intent(getContext(), MapsActivity.class);
                                startActivity(i);
                                break;
                            }
                        }
                    }
                }
            });
        }
        //((ImageView) rootView.findViewById(R.id.help_image)).setImageResource(getResources().getIdentifier("help_" + String.format("%04d", pageNum), "drawable", getActivity().getPackageName()));
        return rootView;
    }

    private String[] getArrText(int id) {
        return getResources().getStringArray(id);
    }
}