package com.example.stammaskool;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stammaskool.Helper.MiscHelper;
import com.example.stammaskool.Models.StatisticsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class SpeechToText extends AppCompatActivity {

    EditText editTextTranscription;
    TextView editTextOutput;
    Button buttonRecord;
    MediaPlayer mediaPlayer;
    protected static final int RESULT_SPEECH = 1;
    SeekBar seekBar;
    TextView textViewAudioStartTime,textViewEndTime;
    Button btnPlay,buttonAddToDatabase;
    Thread thread,timerThread;
    MiscHelper miscHelper;
    Uri uri=null;

    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference reference;
    int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);
        miscHelper=new MiscHelper(this);
        initDB();
        initUI();

    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(auth.getUid());

    }

    private void initUI() {
        seekBar=findViewById(R.id.seekBar);
        buttonAddToDatabase=findViewById(R.id.buttonAddToDatabase);
        buttonAddToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addDataToFirebase();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        textViewAudioStartTime=findViewById(R.id.textViewAudioStartTime);
        textViewEndTime=findViewById(R.id.textViewEndTime);
        btnPlay=findViewById(R.id.btnPlay);
        editTextTranscription=findViewById(R.id.editTextTranscription);
        editTextOutput=findViewById(R.id.editTextOutput);
        buttonRecord=findViewById(R.id.buttonRecord);
        editTextTranscription.setText(getString(R.string.transcript_example));
        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  startSpeech();
            }
        });
    }

    private void addDataToFirebase() throws FileNotFoundException {
      StatisticsModel sm=new StatisticsModel();
      sm.setAudioDuration(getAudioTime());
      sm.setDate(miscHelper.getCurrentDate());
      sm.setTextData(editTextOutput.getText().toString());
      sm.setTime(miscHelper.getCurrentTime());
      if(uri==null){
          Toast.makeText(this, "No Audio file fond", Toast.LENGTH_SHORT).show();
      }else if(sm.getTextData().equals("")){
          Toast.makeText(this, "no text data found", Toast.LENGTH_SHORT).show();
      }else if(sm.getAudioDuration().equals("")){
          Toast.makeText(this, "audio length not found", Toast.LENGTH_SHORT).show();
      }else {
          uploadAudio(sm);
      }
    }

    Dialog dialogProgress;
    private void uploadAudio(StatisticsModel sm) throws FileNotFoundException {

        Log.d("UriPath",uri.toString());
        Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();

        ContentResolver contentResolver = getContentResolver();
        InputStream filestream = contentResolver.openInputStream(uri);

        dialogProgress=miscHelper.openNetLoaderDialog();
            String  imageID="audioFiles/" + UUID.randomUUID().toString()+".amr";
            StorageReference reference1= FirebaseStorage.getInstance().getReference().child(imageID);
            reference1.putStream(filestream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SpeechToText.this, "Audio uploaded  successfully", Toast.LENGTH_SHORT).show();
                    Task<Uri> uri2=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uri2.isComplete());
                    Uri uri3=uri2.getResult();
                    sm.setAudioURL(uri3.toString());
                     uploadData(sm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SpeechToText.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                     sm.setAudioURL("");
                     uploadData(sm);
                }
            });
    }

    private void uploadData(StatisticsModel sm) {
        reference.child("data").child(miscHelper.getCurrentDate()).push().setValue(sm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SpeechToText.this, "Add Successfully", Toast.LENGTH_SHORT).show();
                        dialogProgress.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SpeechToText.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dialogProgress.dismiss();
            }
        });
    }

    private String getAudioTime() {
        return getTime(time);
    }

    private void startSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.GET_AUDIO", true);
        try {
            startActivityForResult(intent, RESULT_SPEECH);
            editTextOutput.setText("");
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_SPEECH) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                uri = data.getData();

                // TODO: read audio file from inputstream
                playAudio(uri);
                editTextOutput.setText(text.get(0));

            } else {
                editTextOutput.setText("Error");
            }
        }else {
            editTextOutput.setText("Error 2");
        }
    }


    private void playAudio(Uri uri) {
        if(uri==null)
            return;

        mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
        time=mediaPlayer.getDuration();
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
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(SpeechToText.this, "Finish", Toast.LENGTH_SHORT).show();
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