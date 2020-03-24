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
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class feedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("FEEDBACK");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        Button button_send=findViewById(R.id.button_send);
        final EditText editText=findViewById(R.id.edit_feedback);
        final ProgressBar progressBar=findViewById(R.id.progressbar);

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    String text=editText.getText().toString();
                    if(text.isEmpty()){
                        editText.setError("Field is empty");
                        editText.requestFocus();
                        return;
                    }
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateAndTime=sdf.format(new Date());

                    SharedPreferences pref=getSharedPreferences("UserData",MODE_PRIVATE);
                    String email=pref.getString("Email","");

                    progressBar.setVisibility(View.VISIBLE);

                    Feedback_values values=new Feedback_values(
                            email,
                            text
                    );

                    FirebaseDatabase.getInstance().getReference("Feedbacks")
                            .child(currentDateAndTime)
                            .setValue(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                showCustomDialog();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

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
        textView.setText("Feedback send successfully");
        Button btn=dialogView.findViewById(R.id.buttonOk);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
