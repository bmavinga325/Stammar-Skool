package com.example.stammaskool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stammaskool.Adapters.DataAdapter;
import com.example.stammaskool.Helper.MiscHelper;
import com.example.stammaskool.Models.StatisticsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ImageView backArrow;
    TextView textViewDate;
    LinearLayout layoutEmpty;
    RecyclerView recyclerView;
    String date="";
    
    List<StatisticsModel> statisticsModels;
    DataAdapter dataAdapter;
    MiscHelper miscHelper;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        date=getIntent().getStringExtra("date");
        miscHelper=new MiscHelper(this);
        initDB();
        initUI();
        initRecyclerView();
        getAllStat();
    }

    private void getAllStat() {
        Dialog progress=miscHelper.openNetLoaderDialog();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                   progress.dismiss();
                   statisticsModels.clear();

                   for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                       statisticsModels.add(dataSnapshot.getValue(StatisticsModel.class));
                   }

                   if(statisticsModels.size()>0){
                       recyclerView.setVisibility(View.VISIBLE);
                       layoutEmpty.setVisibility(View.GONE);
                   }else {
                       recyclerView.setVisibility(View.GONE);
                       layoutEmpty.setVisibility(View.VISIBLE);
                   }

                  dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatisticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                 progress.dismiss();
            }
        });


    }

    private void initRecyclerView() {
        statisticsModels=new ArrayList<>();
        dataAdapter = new DataAdapter(statisticsModels, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(dataAdapter);
        dataAdapter.setOnItemClickListener(new DataAdapter.onItemClickListener() {
            @Override
            public void viewDetails(int position) {

                Intent intent=new Intent(StatisticsActivity.this,StatisticsDetails.class);
                intent.putExtra("text",statisticsModels.get(position).getTextData());
                intent.putExtra("duration",statisticsModels.get(position).getAudioDuration());
                intent.putExtra("time",statisticsModels.get(position).getTime());
                intent.putExtra("url",statisticsModels.get(position).getAudioURL());
                intent.putExtra("date",statisticsModels.get(position).getDate());
                startActivity(intent);
            }
        });


    }

    private void initUI() {
        backArrow=findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textViewDate=findViewById(R.id.textViewDate);
        layoutEmpty=findViewById(R.id.layoutEmpty);
        recyclerView=findViewById(R.id.recyclerView);
        textViewDate.setText(date);

    }

    private void initDB() {
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(firebaseUser.getUid())
                .child("data")
                .child(date.trim());
    }
}