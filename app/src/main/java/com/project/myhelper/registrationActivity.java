package com.project.myhelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class registrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    ImageView logo_image;
    EditText editTextname,editTextemail,editTextpassword,editTextconfirmPassword,editTextlockPin;

    private static final Pattern PASSWORD_PATTERN=
            Pattern.compile("^" +
                    "(?=.*[a-z])" + //Any letter
                    "(?=.*[0-9])" + //atleast one digit
                    "(?=.*[@#$%^&+=])" + //atleast one special character
                    "(?=\\S+$)" + //no whitespaces
                    ".{8,}" + //atleast 8 size
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();

        logo_image=findViewById(R.id.logo);
        editTextname=(EditText)findViewById(R.id.input_name);
        editTextemail=(EditText)findViewById(R.id.input_email);
        editTextpassword=(EditText)findViewById(R.id.input_password);
        editTextconfirmPassword=(EditText)findViewById(R.id.input_confirm_password);
        editTextlockPin=(EditText)findViewById(R.id.input_pin_lock);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        Button sumbit_btn=(Button)findViewById(R.id.btn_submit);
        TextView textViewlogin=(TextView)findViewById(R.id.link_login);

        logo_image.setFocusableInTouchMode(true);

        sumbit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    final String name = editTextname.getText().toString();
                    final String email = editTextemail.getText().toString();
                    final String password = editTextpassword.getText().toString();
                    final String confirmPassword=editTextconfirmPassword.getText().toString();
                    final String lockPin = editTextlockPin.getText().toString();

                    SharedPreferences.Editor ed=getSharedPreferences("temp",MODE_PRIVATE).edit();

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        editTextemail.setError("Invalid email");
                        editTextemail.requestFocus();
                        ed.putBoolean("isError2",true);
                    }

                    if (!PASSWORD_PATTERN.matcher(password).matches()) {
                        editTextpassword.setError("Password is weak");
                        editTextpassword.requestFocus();
                        ed.putBoolean("isError2",true);
                    }

                    if(!password.equals(confirmPassword)){
                        editTextpassword.setError("Password not matched");
                        editTextpassword.requestFocus();
                        editTextconfirmPassword.setError("Password not matched");
                        editTextconfirmPassword.requestFocus();
                        ed.putBoolean("isError2",true);
                    }

                    if(editTextlockPin.length()!=4){
                        editTextlockPin.setError("Pin length must be 4");
                        editTextlockPin.requestFocus();
                        ed.putBoolean("isError2",true);
                    }

                    if (name.isEmpty()) {
                        editTextname.setError("Field is empty");
                        editTextname.requestFocus();
                        ed.putBoolean("isError2",true);
                    }

                    if (email.isEmpty()) {
                        editTextemail.setError("Field is empty");
                        editTextemail.requestFocus();
                        ed.putBoolean("isError2",true);
                    }

                    if (password.isEmpty()) {
                        editTextpassword.setError("Field is empty");
                        editTextpassword.requestFocus();
                        ed.putBoolean("isError2",true);
                    }

                    if(confirmPassword.isEmpty()){
                        editTextconfirmPassword.setError("Field is empty");
                        editTextconfirmPassword.requestFocus();
                        ed.putBoolean("isError2",true);
                    }

                    if (lockPin.isEmpty()) {
                        editTextlockPin.setError("Field is empty");
                        editTextlockPin.requestFocus();
                        ed.putBoolean("isError2",true);
                    }

                    ed.apply();

                    SharedPreferences pref=getSharedPreferences("temp",MODE_PRIVATE);
                    if(pref.getBoolean("isError2",false)){
                        SharedPreferences.Editor ed1=getSharedPreferences("temp",MODE_PRIVATE).edit();
                        ed1.putBoolean("isError2",false);
                        ed1.apply();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){

                                User user=new User(
                                        email,
                                        name,
                                        lockPin
                                );
                                progressBar.setVisibility(View.VISIBLE);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(mAuth.getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if(task.isSuccessful()){
                                            showCustomDialog();
                                        }
                                    }
                                });
                            }
                            else{
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    editTextemail.setError("Already registered email");
                                    editTextemail.requestFocus();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                }
                else{
                    Snackbar.make(v,"No Internet",Snackbar.LENGTH_LONG).setAction("action",null).show();
                }
            }
        });

        textViewlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

    }

    private void showCustomDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.sucess_dialog, viewGroup, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView textView=dialogView.findViewById(R.id.dialog_textView);
        textView.setText("Login again to continue !");
        Button btn=dialogView.findViewById(R.id.buttonOk);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
