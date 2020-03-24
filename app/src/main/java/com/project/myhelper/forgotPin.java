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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class forgotPin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("FORGOT LOCK PIN");

        Button btn_sumbit=(Button)findViewById(R.id.sumbit_button);
        final EditText editTextLoginPassword=(EditText)findViewById(R.id.input_login_password);
        final EditText editTextnewPin=(EditText)findViewById(R.id.input_new_pin);
        TextView textViewForgotPassword=(TextView)findViewById(R.id.textview_to_forgot_login_password);
        final ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressbar);

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),forgotPasswordActivity.class));
            }
        });

        btn_sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager ConnectionManager = (ConnectivityManager)getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    if (editTextnewPin.getVisibility() == View.GONE) {

                        String loginPassword = editTextLoginPassword.getText().toString();

                        if (loginPassword.isEmpty()) {
                            editTextLoginPassword.setError("Field is empty");
                            editTextLoginPassword.requestFocus();
                            return;
                        }

                        SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
                        String currentLoginPassword = pref.getString("Password", "");

                        if (!loginPassword.equals(currentLoginPassword)) {
                            editTextLoginPassword.setError("Invalid Password");
                            editTextLoginPassword.requestFocus();
                            return;
                        }
                        editTextLoginPassword.setFocusable(false);
                        editTextnewPin.setVisibility(View.VISIBLE);
                    } else {

                        final String newLockPin = editTextnewPin.getText().toString();

                        if (newLockPin.isEmpty()) {
                            editTextnewPin.setError("Field is empty");
                            editTextnewPin.requestFocus();
                            return;
                        }

                        if (newLockPin.length() != 4) {
                            editTextnewPin.setError("length must be 4");
                            editTextnewPin.requestFocus();
                            return;
                        }

                        progressBar.setVisibility(View.VISIBLE);

                        SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
                        String name=pref.getString("Name","");
                        String email=pref.getString("Email","");

                        FirebaseUser currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(currentFirebaseUser.getUid());
                        User user=new User(email,name,newLockPin);

                        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()){
                                    SharedPreferences.Editor ed = getSharedPreferences("UserData", MODE_PRIVATE).edit();
                                    ed.putString("LockPin", newLockPin);
                                    ed.apply();
                                    showCustomDialog();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"not success",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else{
                    Snackbar.make(view,"Internet required",Snackbar.LENGTH_LONG).setAction("action",null).show();
                }
            }
        });
    }

    private void showCustomDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.sucess_dialog, viewGroup, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView textView=dialogView.findViewById(R.id.dialog_textView);
        textView.setText("Lock pin is successfully update");
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
