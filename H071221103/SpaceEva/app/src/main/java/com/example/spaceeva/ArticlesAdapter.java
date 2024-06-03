package com.example.spaceeva;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spaceeva.activity.WebViewActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticlesViewHolder> {
    private List<Articles> articlesList;
    private Context context;

    public ArticlesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticlesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticlesViewHolder holder, int position) {
        Articles article = articlesList.get(position);
        holder.title.setText(article.getTitle());
        String formattedDate = formatDate(article.getPublishedAt());
        holder.articleLink.setText(article.getUrl());
        holder.publishAt.setText(formattedDate);
        holder.newsSite.setText(article.getNewsSite());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", article.getUrl());
                context.startActivity(intent);
            }
        });
        if (article.getImageUrl() != null) {
            Glide.with(context).load(article.getImageUrl()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return articlesList != null ? articlesList.size() : 0;
    }

    public static class ArticlesViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView publishAt,newsSite,articleLink;
        ImageView imageView;

        public ArticlesViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_title);
            imageView = itemView.findViewById(R.id.article_image);
            publishAt = itemView.findViewById(R.id.article_publishAt);
            newsSite = itemView.findViewById(R.id.article_newsSite);
            articleLink = itemView.findViewById(R.id.article_Link);
        }
    }
    private String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Jika parsing gagal, kembalikan string kosong atau lakukan penanganan kesalahan yang sesuai
        }
    }

    public void setArticles(List<Articles> articlesList) {
        this.articlesList = articlesList;
        notifyDataSetChanged();
    }
}

