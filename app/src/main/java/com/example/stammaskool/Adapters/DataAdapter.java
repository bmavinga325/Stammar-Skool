package com.example.stammaskool.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stammaskool.DataCalculator.WpmCalculator;
import com.example.stammaskool.Models.StatisticsModel;
import com.example.stammaskool.R;

import java.util.List;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.CustomViewHolder> {
    List<StatisticsModel> statisticsModels;
    Context context;
    WpmCalculator wpmCalculator;

    private  onItemClickListener mListener;
      public  interface onItemClickListener{
          void  viewDetails(int position);
        }


     public  void setOnItemClickListener(onItemClickListener listener){//item click listener initialization
          mListener=listener;
     }
     public static class  CustomViewHolder extends RecyclerView.ViewHolder{
         TextView textViewTime,textViewLength
                 ,textViewWpm;
         TextView textViewTT,textViewLL,textViewWW;
         ImageView imageViewEmoption;


          public CustomViewHolder(View itemView, final onItemClickListener listener) {
             super(itemView);
              textViewTime=itemView.findViewById(R.id.textViewTime);
              textViewLength=itemView.findViewById(R.id.textViewLength);
              textViewWpm=itemView.findViewById(R.id.textViewWpm);
              textViewTT=itemView.findViewById(R.id.textViewTT);
              textViewLL=itemView.findViewById(R.id.textViewLL);
              textViewWW=itemView.findViewById(R.id.textViewWW);
              imageViewEmoption=itemView.findViewById(R.id.imageViewEmoption);

              itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.viewDetails(position);//call delete interface
                        }
                    }
                }
            });



        }
    }

    // Adapter of constructor
    public DataAdapter(List<StatisticsModel> statisticsModels, Context context) {
        this.statisticsModels =statisticsModels;
        this.context = context;
        wpmCalculator=new WpmCalculator();
    }
    @Override
    public int getItemViewType(int position) {
            return R.layout.statistics_item;
    }
    @Override
    public int getItemCount() {
        return  statisticsModels.size();
    }
    
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false),mListener);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Double wpm=wpmCalculator.calculateWpm(statisticsModels.get(position));
        int intWpm=wpm.intValue();
        holder.textViewTime.setText(statisticsModels.get(position).getTime());
        holder.textViewLength.setText(wpmCalculator.getDuration(statisticsModels.get(position).getAudioDuration()+" min"));
        holder.textViewWpm.setText(intWpm+"");

        if(intWpm>=150){
            holder.imageViewEmoption.setImageDrawable(context.getDrawable(R.drawable.ic_image_five_5));
            holder.textViewTime.setTextColor(context.getResources().getColor(R.color.green));
            holder.textViewLength.setTextColor(context.getResources().getColor(R.color.green));
            holder.textViewWpm.setTextColor(context.getResources().getColor(R.color.green));
            holder.textViewTT.setTextColor(context.getResources().getColor(R.color.green));
            holder.textViewLL.setTextColor(context.getResources().getColor(R.color.green));
            holder.textViewWW.setTextColor(context.getResources().getColor(R.color.green));
        }else if(intWpm>=125 && intWpm<150){
            holder.imageViewEmoption.setImageDrawable(context.getDrawable(R.drawable.ic_image_five_4));
            holder.textViewTime.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.textViewLength.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.textViewWpm.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.textViewTT.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.textViewLL.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.textViewWW.setTextColor(context.getResources().getColor(R.color.yellow));
        }else if(intWpm>80 && intWpm<125){
            holder.imageViewEmoption.setImageDrawable(context.getDrawable(R.drawable.ic_image_five_3));
            holder.textViewTime.setTextColor(context.getResources().getColor(R.color.orange));
            holder.textViewLength.setTextColor(context.getResources().getColor(R.color.orange));
            holder.textViewWpm.setTextColor(context.getResources().getColor(R.color.orange));
            holder.textViewTT.setTextColor(context.getResources().getColor(R.color.orange));
            holder.textViewLL.setTextColor(context.getResources().getColor(R.color.orange));
            holder.textViewWW.setTextColor(context.getResources().getColor(R.color.orange));
        }else if(intWpm<=80){
            holder.imageViewEmoption.setImageDrawable(context.getDrawable(R.drawable.ic_image_five_1));
            holder.textViewTime.setTextColor(context.getResources().getColor(R.color.red));
            holder.textViewLength.setTextColor(context.getResources().getColor(R.color.red));
            holder.textViewWpm.setTextColor(context.getResources().getColor(R.color.red));
            holder.textViewTT.setTextColor(context.getResources().getColor(R.color.red));
            holder.textViewLL.setTextColor(context.getResources().getColor(R.color.red));
            holder.textViewWW.setTextColor(context.getResources().getColor(R.color.red));
        }



      }
}
