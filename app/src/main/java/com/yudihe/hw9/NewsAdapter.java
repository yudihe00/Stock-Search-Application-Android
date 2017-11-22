package com.yudihe.hw9;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by heyudi on 11/21/17.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    // item resource id
    private int resourceId;
    public NewsAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<News> objects) {
        super(context,textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        News news = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            // inflate出子项布局，实例化其中的图片控件和文本控件
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();
            // Title
            viewHolder.title = (TextView) view.findViewById(R.id.textViewTitle);
            // Author
            viewHolder.author = (TextView) view.findViewById(R.id.textViewAuthor);
            // Date
            viewHolder.date = (TextView) view.findViewById(R.id.textViewDate);

            // 缓存图片控件和文本控件的实例
            view.setTag(viewHolder);
        }else{
            view = convertView;
            // 取出缓存
            viewHolder = (ViewHolder) view.getTag();
        }

        // 直接使用缓存中的图片控件和文本控件的实例
        // Set title
        viewHolder.title.setText(news.getTitle());
        // Set author
        viewHolder.author.setText(news.getAuthorName());
        // Set date
        viewHolder.date.setText(news.getPubDate());


        return view;
    }
    // 内部类
    class ViewHolder{
        TextView title;
        TextView author;
        TextView date;
    }
}
