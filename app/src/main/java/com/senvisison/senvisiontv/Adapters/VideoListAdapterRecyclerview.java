 package com.senvisison.senvisiontv.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.senvisison.senvisiontv.Model.ModelVideo;
import com.senvisison.senvisiontv.PlayVideo.PlayerVideoActivity;
import com.senvisison.senvisiontv.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoListAdapterRecyclerview  extends RecyclerView.Adapter<VideoListAdapterRecyclerview.ViewHolder> {

    private Context context;
    private List<ModelVideo> arrayList;
    int selected_position = 0;
    public VideoListAdapterRecyclerview(Context context, List<ModelVideo> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public VideoListAdapterRecyclerview.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_items_view, parent, false);
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        return new VideoListAdapterRecyclerview.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoListAdapterRecyclerview.ViewHolder holder, final int position) {
                ModelVideo modelVideo=arrayList.get(position);
                holder.tv_moviename.setText(modelVideo.getVideo());
        Log.d("jhfksfjkhsdahjs",""+modelVideo.getThumb());
                /*holder.img_poster.setImageDrawable(Drawable.createFromPath(modelVideo.getThumb()));*/

        Glide.with(context).asBitmap()
                .load(modelVideo.getThumb()) // or URI/path
                .into(holder.img_poster); //imageview to set thumbnail to


        holder.img_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PlayerVideoActivity.class);
                intent.putExtra("path",modelVideo.getThumb());
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_poster;
        TextView tv_moviename;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_poster=itemView.findViewById(R.id.img_banner);
            tv_moviename=itemView.findViewById(R.id.text_name);


        }
    }
}