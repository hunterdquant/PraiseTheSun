package com.hunterquant.praisethesun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hunter on 6/3/16.
 */
public class PlaceHolderFragment extends Fragment{

    public PlaceHolderFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        String [] forcast = {
          " this", "is", "me"
        };
        List<String> forcastList = new ArrayList<String>(Arrays.asList(forcast));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,
                                                R.id.list_item_forecast_textview, forcastList);

        ListView listView = (ListView) root.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        return root;
    }
}
