package com.example.stammaskool;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stammaskool.DataCalculator.WpmCalculator;
import com.example.stammaskool.Models.StatisticsModel;

import java.io.IOException;

public class StatisticsDetails extends AppCompatActivity {
    StatisticsModel sm;
    ImageView backArrow,imageViewEmoji;
    TextView textViewSpeekTime,textViewWvp
            ,textViewTextData,
            textViewAudioStartTime,textViewEndTime;
    ImageView[] imageViews=new ImageView[5];
    Button btnPlay;
    WpmCalculator wpmCalculator;
    MediaPlayer mediaPlayer;
    Thread thread,timerThread;
    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_details);
        wpmCalculator=new WpmCalculator();
        getIntentData();
        initUI();
        writeDataONUI();
        playAudio();

    }

    private void writeDataONUI() {
        Double wpm=wpmCalculator.calculateWpm(sm);
        int intWpm=wpm.intValue();
        textViewWvp.setText(intWpm+" wpm");
        textViewSpeekTime.setText(wpmCalculator.getDuration(sm.getAudioDuration())+ " min");
        textViewTextData.setText(sm.getTextData());
        if(intWpm>=150){
            imageViewEmoji.setImageDrawable(getDrawable(R.drawable.ic_image_five_5));
            drawStar(5);
        }else if(intWpm>=125 && intWpm<150){
            imageViewEmoji.setImageDrawable(getDrawable(R.drawable.ic_image_five_4));
            drawStar(4);
        }else if(intWpm>80 && intWpm<125){
            imageViewEmoji.setImageDrawable(getDrawable(R.drawable.ic_image_five_3));
            drawStar(2);
        }else if(intWpm<=80){
            drawStar(1);
            imageViewEmoji.setImageDrawable(getDrawable(R.drawable.ic_image_five_1));
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void drawStar(int star) {
        for(int i=0;i<5;i++){
            imageViews[i].setImageDrawable(getDrawable(R.drawable.ic_empty_star));
        }

        for(int i=0;i<star;i++){
            imageViews[i].setImageDrawable(getDrawable(R.drawable.ic_full_star));
        }



    }

    private void initUI() {
        backArrow=findViewById(R.id.backArrow);
        seekBar=findViewById(R.id.seekBar);
        imageViewEmoji=findViewById(R.id.imageViewEmoji);
        textViewSpeekTime=findViewById(R.id.textViewSpeekTime);
        textViewWvp=findViewById(R.id.textViewWvpm);
        textViewTextData=findViewById(R.id.textViewTextData);
        textViewAudioStartTime=findViewById(R.id.textViewAudioStartTime);
        textViewEndTime=findViewById(R.id.textViewEndTime);
        imageViews[0]=findViewById(R.id.imageViewStar1);
        imageViews[1]=findViewById(R.id.imageViewStar2);
        imageViews[2]=findViewById(R.id.imageViewStar3);
        imageViews[3]=findViewById(R.id.imageViewStar4);
        imageViews[4]=findViewById(R.id.imageViewStar5);
        btnPlay=findViewById(R.id.btnPlay);
    }

    private void getIntentData() {
        sm=new StatisticsModel();
        sm.setTextData(getIntent().getStringExtra("text"));
        sm.setAudioDuration(getIntent().getStringExtra("duration"));
        sm.setTime(getIntent().getStringExtra("time"));
        sm.setAudioURL(getIntent().getStringExtra("url"));
        sm.setDate(getIntent().getStringExtra("date"));
    }

    private void playAudio()  {
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource( sm.getAudioURL());
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        Toast.makeText(this, mediaPlayer.getDuration()+"", Toast.LENGTH_SHORT).show();
        // mediaPlayer.start();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }else {
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });

                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(StatisticsDetails.this, "Finish", Toast.LENGTH_SHORT).show();
            }
        });// creating a variable for medi recorder object class.


        thread=new Thread(){
            @Override
            public void run() {
                int totalDuration=mediaPlayer.getDuration();
                int currentPosition=0;
                while (currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition=mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        };

        thread.start();
        seekBar.setMax(mediaPlayer.getDuration());

        String endTime=getTime(mediaPlayer.getDuration());
        textViewEndTime.setText(endTime);


        timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);// one second sleep
                        textViewAudioStartTime.post(new Runnable() {
                            public void run() {
                                String currentTime=getTime(mediaPlayer.getCurrentPosition());
                                textViewAudioStartTime.setText(currentTime);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        timerThread.start();

    }
    private String getTime(int duration){
        String time="";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        time=time+min+":";
        if(sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }
}