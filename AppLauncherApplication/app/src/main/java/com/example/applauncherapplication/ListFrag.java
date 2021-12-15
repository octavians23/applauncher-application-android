package com.example.applauncherapplication;

import static com.example.applauncherapplication.ApplicationClass.softwareApps;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFrag extends Fragment {

    private RecyclerView recyclerview;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View view;

    public ListFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_list, container, false);

        ((MainActivity) requireActivity()).setFragmentRefreshListener((item, softwareApps) -> {
            if(item.equals("Sort by name")){
                softwareApps.sort(Comparator.comparing(p -> p.getAppName().toLowerCase()));
                //Log.d("DEBUG", softwareApps.toString());
                myAdapter = new ApplicationAdapter(getActivity(), softwareApps);
                recyclerview.setAdapter(myAdapter);
            } else if(item.equals("Sort by package")){
                softwareApps.sort(Comparator.comparing(p -> p.getPackageName().toLowerCase()));
                myAdapter = new ApplicationAdapter(getActivity(), softwareApps);
                recyclerview.setAdapter(myAdapter);
            } else if(item.equals("Sort by recently used")){
                //Log.d("DEBUG", softwareApps.toString());
                softwareApps.sort(Comparator.comparing(SoftwareApp::getLastTimeUsed).reversed());
                myAdapter = new ApplicationAdapter(getActivity(), softwareApps);
                recyclerview.setAdapter(myAdapter);
            }else if(item.equals("Sort by memory")){
                softwareApps.sort(Comparator.comparing(SoftwareApp::getAppSize).reversed());
                myAdapter = new ApplicationAdapter(getActivity(), softwareApps);
                recyclerview.setAdapter(myAdapter);
            } else if(item.equals("None")){
                //Log.d("DEBUG", softwareApps.toString());
                myAdapter = new ApplicationAdapter(getActivity(), softwareApps);
                recyclerview.setAdapter(myAdapter);
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerview = view.findViewById(R.id.list);
        recyclerview.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerview.setLayoutManager(layoutManager);

        myAdapter = new ApplicationAdapter(this.getActivity(), ApplicationClass.softwareApps);
        recyclerview.setAdapter(myAdapter);
    }
}
