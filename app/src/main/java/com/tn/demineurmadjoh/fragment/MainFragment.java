package com.tn.demineurmadjoh.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tn.demineurmadjoh.R;
import com.tn.demineurmadjoh.activity.MainActivity;
import com.tn.demineurmadjoh.helper.DmDatabaseHelper;
import com.tn.demineurmadjoh.model.DmGame;



public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;


    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Injecté le layout pour le fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button buttonNewGameEasy = (Button) rootView.findViewById(R.id.main__button_new_game_easy);
        //Facile
        buttonNewGameEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {

                    new MainActivity().setDifficulty("easy");
                    mListener.onRequestToCreateNewGame();
                }
            }
        });
        //Moyenne
        Button buttonNewGameNormal = (Button) rootView.findViewById(R.id.main__button_new_game_normal);
        buttonNewGameNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {

                    new MainActivity().setDifficulty("normal");
                    mListener.onRequestToCreateNewGame();
                }
            }
        });
        //Difficile
        Button buttonNewGameDifficult = (Button) rootView.findViewById(R.id.main__button_new_game_difficult);
        buttonNewGameDifficult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {

                    new MainActivity().setDifficulty("difficult");
                    mListener.onRequestToCreateNewGame();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateViews();
    }

    public void updateViews() {
        DmDatabaseHelper databaseHelper = DmDatabaseHelper.getInstance(getActivity());
        DmGame game = DmGame.loadGame(databaseHelper);
    }

    public interface OnFragmentInteractionListener {
        public void onRequestToCreateNewGame();
    }
}
