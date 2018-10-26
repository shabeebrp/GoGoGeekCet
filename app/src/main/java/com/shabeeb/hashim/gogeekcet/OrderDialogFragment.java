package com.shabeeb.hashim.gogeekcet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shabeeb on 1/2/17.
 */
public class OrderDialogFragment  extends DialogFragment{

    JSONObject orderJSON = new JSONObject();
    JSONArray itemsJSONArray;
    EditText name,phone;
    Spinner year,branch;
    String URL = "http://robo-store-cet.herokuapp.com";
    Context c  ;
    ProgressDialog ringProgressDialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        try {
            itemsJSONArray = new JSONArray(getArguments().getString("order"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setView(inflater.inflate(R.layout.user_details, null)).setTitle("User details");
        builder.setPositiveButton("Order", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        name = (EditText) getDialog().findViewById(R.id.nameET);
                        phone = (EditText) getDialog().findViewById(R.id.phoneET);
                        year = (Spinner) getDialog().findViewById(R.id.spinnerYear);
                        branch = (Spinner) getDialog().findViewById(R.id.spinnerBranch);
                        // FIRE ZE MISSILES!

                        if(name.getText().toString().isEmpty() || phone.getText().toString().isEmpty()){
                            Toast.makeText(((OrderItem)getTargetFragment()).getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
                            OrderDialogFragment frag = new OrderDialogFragment();
                            frag.setTargetFragment(getTargetFragment(),0);
                            frag.setArguments(getArguments());
                            frag.show(getFragmentManager(), "Order dialog");
                        }
                        else{
                            try {
                                orderJSON.put("name",name.getText().toString());
                                orderJSON.put("phone",phone.getText().toString());
                                orderJSON.put("year",year.getSelectedItem().toString());
                                orderJSON.put("branch",branch.getSelectedItem().toString());
                                Log.e("Tag",branch.getSelectedItem().toString());
                                orderJSON.put("order",itemsJSONArray);


                                ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Order is being placed ...", true, true, new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        ringProgressDialog.hide();

                                    }
                                });
                                AndroidNetworking.post(URL+"/order")
                                        .addJSONObjectBody(orderJSON) // posting json
                                        .setTag("test")
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override

                                            public void onResponse(JSONObject response) {
                                                // do anything with response
                                                ringProgressDialog.hide();
                                                ((OrderItem)getTargetFragment()).onOrderPlaced(true,orderJSON);
                                            }
                                            @Override
                                            public void onError(ANError error) {
                                                // handle error

                                                //Toast.makeText(c,"Error , Order not placed",Toast.LENGTH_LONG).show();
                                                ringProgressDialog.hide();
                                                ((OrderItem)getTargetFragment()).onOrderPlaced(false,orderJSON);
                                            }
                                        });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

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
    @Override
    public void onStop() {
        if(ringProgressDialog != null)
            ringProgressDialog.dismiss();
        super.onStop();
    }
}
