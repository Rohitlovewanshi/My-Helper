package com.project.myhelper;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageListViewActivity extends Fragment {

    DatabaseHelper myDb;
    ListView listView;
    RelativeLayout noMessageLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_message_list_view,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("HISTORY");

        listView=getActivity().findViewById(R.id.listViewMessages);
        noMessageLayout=getActivity().findViewById(R.id.no_message_layout);

        List<HashMap<String,String>>listItems=new ArrayList<>();
        SimpleAdapter adapter=new SimpleAdapter(getContext(),listItems,R.layout.list_item, new String[]{"First Line","Second Line","Third Line","Fourth Line"}, new int[]{R.id.text1,R.id.text2,R.id.text3,R.id.text4});

        myDb=new DatabaseHelper(getContext());
        ArrayList arrayList=myDb.getAllMessages();

        if(arrayList.size()==0){
            noMessageLayout.setVisibility(View.VISIBLE);
            return;
        }

        for(int i=arrayList.size()-1;i>=0;){
            HashMap<String,String>resultmap=new HashMap<>();
            resultmap.put("Third Line",arrayList.get(i-2).toString()+" :- "+arrayList.get(i-1).toString());
            resultmap.put("Fourth Line",arrayList.get(i).toString());
            i-=3;
            resultmap.put("First Line",arrayList.get(i-2).toString()+" :- "+arrayList.get(i-1).toString());
            resultmap.put("Second Line",arrayList.get(i).toString());
            i-=3;
            listItems.add(resultmap);
        }
        listView.setAdapter(adapter);
    }
}
