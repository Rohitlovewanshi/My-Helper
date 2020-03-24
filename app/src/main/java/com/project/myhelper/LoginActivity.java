package com.project.myhelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText editTextemail,editTextpassword;
    Button login_btn;
    TextView textViewRegister,textViewForgot;
    ProgressBar progressBar;
    ImageView logo_image;

    private FirebaseAuth mAuth;
    DatabaseReference reference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        logo_image=findViewById(R.id.logo);
        editTextemail=(EditText)findViewById(R.id.input_email);
        editTextpassword=(EditText)findViewById(R.id.input_password);
        login_btn=(Button)findViewById(R.id.btn_login);
        textViewRegister=(TextView)findViewById(R.id.link_register);
        textViewForgot=(TextView)findViewById(R.id.link_forgotPassword);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);

        logo_image.setFocusableInTouchMode(true);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    final String email = editTextemail.getText().toString();
                    final String password = editTextpassword.getText().toString();

                    SharedPreferences.Editor ed=getSharedPreferences("temp",MODE_PRIVATE).edit();

                    if (email.isEmpty()) {
                        editTextemail.setError("Field is required");
                        editTextemail.requestFocus();
                        ed.putBoolean("isError1",true);
                    }

                    if (password.isEmpty()) {
                        editTextpassword.setError("Field is required");
                        editTextpassword.requestFocus();
                        ed.putBoolean("isError1",true);
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        editTextemail.setError("Invalid email");
                        editTextemail.requestFocus();
                        ed.putBoolean("isError1",true);
                    }
                    ed.apply();

                    SharedPreferences pref=getSharedPreferences("temp",MODE_PRIVATE);
                    if(pref.getBoolean("isError1",false)){
                        SharedPreferences.Editor ed1=getSharedPreferences("temp",MODE_PRIVATE).edit();
                        ed1.putBoolean("isError1",false);
                        ed1.apply();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {

                                user = mAuth.getCurrentUser();
                                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String Email = dataSnapshot.child("Email").getValue().toString();
                                        String name=dataSnapshot.child("Name").getValue().toString();
                                        String Pin = dataSnapshot.child("LockPin").getValue().toString();

                                        SharedPreferences.Editor ed = getSharedPreferences("UserData", MODE_PRIVATE).edit();
                                        ed.putString("Name",name);
                                        ed.putString("Email", Email);
                                        ed.putString("Password",password);
                                        ed.putString("LockPin", Pin);
                                        ed.apply();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                SharedPreferences.Editor ed = getSharedPreferences("isFirst", MODE_PRIVATE).edit();
                                ed.putBoolean("LoginActivity", false);
                                ed.apply();

                                Intent intent=new Intent(getApplicationContext(),screenPinActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    editTextpassword.setError("Wrong password");
                                    editTextpassword.requestFocus();
                                } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    editTextemail.setError("Email not registered");
                                    editTextemail.requestFocus();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                else{
                    Snackbar.make(v,"Internet required",Snackbar.LENGTH_LONG).setAction("action",null).show();
                }
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),registrationActivity.class));
            }
        });

        textViewForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),forgotPasswordActivity.class));
            }
        });
    }
}
