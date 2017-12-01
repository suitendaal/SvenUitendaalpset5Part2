package com.example.svenu.svenuitendaal__pset5part2;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends ListFragment {

    ArrayList<ItemMenu> items = new ArrayList<>();
    RequestQueue queue;
    MenuAdapter theAdapter;
    Context theContext;
    RestoDatabase db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = this.getArguments();
        final String category = arguments.getString("category");

        theContext = getActivity();
        queue = Volley.newRequestQueue(this.getContext());
        theAdapter = new MenuAdapter(getActivity().getApplicationContext(), items);
        db = RestoDatabase.getInstance(theContext);

        // Get categories.
        String url = "https://resto.mprog.nl/menu";

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray menuItemsJSON = response.getJSONArray("items");
                    int n = menuItemsJSON.length();
                    for (int i = 0; i < n; i+=1) {
                        JSONObject categoriesJSON = menuItemsJSON.getJSONObject(i);
                        String categoryName = categoriesJSON.getString("category");
                        if (categoryName.equals(category)) {
                            String name = categoriesJSON.getString("name");
                            float price = Float.valueOf(categoriesJSON.getString("price"));
                            String description = categoriesJSON.getString("description");
                            String image = categoriesJSON.getString("image_url");

                            ItemMenu itemMenu = new ItemMenu(name, description, price, image);
                            items.add(itemMenu);
                        }
                    }
                    MenuFragment.this.setListAdapter(theAdapter);
                }
                catch (JSONException exception) {
                    MainActivity.apology("That didn't work!", theContext);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MainActivity.apology("No internet connection", theContext);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView itemTextView = v.findViewById(R.id.item_title_menu);
        TextView priceTextView = v.findViewById(R.id.item_price_menu);
        ImageView imageView = v.findViewById(R.id.imageView_menu);

        final String item = itemTextView.getText().toString();
        String priceString = String.valueOf(priceTextView.getText().toString().replace("€", "")).replace(",", ".");
        final String image = imageView.getTag().toString();

        final float price = Float.valueOf(priceString);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Do you want to add " + item + "?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                boolean result = db.addItem(item, price, image, 1);
                if (result) {
                    MainActivity.apology(item + " ordered!", theContext);
                }
                else {
                    MainActivity.apology("Something went wrong", theContext);
                }

                CategoriesFragment categoriesFragment = new CategoriesFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, categoriesFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setNeutralButton("Add more",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addMore(item, price, image);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void addMore(final String item, final float price, final String image) {
        RelativeLayout linearLayout = new RelativeLayout(theContext);
        final NumberPicker aNumberPicker = new NumberPicker(theContext);
        aNumberPicker.setMaxValue(50);
        aNumberPicker.setMinValue(1);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker,numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(theContext);
        alertDialogBuilder.setTitle("How many?");
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                int amount = aNumberPicker.getValue();
                                db.addItem(item, price, image, amount);
                                MainActivity.apology(amount + " " + item + " added", theContext);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
