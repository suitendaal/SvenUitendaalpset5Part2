package com.example.svenu.svenuitendaal__pset5part2;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by svenu on 30-11-2017.
 */

public class MenuAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<ItemMenu> itemMenus;

    public MenuAdapter(Context context, ArrayList<ItemMenu> itemMenus) {
        super(context, R.layout.row_menu_item, itemMenus);

        this.context = context;
        this.itemMenus = itemMenus;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.row_menu_item, parent, false);

        ImageView imageView = view.findViewById(R.id.imageView_menu);
        String imageUrl = itemMenus.get(position).getImage();
        imageRequestFunction(imageUrl, imageView);
        imageView.setTag(imageUrl);

        TextView name = view.findViewById(R.id.item_title_menu);
        name.setText(itemMenus.get(position).getName());

        TextView description = view.findViewById(R.id.item_description_menu);
        description.setText(itemMenus.get(position).getDescription());

        TextView price = view.findViewById(R.id.item_price_menu);
        price.setText("€" + String.format("%.02f", itemMenus.get(position).getPrice()));

        return view;
    }

    private void imageRequestFunction(String imageUrl, final ImageView imageView) {
        RequestQueue queue = Volley.newRequestQueue(context);
        // bron: https://www.programcreek.com/javi-api-examples/index.php?api=com.android.volley.toolbox.ImageRequest
        ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        }, 0, 0, null, Bitmap.Config.ALPHA_8,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        queue.add(imageRequest);
    }
}
