package com.senvisison.senvisiontv.PlayVideo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.senvisison.senvisiontv.Model.ModelVideo;
import com.senvisison.senvisiontv.R;
import com.senvisison.senvisiontv.VideosListActivity;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerVideoActivity extends AppCompatActivity implements View.OnClickListener {
    ExoPlayer player;
    String video_path;
    ExtractorMediaSource mediaSource;
    PlayerView exoPlayer;
    private int currentApiVersion;
    TextView textView_startime, tv_endtime;
    ImageView imageView_prev, image_next, image_fast_rwind, image_fast_forward, image_play, iamge_pause,imageView_back;
    SeekBar seekBar;
    public static int position;
    List<ModelVideo> videoList1 = VideosListActivity.videoList;
    int pos = 0;
    String time;
    long duration;
    long currenttim;
    private Handler mHandler = new Handler();
    LinearLayout linear_lay_control;
    boolean m = false;
    CountDownTimer timer;
    boolean play = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();

        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
        setContentView(R.layout.activity_player_video);
        exoPlayer = findViewById(R.id.videoplay);

        imageView_prev = findViewById(R.id.img_prev);
        image_next = findViewById(R.id.img_next);
        image_fast_rwind = findViewById(R.id.img_fast_rewind);
        image_fast_forward = findViewById(R.id.img_fast_forward);
        image_play = findViewById(R.id.img_play);
        iamge_pause = findViewById(R.id.img_pause);
        textView_startime = findViewById(R.id.tv_starttime);
        tv_endtime = findViewById(R.id.tv_end_time);
        seekBar = findViewById(R.id.seekBar);
        imageView_back = findViewById(R.id.img_back);
        linear_lay_control = findViewById(R.id.linear_lay_control);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        position = intent.getIntExtra("position", 0);
        Log.d("hjkhkj", "khjk" + path);
        initializePlayer(position);

        imageView_prev.setOnClickListener(this);
        image_next.setOnClickListener(this);
        image_fast_rwind.setOnClickListener(this);
        image_fast_forward.setOnClickListener(this);
        image_play.setOnClickListener(this);
        iamge_pause.setOnClickListener(this);
        imageView_back.setOnClickListener(this);


      /*  exoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showcontrols();
            }
        });
*/
        exoPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showcontrols();
                return false;
            }
        });


        /* runnigthread();*/

    }

    private void showcontrols() {
        if (!m) {
            imageView_back.setVisibility(View.VISIBLE);
            linear_lay_control.setVisibility(View.VISIBLE);
            timer();
            m = true;
        } else {
            imageView_back.setVisibility(View.GONE);
            linear_lay_control.setVisibility(View.GONE);
            m = false;
        }
    }

    private void timer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(5000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }

            @SuppressLint("ResourceAsColor")
            public void onFinish() {
                linear_lay_control.setVisibility(View.GONE);
                m = false;
            }
        }.start();
    }


 /*   private void runnigthread() {

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    playnextvid();
                }
            }
        }).start();
    }*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void playnextvid() {

        if (player != null) {
            if (player.getCurrentPosition() == player.getDuration()) {
                if (position == videoList1.size() - 1) {
                    Toast.makeText(PlayerVideoActivity.this, "No more video after it", Toast.LENGTH_SHORT).show();
                } else {
                    if (player != null) {
                        player.release();
                        player.setPlayWhenReady(false);
                        player = null;
                        position++;
                        initializePlayer(position);
                    }
                }
            }
        }
    }


    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializePlayer(int position) {
        /* Data1 data1=videolist.get(0);*/
        /*String videoPath = RawResourceDataSource.buildRawResourceUri(R.raw.mmm).toString();*/
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
/*
        List<ExtractorMediaSource> mediaSources = new ArrayList<>();
        for (int i=0;i<videolist.size();i++){
            Data1 data1=videolist.get(i);
            mediaSources.add(new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(data1.getMediaFileName())));
        }*/


        play = true;
        ModelVideo modelVideo = videoList1.get(position);
        String pathplay = modelVideo.getThumb();

        Uri uri = Uri.parse(pathplay);
        /*Log.d("hdfjhfsjkdfkdsfhshkd",""+uri);*/
        TrackSelector trackSelector = new DefaultTrackSelector();

        DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(32 * 1024, 64 * 1024, 1024, 1024).createDefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        //ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(mediaSources.toArray(new MediaSource[mediaSources.size()]));*/

        mediaSource = new ExtractorMediaSource(
                uri,
                new DefaultDataSourceFactory(this, "MyExoplayer"),
                new DefaultExtractorsFactory(),
                null,
                null
        );

        player.prepare(mediaSource);


        exoPlayer.setPlayer(player);

        exoPlayer.setResizeMode(AspectRatioFrameLayout.FOCUSABLES_TOUCH_MODE);
        player.setPlayWhenReady(true);
        /*duration=player.getDuration();*/

            /*if (pos > 0) {
                player.seekTo(pos);
                player.setPlayWhenReady(true);
            } else {
                player.setPlayWhenReady(true);
            }
*/
        exoPlayer.hideController();

        /*  player.addListener();*/
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {

            }

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        Log.d("okkkll", "buffering");
                        break;
                    case Player.STATE_ENDED:
                        // Activate the force enable
                        playnext();
                        Log.d("okkkmm", "ended");
                        break;
                    case Player.STATE_IDLE:
                        break;
                    case Player.STATE_READY:
                        Log.d("okkkhh", "ended");
                        break;
                    default:
                        // status = PlaybackStatus.IDLE;
                        break;
                }
            }

            @Override
            public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    long du = player.getDuration();
                    time = String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(du),
                            TimeUnit.MILLISECONDS.toMinutes(du) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(du)), // The change is in this line
                            TimeUnit.MILLISECONDS.toSeconds(du) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(du)));

                    tv_endtime.setText(time);
                    loadSeekbar(du);

                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
                /*    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(player!=null && player.getCurrentPosition()<player.getDuration())
                            {
                                seekBar_playvideo.setProgress((int) player.getCurrentPosition());
                                Message msg=new Message();
                                int millis = (int) player.getCurrentPosition();
                                msg.obj=millis/1000;
                                mHandler.sendMessage(msg);
                                try {
                                    Thread.sleep(player.getDuration()/4);
                                    seekBar_playvideo.setProgress((int) player.getCurrentPosition());
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();*/


                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player != null) {
            player.release();
            player.setPlayWhenReady(false);
            player = null;
            finish();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_prev) {
            if (position == 0) {
                Toast.makeText(this, "No more video behind", Toast.LENGTH_SHORT).show();
            } else {
                if (player != null) {
                    player.release();
                    player.setPlayWhenReady(false);
                    player = null;
                    position--;
                    initializePlayer(position);
                }
            }

        } else if (id == R.id.img_next) {
            if (position == videoList1.size() - 1) {
                Toast.makeText(this, "No more video after it", Toast.LENGTH_SHORT).show();
            } else {
                if (player != null) {
                    player.release();
                    player.setPlayWhenReady(false);
                    player = null;
                    position++;
                    initializePlayer(position);
                }
            }
        } else if (id == R.id.img_fast_rewind) {
            if (player.getCurrentPosition() != 0) {
                player.seekTo(player.getCurrentPosition() - 10000);
                seekBar.setProgress((int) (player.getCurrentPosition() - 10000));
            }
        } else if (id == R.id.img_fast_forward) {
            player.seekTo(player.getCurrentPosition() + 10000);
            seekBar.setProgress((int) (player.getCurrentPosition() + 10000));
        } else if (id == R.id.img_pause) {
            if (player != null) {
                player.setPlayWhenReady(false);
                image_play.setVisibility(View.VISIBLE);
                iamge_pause.setVisibility(View.GONE);
            }
        } else if (id == R.id.img_play) {
            if (player != null) {
                player.setPlayWhenReady(true);
                image_play.setVisibility(View.GONE);
                iamge_pause.setVisibility(View.VISIBLE);
            }
        }else if (id==R.id.img_back){
            if (player != null) {
                player.release();
                player.setPlayWhenReady(false);
                player = null;
                finish();
            }
        }

    }


    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            if (player != null) {
                seekBar.setProgress((int) player.getCurrentPosition());
                long dur = player.getCurrentPosition();
                @SuppressLint("DefaultLocale") String time1 = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(dur),
                        TimeUnit.MILLISECONDS.toMinutes(dur) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dur)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(dur) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur)));
                textView_startime.setText(time1);
                mSeekbarUpdateHandler.postDelayed(this, 50);

                /*if (dur==player.getDuration()) {
                    if (position == videoList1.size() - 1) {
                        Toast.makeText(PlayerVideoActivity.this, "No more video after it", Toast.LENGTH_SHORT).show();
                    } else {
                        if (player != null) {
                            player.release();
                            player.setPlayWhenReady(false);
                            player = null;
                            position++;
                            initializePlayer(position);
                        }


                    }
                }*/

            }
        }
    };


    private void loadSeekbar(long time) {
        seekBar.setMax((int) time);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                    long dur = player.getCurrentPosition();
                    @SuppressLint("DefaultLocale") String time1 = String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(dur),
                            TimeUnit.MILLISECONDS.toMinutes(dur) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dur)), // The change is in this line
                            TimeUnit.MILLISECONDS.toSeconds(dur) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur)));

                    textView_startime.setText(time1);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    void playnext() {
        if (position == videoList1.size() - 1) {
            Toast.makeText(PlayerVideoActivity.this, "No more video after it", Toast.LENGTH_SHORT).show();
        } else {
            if (player != null) {
                player.release();
                player.setPlayWhenReady(false);
                player = null;
                position++;
                initializePlayer(position);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        Log.d("kjvhjkvhjsvfs","keycode"+event.getKeyCode()+"   action"+event.getAction());
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            showcontrols();
            /*paypause();*/
        }else if (event.getKeyCode()== KeyEvent.KEYCODE_BACK){
            paypause();
        }
       /* if (event.getAction() == KeyEvent.KEYCODE_MEDIA_NEXT) {

            playnext();

        } else if (event.getAction() == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            if (position == 0) {
                Toast.makeText(this, "No more video behind", Toast.LENGTH_SHORT).show();
            } else {
                if (player != null) {
                    player.release();
                    player.setPlayWhenReady(false);
                    player = null;
                    position--;
                    initializePlayer(position);
                }
            }
        } else if (event.getAction() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {

            if (player != null) {
                if (player.isPlaying()) {
                    player.setPlayWhenReady(false);
                   *//* image_play.setVisibility(View.VISIBLE);
                    iamge_pause.setVisibility(View.GONE); *//*
                } else {
                    player.setPlayWhenReady(true);
                   *//* image_play.setVisibility(View.GONE);
                    iamge_pause.setVisibility(View.VISIBLE); *//*
                }

            }
        }*//*else if (event.getAction() == KeyEvent.KEYCODE_MEDIA_PLAY){
            if (player != null) {
                player.setPlayWhenReady(true);
                image_play.setVisibility(View.GONE);
                iamge_pause.setVisibility(View.VISIBLE);
            }
        }*//* else if (event.getAction() == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            player.seekTo(player.getCurrentPosition() + 10000);
            seekBar.setProgress((int) (player.getCurrentPosition() + 10000));
        } else if (event.getAction() == KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD) {
            if (player.getCurrentPosition() != 0) {
                player.seekTo(player.getCurrentPosition() - 10000);
                seekBar.setProgress((int) (player.getCurrentPosition() - 10000));
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {

            paypause();
        }else if (event.getAction() == KeyEvent.KEYCODE_NAVIGATE_PREVIOUS){
            if (player != null) {
                player.release();
                player.setPlayWhenReady(false);
                player = null;
                finish();
            }
        }*/
        return true;
    }

    void paypause() {
        if (player != null) {
            if (player.isPlaying()) {
                player.setPlayWhenReady(false);
                   /* image_play.setVisibility(View.VISIBLE);
                    iamge_pause.setVisibility(View.GONE); */
            } else {
                player.setPlayWhenReady(true);
                   /* image_play.setVisibility(View.GONE);
                    iamge_pause.setVisibility(View.VISIBLE); */
            }
        }
    }
}