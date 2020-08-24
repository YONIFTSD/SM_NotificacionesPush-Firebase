package com.example.notificacionespushfirebase.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notificacionespushfirebase.R;
import com.example.notificacionespushfirebase.model.Noticia;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolderNews> implements View.OnClickListener{

    private ArrayList<Noticia> news;
    private View.OnClickListener listener;
    private OnItemClickListener mListener;

    public Adapter(ArrayList<Noticia> news) {
        this.news = news;
    }

    @NonNull
    @Override
    public Adapter.ViewHolderNews onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_noticia_row, null, false);
        view.setOnClickListener(this);
        return new ViewHolderNews(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolderNews holder, int position) {
        holder.assignData(news.get(position));
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }
    }

    public interface OnItemClickListener {
        void onSendNotificationClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class ViewHolderNews extends RecyclerView.ViewHolder {

        ImageView news_image;
        TextView news_title;
        TextView news_summary;
        Button news_action;

        ViewHolderNews(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            news_image = itemView.findViewById(R.id.news_image);
            news_title = itemView.findViewById(R.id.news_title);
            news_summary = itemView.findViewById(R.id.news_summary);
            news_action = itemView.findViewById(R.id.news_action);

            news_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onSendNotificationClick(position);
                        }
                    }
                }
            });
        }

        private void assignData(Noticia news) {
            Glide.with(news_image.getContext()).load(news.getUrlImage()).into(news_image);
            news_title.setText(news.getTitle());
            news_summary.setText(news.getSummary());
        }
    }
}
