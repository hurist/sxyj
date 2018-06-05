package com.ffcc66.sxyj.personal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ffcc66.sxyj.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment {

    private static final String TAG = "PersonalFragment";


    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

}
