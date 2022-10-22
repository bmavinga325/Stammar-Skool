package com.example.stammaskool.Models;

public class StatisticsModel {
   String textData;
   String audioURL;
   String audioDuration;
   String date;
   String time;

   public StatisticsModel() {
   }

   public String getTextData() {
      return textData;
   }

   public void setTextData(String textData) {
      this.textData = textData;
   }

   public String getAudioURL() {
      return audioURL;
   }

   public void setAudioURL(String audioURL) {
      this.audioURL = audioURL;
   }

   public String getAudioDuration() {
      return audioDuration;
   }

   public void setAudioDuration(String audioDuration) {
      this.audioDuration = audioDuration;
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public String getTime() {
      return time;
   }

   public void setTime(String time) {
      this.time = time;
   }
}
