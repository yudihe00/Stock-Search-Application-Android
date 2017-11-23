package com.yudihe.hw9;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by heyudi on 11/22/17.
 */

public class FavAdapter extends ArrayAdapter<FavoriteSymbol> {

    // item resource id
    private int resourceId;

    public FavAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<FavoriteSymbol> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FavoriteSymbol fav = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            // inflate出子项布局，实例化其中的图片控件和文本控件
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new FavAdapter.ViewHolder();
            // Title
            viewHolder.symbol = (TextView) view.findViewById(R.id.favSymbol);
            // Author
            viewHolder.price = (TextView) view.findViewById(R.id.favPrice);
            // Date
            viewHolder.changeString = (TextView) view.findViewById(R.id.favChange);

            // 缓存图片控件和文本控件的实例
            view.setTag(viewHolder);
        } else{
            view = convertView;
            // 取出缓存
            viewHolder = (ViewHolder) view.getTag();
        }

        // 直接使用缓存中的图片控件和文本控件的实例
        // Set title
        viewHolder.symbol.setText(fav.getSymbolName());
        // Set author
        viewHolder.price.setText(fav.getPrice());
        // Set date
        viewHolder.changeString.setText(fav.getChangeString());
        if(fav.getChangeFloat()>=0){
            viewHolder.changeString.setTextColor(Color.GREEN);
        } else {
            viewHolder.changeString.setTextColor(Color.RED);
        }

        return view;
    }

    // Internal class
    class ViewHolder {
        TextView symbol;
        TextView price;
        TextView changeString;
    }
}
