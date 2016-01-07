package com.cabalry.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabalry.R;

/**
 * LoadingDialogFragment
 */
public class LoadingDialogFragment extends DialogFragment {
    private static final String TAG = "LoadingDialogFragment";

    //STYLE_NORMAL
    //STYLE_NO_TITLE
    //STYLE_NO_FRAME
    //STYLE_NO_INPUT
    //STYLE_NORMAL
    //STYLE_NORMAL
    //STYLE_NO_TITLE
    //STYLE_NO_FRAME
    //STYLE_NORMAL

    //Theme_Holo
    //Theme_Holo_Light_Dialog
    //Theme_Holo_Light
    //Theme_Holo_Light_Panel
    //Theme_Holo_Light

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_FRAME, 0);
        /*
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
          .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
          .show(somefrag)
          .commit();
         */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_loading_dialog, container, false);
        getDialog().setTitle("DialogFragment");

        return rootView;
    }
}
