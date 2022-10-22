package com.example.stammaskool;

import static android.media.AudioManager.MODE_IN_COMMUNICATION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class DAFActivity extends AppCompatActivity {
    private short[] recordData;
    private AudioRecord inPutAudioRecord;
    private AudioTrack outPutAudioTrack;
    private int bufferSize;
    private boolean isRunning = false;
    AudioManager audioManager;
    private Thread speechThread;
    SeekBar seekBarDelayTime, seekBarEcho, seekBarVolume, seekBarNoise;
    TextView textViewRecorderStatus, textViewDelayTime, textViewEcho, textViewVolume;
    Button buttonStart, buttonStop;
    int delayUnit, echoUnit, noiseUnit;
    int frequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dafactivity);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        initView();
    }

    private void initView() {
        seekBarDelayTime = findViewById(R.id.seekBarDelay);
        seekBarEcho = findViewById(R.id.seekBarEcho);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        seekBarNoise = findViewById(R.id.background_noise_reduction);
        textViewRecorderStatus = findViewById(R.id.textViewRecorderStatus);
        textViewDelayTime = findViewById(R.id.textViewDelay);
        textViewEcho = findViewById(R.id.textViewEcho);
        textViewVolume = findViewById(R.id.textViewVolume);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
        seekBarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        String stringDelay = String.valueOf(seekBarDelayTime.getProgress()) + getString(R.string.time_unit);
        textViewDelayTime.setText(stringDelay);
        textViewEcho.setText(String.valueOf(String.valueOf(seekBarEcho.getProgress())));
        textViewVolume.setText(String.valueOf(seekBarVolume.getProgress()));
        seekBarDelayTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    seekBarDelayTime.setProgress(1);
                String stringDelay = String.valueOf(seekBarDelayTime.getProgress()) + getString(R.string.time_unit);
                textViewDelayTime.setText(stringDelay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarEcho.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    seekBarEcho.setProgress(1);
                textViewEcho.setText(String.valueOf(String.valueOf(seekBarEcho.getProgress())));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewVolume.setText(String.valueOf(seekBarVolume.getProgress()));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
            }
        });
        seekBarNoise.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    seekBarNoise.setProgress(1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void startRecording() {
        if (isRunning) {
           // Toast.makeText(getApplicationContext(), "Already ", Toast.LENGTH_SHORT).show();

            showToast(getString(R.string.status_start));
            return;
        }
        isRunning = true;

        delayUnit = seekBarDelayTime.getProgress();
        echoUnit = seekBarEcho.getProgress();
        noiseUnit = seekBarNoise.getProgress();
        textViewRecorderStatus.setText(getString(R.string.active));

        speechThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startSpeech();
            }
        });
        speechThread.start();
    }

    public void stopRecording() {
        if (!isRunning) {
            showToast(getString(R.string.status_stop));
            return;
        }
        outPutAudioTrack.stop();
        inPutAudioRecord.stop();
        textViewRecorderStatus.setText(getString(R.string.stop));
        speechThread.interrupt();
        isRunning = false;
    }

    private void startSpeech() {
        setup();
        start();
        while (isRunning) {
            inPutAudioRecord.read(recordData, 0, recordData.length);


            long finishTime = System.currentTimeMillis()/**=2343784**/ + delayUnit/**=10**/;
            while (System.currentTimeMillis() < finishTime) ;
            for (int i = 0; i < recordData.length; i++) {
                recordData[i] = (short) Math.min(recordData[i] * echoUnit, Short.MAX_VALUE);
            }
            outPutAudioTrack.write(recordData, 0, recordData.length);
        }
    }

    private void setup() {
        audioManager.setParameters("noise_suppression=auto");
        audioManager.setMode(MODE_IN_COMMUNICATION);
        frequency = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM);
        bufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        bufferSize = bufferSize * noiseUnit;
        recordData = new short[bufferSize];
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        inPutAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC
                , frequency
                , AudioFormat.CHANNEL_IN_STEREO
                , AudioFormat.ENCODING_PCM_16BIT
                , bufferSize);
        if (AutomaticGainControl.isAvailable()) {
            AutomaticGainControl automaticGainControl = AutomaticGainControl.create(
                    inPutAudioRecord.getAudioSessionId()
            );
            automaticGainControl.setEnabled(false);
        }
        outPutAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC
                , frequency
                , AudioFormat.CHANNEL_OUT_STEREO
                , AudioFormat.ENCODING_PCM_16BIT
                , bufferSize
                , AudioTrack.MODE_STREAM);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
    }
    private void start() {
        outPutAudioTrack.setPlaybackRate(frequency);
        inPutAudioRecord.startRecording();
        outPutAudioTrack.play();
    }
}