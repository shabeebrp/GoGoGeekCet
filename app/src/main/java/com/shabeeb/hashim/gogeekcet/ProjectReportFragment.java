package com.shabeeb.hashim.gogeekcet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProjectReportFragment extends Fragment {
    ListView listView;
    String URL = "http://robo-store-cet.herokuapp.com/reports";
    ArrayList<String> nameList, linkList;
    ArrayAdapter<String> adapter;
    ProgressDialog pd;

    public ProjectReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_report, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        listView = (ListView) getActivity().findViewById(R.id.project_LV);


        pd = new ProgressDialog(getActivity());
        pd.setMessage("Getting data");
        pd.setTitle("Please Wait");
        pd.show();
        AndroidNetworking.get(URL)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        populateList(response);
                        //adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,nameList);
                        adapter = new ArrayAdapter<String>(getActivity(), R.layout.link_list, R.id.linkTV, nameList);
                        listView.setAdapter(adapter);
                        listView.setDividerHeight(5);
                        pd.hide();
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        pd.hide();
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(linkList.get(position)));
                startActivity(i);
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    void populateList(JSONArray array) {
        int length = array.length();
        String name, link;
        nameList = new ArrayList<>();
        linkList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                name = obj.getString("name");

                link = obj.getString("link");

                nameList.add(name);
                linkList.add(link);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onStop() {
        if (pd != null)
            pd.dismiss();
        super.onStop();
    }
}
