package com.ffcc66.sxyj.bookcase;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ffcc66.sxyj.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookCaseFragment extends Fragment {

    private ListView lvBookcase;


    public BookCaseFragment() {
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_book_case, container, false);

        lvBookcase = view.findViewById(R.id.lvBookcase);


        return view;
    }

}
