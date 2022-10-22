package com.example.stammaskool.Helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.example.stammaskool.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class MiscHelper {
    Context context;

    public MiscHelper(Context context) {
        this.context = context;
    }

    public String getCurrentDate(){
          return  new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
       }
    public String getCurrentTime(){
        return  new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }
    public Dialog openNetLoaderDialog() {
       Dialog dialogP=new Dialog(context);
        dialogP.setContentView(R.layout.dialog_loading);
        dialogP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogP.setCancelable(false);
        dialogP.show();
        return dialogP;
    }

}
