package com.example.svenu.svenuitendaal__pset5part2;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends DialogFragment implements View.OnClickListener{
    RestoDatabase db;
    RestoAdapter restoAdapter;
    ListView listView;
    Context theContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        theContext = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        listView = rootView.findViewById(R.id.orderList);

        Button cancelButton = rootView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(this);

        Button placeOrderButton = rootView.findViewById(R.id.button_place_order);
        placeOrderButton.setOnClickListener(this);

        listView.setOnItemClickListener(new GoItemClickListener());
        listView.setOnItemLongClickListener(new GoItemLongClickListener());

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        db = RestoDatabase.getInstance(getActivity().getApplicationContext());
        restoAdapter = new RestoAdapter(getActivity().getApplicationContext(), db.selectAll());

        listView.setAdapter(restoAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_place_order:
                placeOrder(3);
            case R.id.button_cancel:
                closeFragment();
                break;
        }
    }

    private void placeOrder(float totalPrice) {
        String price = "€" + String.format("%.02f", totalPrice);
        orderTime(price);
    }

    private void closeFragment() {
        getDialog().dismiss();
    }

    private void orderTime (final String price) {
        RequestQueue queue = Volley.newRequestQueue(theContext.getApplicationContext());

        // Estimated time
        String url = "https://resto.mprog.nl/order";

        // Request a string response from the provided URL.
        JsonObjectRequest timeRequest = new JsonObjectRequest(
                Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String text = response.getString("preparation_time");
                    getPlaceOrderDialog("Total price: " + price + "\nEstimated order time: " + text + " minutes");
            }
                catch (JSONException exception) {
                    MainActivity.apology("No order time available", theContext);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MainActivity.apology("No internet connection", theContext);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(timeRequest);
    }

    private void getPlaceOrderDialog(String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(theContext);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                db.clear();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class GoItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, final long id) {
            TextView itemNameTextView = view.findViewById(R.id.item_title);
            final String itemTitle = itemNameTextView.getText().toString();

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("Are you sure you want to add " + itemTitle + "?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    db.addItem(id);
//                    db.deleteOneItem(id);
                    MainActivity.apology(itemTitle + " added", theContext);

                    restoAdapter = new RestoAdapter(getActivity().getApplicationContext(), db.selectAll());
                    listView.setAdapter(restoAdapter);
                }
            });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private class GoItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, final long id) {
            Vibrator vibrator = (Vibrator) theContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
            TextView itemNameTextView = view.findViewById(R.id.item_title);
            final String itemTitle = itemNameTextView.getText().toString();

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("Are you sure you want to delete " + itemTitle + "?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    db.delete(id);
                    MainActivity.apology(itemTitle + " deleted", theContext);

                    restoAdapter = new RestoAdapter(getActivity().getApplicationContext(), db.selectAll());
                    listView.setAdapter(restoAdapter);
                }
            });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            return true;
        }
    }
}
