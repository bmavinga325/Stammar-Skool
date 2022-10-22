package com.example.stammaskool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stammaskool.Helper.MiscHelper;
import com.example.stammaskool.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText uEmail, uPassword;
    Button loginButton;
    FirebaseAuth auth;
    FirebaseUser user;
    MiscHelper miscHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        miscHelper=new MiscHelper(this);
        initDB();
        initUI();

    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
    }

    private void initUI() {
        uEmail = findViewById(R.id.email);
        uPassword = findViewById(R.id.password);
        TextView txt_signup = findViewById(R.id.txt_signup);
        loginButton = findViewById(R.id.btn_login);

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

    }


    private void userLogin() {
        String email = uEmail.getText().toString().trim();
        String password = uPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            uEmail.setError("email is Required.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            uPassword.setError("Password is Required.");
            return;
        }
        if (password.length() < 5) { // keep this
            uPassword.setError(" minimum 5 Characters ");
            return;
        }

        Dialog dialogProgress=miscHelper.openNetLoaderDialog();

        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                 startNewTask();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogProgress.dismiss();
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void startNewTask() {
        Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}