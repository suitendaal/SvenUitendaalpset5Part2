package com.example.svenu.svenuitendaal__pset5part2;


import android.os.TransactionTooLargeException;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Queue;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends ListFragment {

    ArrayList<String> categories = new ArrayList<>();
    RequestQueue queue;
    ArrayAdapter theAdapter;
    MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = new MainActivity();
        queue = Volley.newRequestQueue(this.getContext());
        theAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, categories);

        // Get categories.
        String url = "https://resto.mprog.nl/categories";

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray categoriesJSON = response.getJSONArray("categories");
                    for (int i = 0; i < categoriesJSON.length(); i++) {
                        categories.add(categoriesJSON.getString(i));
                    }
                    CategoriesFragment.this.setListAdapter(theAdapter);
                }
                catch (JSONException exception) {
                    mainActivity.apology("That didn't work!");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mainActivity.apology("No internet connection", getContext());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        MenuFragment menuFragment = new MenuFragment();
        String s = String.valueOf(l.getItemAtPosition(position));
        Bundle args = new Bundle();
        args.putString("category", s);
        menuFragment.setArguments(args);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit();
    }
}
