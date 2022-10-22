package com.example.stammaskool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stammaskool.Helper.MiscHelper;
import com.example.stammaskool.Models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity  {

    // private TextView register;

    EditText uName, uEmail, uPassword, uConfirmPassword;
    Button registerBtn;

    FirebaseAuth auth;
    FirebaseUser user;

    DatabaseReference reference;
    ProgressBar progressBar;
    SharedPrefs sharedPrefs;
    Dialog dialogEmailSent ;
    MiscHelper miscHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        miscHelper=new MiscHelper(this);
        initDB();
        initUI();


    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference=FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users");
    }

    private void initUI() {
        sharedPrefs = new SharedPrefs(this);
        TextView txt_signup = findViewById(R.id.txt_signup);
        uName = findViewById(R.id.username);
        uEmail = findViewById(R.id.email);
        uPassword = findViewById(R.id.password);
        uConfirmPassword = findViewById(R.id.Confirmpassword);
        dialogEmailSent = new Dialog(this);
        registerBtn = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


    }

    private void registerUser() {
        String fullName = uName.getText().toString().trim();
        String email = uEmail.getText().toString().trim();
        String password = uPassword.getText().toString().trim();
        String cPassword    = uConfirmPassword.getText().toString().trim();
        String image="Default";

        if(fullName.isEmpty()){
            uName.setError("Full name is required!");
            uName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            uEmail.setError("Email is required!");
            uEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            uEmail.setError("Please provide a valid email!");
            uEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            uPassword.setError("Password  is required!");
            uPassword.requestFocus();
            return;
        }
        if(cPassword.isEmpty()){
            uConfirmPassword.setError("Password  is required!");
            uConfirmPassword.requestFocus();
            return;
        }

        Dialog dialogProgress=miscHelper.openNetLoaderDialog();
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                UserModel userModel=new UserModel();
                userModel.setEmail(email);
                userModel.setName(fullName);
                Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                user=auth.getCurrentUser();
                reference.child(user.getUid()).child("profile")
                        .setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startNewTask();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialogProgress.dismiss();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             dialogProgress.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




}

    private void startNewTask() {
        Intent intent=new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}