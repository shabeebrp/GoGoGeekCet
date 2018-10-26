package com.shabeeb.hashim.gogeekcet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shabeeb on 3/2/17.
 */

public class OrderVerifyDialog extends DialogFragment {
    String URL = "http://robo-store-cet.herokuapp.com";
    ListView listView;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.order_verify_dialog, null);
        builder.setView(view);
        listView = (ListView) view.findViewById(R.id.order_verifyLV);


        ArrayList<String> list = getItems(getArguments().getString("order"));


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        listView.setDividerHeight(5);
        builder.setTitle("Selected Items");
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                OrderDialogFragment frag = new OrderDialogFragment();
                frag.setTargetFragment(getTargetFragment(),0);
                frag.setArguments(getArguments());
                frag.show(getFragmentManager(), "Order dialog");
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }



    ArrayList<String> getItems(String arg){
        JSONArray array = null;
        JSONObject obj = null;
        String name =null,type=null;
        Double price =null;
        int qty =0;
        double total = 0;
        ArrayList<String> result = new ArrayList<>();
        try {
            array = new JSONArray(arg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(array != null){
            int length = array.length();
            for(int i=0;i<length;i++){
                obj = null;
                try {
                    obj = array.getJSONObject(i);
                    name = obj.getString("name");
                    type = obj.getString("type");
                    price = obj.getDouble("price");
                    qty = obj.getInt("quantity");
                    total += price*qty;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(obj != null){
                    result.add(name+" * "+qty+" \n ₹"+price*qty);
                }
            }
            result.add("Total       ₹"+total);
        }

        return  result;
    }

}
