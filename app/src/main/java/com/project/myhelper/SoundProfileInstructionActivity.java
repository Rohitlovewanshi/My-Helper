package com.project.myhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SoundProfileInstructionActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_profile_instruction);

        txt=findViewById(R.id.textview_for_profile);
        txt.setText("MyHelper <yourLoginPassword> ChangeProfile.");

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Change Sound Profile");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}
