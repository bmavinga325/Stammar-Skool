package com.example.stammaskool.DataCalculator;

import com.example.stammaskool.Models.StatisticsModel;

public class WpmCalculator {


   public String getDuration(String time){
      int word=calculateWords(time);
      return  time.trim().replace(":",".");
   }
   public Double calculateWpm(StatisticsModel sm){
      int word=calculateWords(sm.getTextData());
      String timeString=sm.getAudioDuration().trim().replace(":",".");
      Double totalMinutes= Double.valueOf(timeString);
     return word/totalMinutes;
   }

   public int calculateWords(String data){
      String words = data.trim();
      if (words.isEmpty())
         return 0;
      return words.split("\\s+").length;
   }

}
