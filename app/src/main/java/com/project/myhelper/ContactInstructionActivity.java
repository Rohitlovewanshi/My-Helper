package com.project.myhelper;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactInstructionActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView imageView;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_instruction);

        txt=findViewById(R.id.textview_for_contact);
        txt.setText("MyHelper <yourLoginPassword> getContact <contactName>.");

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("ACCESS CONTACTS");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        imageView=findViewById(R.id.contact_instruction_image);
    }
}
