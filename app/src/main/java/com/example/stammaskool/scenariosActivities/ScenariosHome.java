package com.example.stammaskool.scenariosActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stammaskool.R;

public class ScenariosHome extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4;
    PopupMenu popup, popu2, popu3, popup4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenarios);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);

        popup = new PopupMenu(this, tv1);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());

        popu2 = new PopupMenu(this, tv2);
        MenuInflater inflater1 = popu2.getMenuInflater();
        inflater1.inflate(R.menu.menu2, popu2.getMenu());

        popu3 = new PopupMenu(this, tv3);
        MenuInflater inflater2 = popu3.getMenuInflater();
        inflater2.inflate(R.menu.menu3, popu3.getMenu());

        popup4 = new PopupMenu(this, tv4);
        MenuInflater inflater3 = popup4.getMenuInflater();
        inflater3.inflate(R.menu.menu4, popup4.getMenu());

        tv1.setOnClickListener(view -> {
            popup.show();
        });

        tv2.setOnClickListener(view -> {
            popu2.show();
        });

        tv3.setOnClickListener(view -> {
            popu3.show();
        });

        tv4.setOnClickListener(view -> {
            popup4.show();
        });


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.oneonone) {
                    startActivity(new Intent(ScenariosHome.this, JobInterview.class));
                } else {
                    if (menuItem.getItemId() == R.id.panel) {
                        startActivity(new Intent(ScenariosHome.this, PanelInterview.class));
                    }
                }
                return false;
            }
        });

        popu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.general) {
                    startActivity(new Intent(ScenariosHome.this, General.class));
                } else {
                    if (menuItem.getItemId() == R.id.transport) {
                        startActivity(new Intent(ScenariosHome.this, Transport.class));
                    }
                }
                return false;
            }
        });

        popu3.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.emergency) {
                    startActivity(new Intent(ScenariosHome.this, Emergency.class));
                } else {
                    if (menuItem.getItemId() == R.id.doctor) {
                        startActivity(new Intent(ScenariosHome.this, Doctor.class));
                    }
                }
                return false;
            }
        });

        popup4.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.conversations) {
                    startActivity(new Intent(ScenariosHome.this, Conversations.class));
                }

                return false;
            }
        });

    }
}
