package com.shabeeb.hashim.gogeekcet;


import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderItem extends Fragment {

    AutoCompleteTextView typeACTV, nameACTV;
    TextView priceTV,itemsTV;
    EditText qtyET;
    Button orderBtn,cartBtn;
    HashMap<String,Float> itemPrice = new HashMap<>();
    String URL = "http://robo-store-cet.herokuapp.com";
    //String URL = "192.168.0.15:8080";

    String type;
    Float price;
    JSONObject orderJSON = new JSONObject();
    JSONArray itemsJSONArray = new JSONArray();
    ProgressDialog ringProgressDialog;
    int total_price;
    int number_of_items = 0,type_loaded =0,name_loaded = 0;
    public OrderItem() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        typeACTV = (AutoCompleteTextView) getActivity().findViewById(R.id.item_type);
        nameACTV = (AutoCompleteTextView) getActivity().findViewById(R.id.item_name);
        priceTV = (TextView) getActivity().findViewById(R.id.priceTV);
        orderBtn = (Button) getActivity().findViewById(R.id.order_btn);
        cartBtn = (Button) getActivity().findViewById(R.id.cart_button);
        qtyET = (EditText) getActivity().findViewById(R.id.qtyET);
        itemsTV = (TextView) getActivity().findViewById(R.id.itemsTV);
        typeACTV.setInputType(InputType.TYPE_NULL);
        nameACTV.setInputType(InputType.TYPE_NULL);
        qtyET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(price !=null && !s.toString().isEmpty())
                    priceTV.setText("Price : "+(int)(price * Integer.parseInt(s.toString())));
                else
                    priceTV.setText("Price : "+price);
            }
        });
        typeACTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                typeACTV.showDropDown();

                return false;
            }
        });
        nameACTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                nameACTV.showDropDown();

                return false;
            }
        });

        typeACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nameACTV.setText("");
                qtyET.setText("");
                type = parent.getItemAtPosition(position).toString();
                ringProgressDialog.show();
                AndroidNetworking.get(URL+"/items/"+type )
                        .setTag(this)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsParsed(new TypeToken<List<Item>>() {
                        }, new ParsedRequestListener<List<Item>>() {
                            @Override
                            public void onResponse(List<Item> items) {
                                String TAG = "Items get response";

                                // do anything with response
                                ArrayList<String> nameList = new ArrayList<String>();

                                for (Item item : items) {

                                    nameList.add(item.name);
                                    itemPrice.put(item.name,item.price);
                                }

                                Collections.sort(nameList);
                                if(getActivity()!=null) {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                            android.R.layout.simple_dropdown_item_1line, nameList);
                                    nameACTV.setAdapter(adapter);
                                }
                                nameACTV.setVisibility(View.VISIBLE);
                                typeACTV.clearFocus();
                                nameACTV.requestFocus();
                                ringProgressDialog.hide();
                                priceTV.setText("Price : "+price);
                            }

                            @Override
                            public void onError(ANError anError) {
                                // handle error
                                anError.printStackTrace();
                            }
                        });
            }
        });
        nameACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(getActivity().getCurrentFocus().getApplicationWindowToken(), 0);
                nameACTV.clearFocus();
                price = itemPrice.get(parent.getItemAtPosition(position).toString());

                priceTV.setText("Price : "+price);
                priceTV.setVisibility(View.VISIBLE);
            }
        });
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeACTV.getText().toString().isEmpty() || nameACTV.getText().toString().isEmpty()||qtyET.getText().toString().isEmpty()){
                    Snackbar.make(getView(), "Fill all fields", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(number_of_items == 0){
                    Snackbar.make(getView(), "Add atleast 1 item to cart", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                else{
                    Bundle bundle = new Bundle();
                    bundle.putString("order",itemsJSONArray.toString());
                    OrderVerifyDialog frag = new OrderVerifyDialog();
                    frag.setTargetFragment(OrderItem.this,0);
                    frag.setArguments(bundle);
                    frag.show(getFragmentManager(), "Order Verify Dialog");
                }
                }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number_of_items == 0)
                    itemsTV.setText("Cart Empty");
                JSONObject temp = new JSONObject();
                if(typeACTV.getText().toString().isEmpty() || nameACTV.getText().toString().isEmpty()||qtyET.getText().toString().isEmpty()){
                    Snackbar.make(getView(), "Fill all fields", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    try {
                        temp.put("type",typeACTV.getText().toString());
                        temp.put("name",nameACTV.getText().toString());
                        temp.put("price",price);
                        temp.put("quantity",Integer.valueOf(qtyET.getText().toString()));
                        itemsJSONArray.put(temp);

                        number_of_items += Integer.parseInt(qtyET.getText().toString());
                        itemsTV.setText("#Cart : "+number_of_items);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Getting data ...", true, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                OrderItem.this.getActivity().getFragmentManager().popBackStack();
            }
        });

        AndroidNetworking.get(URL+"/types")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsParsed(new TypeToken<List<String>>() {
                }, new ParsedRequestListener<List<String>>() {
                    @Override
                    public void onResponse(List<String> types) {
                        String TAG = "Types get response";
                        // do anything with response
                        ArrayList<String> typeList = new ArrayList<String>();

                        for (String type : types) {
                            //typeSet.add(user.type);

                            typeList.add(type);
                        }
                        Collections.sort(typeList);
                        if(getActivity()!=null){
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_dropdown_item_1line, typeList);
                        typeACTV.setAdapter(adapter);}
                        ringProgressDialog.hide();

                    }

                    @Override
                    public void onError(ANError anError) {
                        // handle error
                        anError.printStackTrace();

                        ringProgressDialog.hide();

                    }
                });
    }
    public void onOrderPlaced(boolean placed,JSONObject orderJSON){
        if(placed == true) {
            Toast.makeText(getActivity(), "Order Placed", Toast.LENGTH_LONG).show();
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new MainFragment(), "MainFragment");
            ft.commit();
        }
        else
            Toast.makeText(getActivity(),"Order not Placed",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStop() {
        if(ringProgressDialog != null)
            ringProgressDialog.dismiss();
        super.onStop();
    }
}
