package com.project.myhelper;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class forgotPasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressBar progressBar;
    EditText editTextEmail;
    Button submitBtn;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        editTextEmail=(EditText)findViewById(R.id.input_email);
        submitBtn=(Button)findViewById(R.id.btn_sumbit);

        toolbar.setTitle("FORGOT PASSWORD");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        firebaseAuth=FirebaseAuth.getInstance();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=editTextEmail.getText().toString();

                if(email.isEmpty()){
                    editTextEmail.setError("Field is required");
                    editTextEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Invalid email");
                    editTextEmail.requestFocus();
                    return;
                }

                ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.sendPasswordResetEmail(editTextEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "password link send to your email", Toast.LENGTH_SHORT).show();
                            } else {
                                if(task.getException() instanceof FirebaseAuthInvalidUserException){
                                    editTextEmail.setError("Email not registered");
                                    editTextEmail.requestFocus();
                                    return;
                                }
                            }
                        }
                    });
                }
                else {
                    Snackbar.make(v,"Internet required",Snackbar.LENGTH_LONG).setAction("action",null).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this,LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
