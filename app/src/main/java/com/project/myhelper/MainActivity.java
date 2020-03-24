package com.project.myhelper;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DevicePolicyManager devicePolicyManager;
    public ComponentName compName;

    Button buttonCancel,buttonAdd;
    EditText editTextNumber,editTextNumberCode;
    AlertDialog dialogBuilder;
    View dialogView;

    public DatabaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb=new DatabaseHelper(this);

        dialogBuilder=new AlertDialog.Builder(this).create();
        LayoutInflater inflater=this.getLayoutInflater();
        dialogView= inflater.inflate(R.layout.dialog_box_for_block_contacts,null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        editTextNumber=(EditText)dialogView.findViewById(R.id.edit_number);
        editTextNumberCode=(EditText)dialogView.findViewById(R.id.edit_number_code);
        buttonAdd=(Button)dialogView.findViewById(R.id.buttonAdd);
        buttonCancel=(Button)dialogView.findViewById(R.id.buttonCancel);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView(0);
        TextView navHeaderName=headerView.findViewById(R.id.nav_header_name);
        TextView navHeaderEmail=headerView.findViewById(R.id.nav_header_email);

        SharedPreferences pref=getSharedPreferences("UserData",MODE_PRIVATE);
        navHeaderName.setText(pref.getString("Name",""));
        navHeaderEmail.setText(pref.getString("Email",""));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment=null;
        fragment=new InstructionActivity();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame,fragment);
        ft.commit();
    }

    boolean doubleBackToExitPressesOnce=false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(doubleBackToExitPressesOnce) {
                super.onBackPressed();
                finishAffinity();
                return;
            }

            this.doubleBackToExitPressesOnce=true;
            Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressesOnce=false;
                }
            },2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    MenuItem addContact,deleteAllMessage;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        addContact=menu.findItem(R.id.action_add_contact);
        deleteAllMessage=menu.findItem(R.id.action_delete_all_messages);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_add_contact){

            editTextNumber.getText().clear();

           buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            });

           buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number=editTextNumber.getText().toString();
                    String numberCode=editTextNumberCode.getText().toString();
                    if(number.isEmpty()){
                        editTextNumber.setError("Field is empty");
                        editTextNumber.requestFocus();
                        return;
                    }
                    if(numberCode.isEmpty()){
                        editTextNumberCode.setError("Field is empty");
                        editTextNumberCode.requestFocus();
                        return;
                    }
                    if(number.length()!=10){
                        editTextNumber.setError("Invalid number");
                        editTextNumber.requestFocus();
                        return;
                    }
                    if(numberCode.length()!=3){
                        editTextNumberCode.setError("Invalid field");
                        editTextNumberCode.requestFocus();
                        return;
                    }
                    boolean result=mydb.insertIntoBlockedContacts(numberCode+number);
                    if(!result){
                        editTextNumber.setError("Number already exists");
                        editTextNumber.requestFocus();
                        return;
                    }
                    dialogBuilder.dismiss();
                    Fragment frg=null;
                    frg=new BlockedContactsListViewActivity();
                    FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame,frg);
                    ft.commit();

                }
           });

           dialogBuilder.setView(dialogView);
           dialogBuilder.show();
           return true;
        }
        else if (id == R.id.action_delete_all_messages) {
            final android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("Confirmation");
            builder.setMessage("Delete all history");
            builder.setCancelable(false);
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mydb.deleteAllMessage();
                    Fragment frg=null;
                    frg=new MessageListViewActivity();
                    FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame,frg);
                    ft.commit();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment=null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            addContact.setVisible(false);
            deleteAllMessage.setVisible(false);
            fragment=new InstructionActivity();

        } else if (id == R.id.nav_blockedContacts) {

            deleteAllMessage.setVisible(false);
            addContact.setVisible(true);
            fragment = new BlockedContactsListViewActivity();

        }else if(id == R.id.nav_history){

            deleteAllMessage.setVisible(true);
            addContact.setVisible(false);
            fragment=new MessageListViewActivity();

        } else if (id == R.id.nav_setting) {

            startActivity(new Intent(this,SettingsActivity.class));

        } else if (id == R.id.nav_setup_permission) {

            startActivity(new Intent(this,setupActivity.class));

        } else if (id == R.id.nav_uninstall) {
            devicePolicyManager=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
            compName=new ComponentName(this,MyAdmin.class);
            devicePolicyManager.removeActiveAdmin(compName);
            Intent intent=new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:com.project.myhelper"));
            startActivity(intent);

        } else if (id == R.id.nav_share) {

            Intent  intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBodyText="Your shearing message goes here";
            intent.putExtra(Intent.EXTRA_SUBJECT,"Subject/Title");
            intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
            startActivity(Intent.createChooser(intent,"Choose sharing method"));

        } else if (id == R.id.nav_feedback) {

            startActivity(new Intent(getApplicationContext(),feedbackActivity.class));

        } else if (id == R.id.nav_about) {

        }

        if(fragment!=null){
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        dialogBuilder.dismiss();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dialogBuilder.dismiss();
        super.onDestroy();
    }
}
