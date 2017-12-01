package com.example.svenu.svenuitendaal__pset5part2;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;


/**
 * Created by svenu on 29-11-2017.
 */

public class RestoAdapter extends ResourceCursorAdapter {
    public RestoAdapter(Context context, Cursor cursor){
        super(context, R.layout.row_order_item, cursor);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView itemTitle = view.findViewById(R.id.item_title);
        TextView itemPrice = view.findViewById(R.id.item_price);
        TextView itemAmount = view.findViewById(R.id.item_amount);

        String title = cursor.getString(cursor.getColumnIndex(RestoDatabase.COL2));
        float price = cursor.getFloat(cursor.getColumnIndex(RestoDatabase.COL3));
        int amount = cursor.getInt(cursor.getColumnIndex(RestoDatabase.COL4));
        String imageUrl = cursor.getString(cursor.getColumnIndex(RestoDatabase.COL5));;

        String priceString = "€" + String.format("%.02f", amount * price);

        itemTitle.setText(title);
        itemPrice.setText(priceString);
        itemAmount.setText("" + amount);

        imageRequestFunction(imageUrl, imageView, context);
    }

    private void imageRequestFunction(String imageUrl, final ImageView imageView, Context context) {
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
