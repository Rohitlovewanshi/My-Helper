package com.project.myhelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class screenPinActivity extends AppCompatActivity {

    Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    ImageView dot_1, dot_2, dot_3, dot_4, btn_clear,btn_forgot;
    LinearLayout dot_layout;
    TextView txt1;

    private static final int MAX_LENGTH = 4;
    String password1 = "";
    String password2 = "";
    private String codeString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_pin);

        btn0 = (Button) findViewById(R.id.btn0);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);
        btn9 = (Button) findViewById(R.id.btn9);

        btn_forgot = (ImageView) findViewById(R.id.btn_forgot);
        btn_clear = (ImageView) findViewById(R.id.btn_clear);
        dot_1 = (ImageView) findViewById(R.id.dot_1);
        dot_2 = (ImageView) findViewById(R.id.dot_2);
        dot_3 = (ImageView) findViewById(R.id.dot_3);
        dot_4 = (ImageView) findViewById(R.id.dot_4);
        dot_layout = (LinearLayout) findViewById(R.id.dot_layout);

        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),forgotPin.class));
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (codeString.length() > 0) {
                    codeString = removeLastChar(codeString);
                    setDotImagesState();
                }
            }
        });

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(0);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(3);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(4);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(5);
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(6);
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(7);
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(8);
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringCode(9);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode,event);
    }

    @SuppressLint("SetTextI18n")
    private void getStringCode(int buttonId) {
        TextView txt1 = (TextView) findViewById(R.id.txt1);
        switch (buttonId) {
            case 0:
                codeString += "0";
                break;
            case 1:
                codeString += "1";
                break;
            case 2:
                codeString += "2";
                break;
            case 3:
                codeString += "3";
                break;
            case 4:
                codeString += "4";
                break;
            case 5:
                codeString += "5";
                break;
            case 6:
                codeString += "6";
                break;
            case 7:
                codeString += "7";
                break;
            case 8:
                codeString += "8";
                break;
            case 9:
                codeString += "9";
                break;
            default:
                break;
        }
        setDotImagesState();
        if (codeString.length() == MAX_LENGTH) {

            SharedPreferences pref=getSharedPreferences("UserData",MODE_PRIVATE);
            if(codeString.equals(pref.getString("LockPin",""))){
                SharedPreferences pref1=getSharedPreferences("isFirst",MODE_PRIVATE);
                if(pref1.getBoolean("setupActivity",true)){
                    Intent intent=new Intent(getApplicationContext(),setupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
            else {
                Toast.makeText(this,"Wrong pin",Toast.LENGTH_SHORT).show();
                shakeAnimation();
            }
        }
        else if(codeString.length() > MAX_LENGTH){
            codeString="";
            getStringCode(buttonId);
        }
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        dot_layout.startAnimation(shake);
    }

    private void setDotImagesState() {
        switch (codeString.length()) {
            case 1:
                dot_1.setImageResource(R.drawable.dot_enable);
                break;
            case 2:
                dot_1.setImageResource(R.drawable.dot_enable);
                dot_2.setImageResource(R.drawable.dot_enable);
                break;
            case 3:
                dot_1.setImageResource(R.drawable.dot_enable);
                dot_2.setImageResource(R.drawable.dot_enable);
                dot_3.setImageResource(R.drawable.dot_enable);
                break;
            case 4:
                dot_1.setImageResource(R.drawable.dot_enable);
                dot_2.setImageResource(R.drawable.dot_enable);
                dot_3.setImageResource(R.drawable.dot_enable);
                dot_4.setImageResource(R.drawable.dot_enable);
                break;
            default:
                break;
        }
        if (codeString.length() < 4) {
            switch (codeString.length()) {
                case 3:
                    dot_4.setImageResource(R.drawable.dot_disable);
                    break;
                case 2:
                    dot_3.setImageResource(R.drawable.dot_disable);
                    dot_4.setImageResource(R.drawable.dot_disable);
                    break;
                case 1:
                    dot_2.setImageResource(R.drawable.dot_disable);
                    dot_3.setImageResource(R.drawable.dot_disable);
                    dot_4.setImageResource(R.drawable.dot_disable);
                    break;
                case 0:
                    dot_1.setImageResource(R.drawable.dot_disable);
                    dot_2.setImageResource(R.drawable.dot_disable);
                    dot_3.setImageResource(R.drawable.dot_disable);
                    dot_4.setImageResource(R.drawable.dot_disable);
                default:
                    break;
            }
        }
    }

    private String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length() - 1);
    }
}
