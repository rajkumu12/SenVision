package com.senvisison.senvisiontv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.senvisison.senvisiontv.Adapters.VideoListAdapterRecyclerview;
import com.senvisison.senvisiontv.Model.ModelVideo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.com.tvrecyclerview.TvRecyclerView;

public class VideosListActivity extends AppCompatActivity {

   public static List<ModelVideo>videoList;
   TvRecyclerView recyclerView_list;
    int data,folder,id,thumb,title,duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_videos_list);


        checkPermissions();
        recyclerView_list=findViewById(R.id.tv_recycler_view);

        loadVideos();

        addShourcut();
    }

    private void addShourcut(){
        Intent shortCutIntent = new Intent(getApplicationContext() ,VideosListActivity.class);

        shortCutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();

        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT , shortCutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME , "Convertor");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE ,
                Intent.ShortcutIconResource.fromContext(getApplicationContext() , R.mipmap.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate" , false);
        getApplicationContext().sendBroadcast(addIntent);

    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    5);

            return;
        }


    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                loadVideos();
            } else {
                // User refused to grant permission.
            }
        }
    }

    void loadVideos()
    {
        videoList=new ArrayList<>();
        Cursor cursor;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media._ID,MediaStore.Video.Thumbnails.DATA,MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION};
        final String orderBy = MediaStore.Video.Media.TITLE;


        cursor = getApplicationContext().getContentResolver().query(uri, projection,null,null, orderBy);


        id      = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thumb   = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
        title   = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
        duration= cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        while(cursor.moveToNext()) {

            long length = Long.parseLong(cursor.getString(duration)) / 1000;
            long minutes = length / 60;
            long seconds = length % 60;
            ModelVideo modelVideo=new ModelVideo();
            modelVideo.setThumb(cursor.getString(thumb));
            modelVideo.setVideo(cursor.getString(title));
            modelVideo.setVideoUrl(String.valueOf(ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)));
            /*items.add(new VideoItem(,,cursor.getString(id),properFormat(minutes)+":"+properFormat(seconds)));*/
            videoList.add(modelVideo);
        }

        VideoListAdapterRecyclerview videoListAdapterRecyclerview = new VideoListAdapterRecyclerview(VideosListActivity.this, videoList);
        GridLayoutManager layoutManager2 = new GridLayoutManager(VideosListActivity.this, 4);
        recyclerView_list.setLayoutManager(layoutManager2);
                          /* int spacingInPixels = Objects.requireNonNull(getContext()).getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));*/
        recyclerView_list.setItemAnimator(new DefaultItemAnimator());
        recyclerView_list.setAdapter(videoListAdapterRecyclerview);
        recyclerView_list.setOnItemStateListener(new TvRecyclerView.OnItemStateListener() {
            @Override
            public void onItemViewClick(View view, int position) {
                Log.i("lolllllllll", "you click item position: " + position);
            }

            @Override
            public void onItemViewFocusChanged(boolean gainFocus, View view, int position) {
            }
        });

        recyclerView_list.setSelectPadding(8, 8, 8, 8);
       /* adapter.notifyDataSetChanged();*/
        cursor.close();



        /*videoList=new ArrayList<>();
        Cursor cursor;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media._ID,MediaStore.Video.Thumbnails.DATA,MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION};
        final String orderBy = MediaStore.Video.Media.TITLE;
        int data,folder,id,thumb,title,duration;

        cursor = getApplicationContext().getContentResolver().query(uri, projection,null,null, orderBy);

        id      = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thumb   = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
        title   = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
        duration= cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        while(cursor.moveToNext()) {
            long length = Long.parseLong(cursor.getString(duration)) / 1000;
            long minutes = length / 60;
            long seconds = length % 60;

            videoList.add(new ModelVideo(cursor.getString(title)));
            Log.d("vdhshkd","hjgh"+cursor.getString(title));
           *//* items.add(new VideoItem(cursor.getString(title),cursor.getString(thumb),cursor.getString(id),properFormat(minutes)+":"+properFormat(seconds)));*//*

        }

       *//* adapter.notifyDataSetChanged();*//*
        cursor.close();*/


        /*List<Video> videoList = new ArrayList<Video>();*/

      /*  Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.MEDIA_SCANNER_VOLUME);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MIME_TYPE,
        };
        String selection = MediaStore.Video.Media.DURATION +
                " >= ?";
        String[] selectionArgs = new String[] {
                String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))};
        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                Log.d("gdhaskjGJASGJ","df"+name);
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                videoList=new ArrayList<>();
                videoList.add(new ModelVideo(name));


                Toast.makeText(this, "list size"+videoList.size()+"  "+name, Toast.LENGTH_SHORT).show();
                // Stores column values and the contentUri in a local object
                // that represents the media file.
                *//*videoList.add(new Video(contentUri, name, duration, size));*//*
            }
            VideoListAdapterRecyclerview videoListAdapterRecyclerview = new VideoListAdapterRecyclerview(VideosListActivity.this, videoList);
            GridLayoutManager layoutManager2 = new GridLayoutManager(VideosListActivity.this, 4);
            recyclerView_list.setLayoutManager(layoutManager2);
                            *//*  int spacingInPixels = Objects.requireNonNull(getContext()).getResources().getDimensionPixelSize(R.dimen.spacing);
                                recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));*//*
            recyclerView_list.setItemAnimator(new DefaultItemAnimator());
            recyclerView_list.setAdapter(videoListAdapterRecyclerview);
        }*/


    }

    /*private void loadVideo() {

        loadlist();

    }*/

    /*private void loadlist() {
        videoList=new ArrayList<>();
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));
        videoList.add(new ModelVideo("sjdhfjhdfh"));


        VideoListAdapterRecyclerview videoListAdapterRecyclerview = new VideoListAdapterRecyclerview(VideosListActivity.this, videoList);
        GridLayoutManager layoutManager2 = new GridLayoutManager(VideosListActivity.this, 4);
        recyclerView_list.setLayoutManager(layoutManager2);
                            *//*  int spacingInPixels = Objects.requireNonNull(getContext()).getResources().getDimensionPixelSize(R.dimen.spacing);
                                recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));*//*
        recyclerView_list.setItemAnimator(new DefaultItemAnimator());
        recyclerView_list.setAdapter(videoListAdapterRecyclerview);

    }*/
}