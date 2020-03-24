package com.project.myhelper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;


public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS=1;

    private static final Pattern PASSWORD_PATTERN=
            Pattern.compile("^" +
                    "(?=.*[a-z])" + //Any letter
                    "(?=.*[0-9])" + //atleast one digit
                    "(?=.*[@#$%^&+=])" + //atleast one special character
                    "(?=\\S+$)" + //no whitespaces
                    ".{8,}" + //atleast 8 size
                    "$");

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("SETTINGS");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings,new SettingsFragment())
                .commit();
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this,MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, final String rootKey) {
            setPreferencesFromResource(R.xml.app_preferences, rootKey);



            Preference myPref=findPreference("changeName");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    final AlertDialog dialogBuilder=new AlertDialog.Builder(getContext()).create();
                    LayoutInflater inflater=getLayoutInflater();
                    View dialogView= inflater.inflate(R.layout.dialog_box_for_change_name,null);
                    final EditText editText=(EditText)dialogView.findViewById(R.id.edit_name);
                    Button buttonChange=(Button)dialogView.findViewById(R.id.buttonChange);
                    Button buttonCancel=(Button)dialogView.findViewById(R.id.buttonCancel);
                    final ProgressBar progressBar=(ProgressBar)dialogView.findViewById(R.id.progressbar);

                    buttonChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ConnectivityManager ConnectionManager = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {

                                final String name=editText.getText().toString();

                                if (name.length() == 0) {
                                    editText.setError("Field is empty");
                                    editText.requestFocus();
                                    return;
                                }

                                progressBar.setVisibility(View.VISIBLE);

                                SharedPreferences pref=getActivity().getSharedPreferences("UserData",MODE_PRIVATE);
                                String email=pref.getString("Email","");
                                String lockPin=pref.getString("LockPin","");

                                FirebaseUser currentFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference mDatabase;
                                mDatabase=FirebaseDatabase.getInstance().getReference("Users");
                                User user=new User(email,name,lockPin);
                                mDatabase.child(currentFirebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if(task.isSuccessful()){

                                            SharedPreferences.Editor ed = getActivity().getSharedPreferences("UserData", MODE_PRIVATE).edit();
                                            ed.putString("Name", name);
                                            ed.apply();
                                            showCustomDialog(getActivity(),"Profile name is successfully updated");
                                            dialogBuilder.dismiss();

                                        }
                                        else{
                                            Toast.makeText(getContext(),"Unsuccess", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Snackbar.make(v,"Internet required",Snackbar.LENGTH_LONG).setAction("action",null).show();
                            }
                        }
                    });

                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    });
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.show();
                    return true;
                }
            });

            Preference myPref1=findPreference("changeLoginPassword");
            myPref1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final AlertDialog dialogBuilder=new AlertDialog.Builder(getContext()).create();
                    LayoutInflater inflater=getLayoutInflater();
                    View dialogView= inflater.inflate(R.layout.dialog_box_for_changeloginpassword,null);
                    final EditText editCurrentPassword=(EditText)dialogView.findViewById(R.id.edit_current_password);
                    final EditText editNewPassword=(EditText)dialogView.findViewById(R.id.edit_new_password);
                    final EditText editConfirmPassword=(EditText)dialogView.findViewById(R.id.edit_confirm_new_password);
                    Button buttonChange=(Button)dialogView.findViewById(R.id.buttonChange);
                    Button buttonCancel=(Button)dialogView.findViewById(R.id.buttonCancel);
                    final ProgressBar progressBar=(ProgressBar)dialogView.findViewById(R.id.progressbar);

                    final FirebaseAuth mAuth=FirebaseAuth.getInstance();

                    buttonChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ConnectivityManager ConnectionManager = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                String currentPassword = editCurrentPassword.getText().toString();
                                final String newPassword = editNewPassword.getText().toString();
                                String confirmPassword = editConfirmPassword.getText().toString();

                                if (currentPassword.length() == 0) {
                                    editCurrentPassword.setError("Field is empty");
                                    editCurrentPassword.requestFocus();
                                    return;
                                }
                                if (newPassword.length() == 0) {
                                    editNewPassword.setError("Field is empty");
                                    editNewPassword.requestFocus();
                                    return;
                                }
                                if (confirmPassword.length() == 0) {
                                    editConfirmPassword.setError("Field is empty");
                                    editConfirmPassword.requestFocus();
                                    return;
                                }
                                if (!newPassword.equals(confirmPassword)) {
                                    editNewPassword.setError("Password does not matched");
                                    editNewPassword.requestFocus();
                                    editConfirmPassword.setError("Password does not match");
                                    editConfirmPassword.requestFocus();
                                    return;
                                }
                                SharedPreferences pref = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
                                String email = pref.getString("Email", "");
                                String password = pref.getString("Password", "");

                                if (!currentPassword.equals(password)) {
                                    editCurrentPassword.setError("Password does not match");
                                    editCurrentPassword.requestFocus();
                                    return;
                                }
                                if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                                    editNewPassword.setError("Password is weak");
                                    editNewPassword.requestFocus();
                                    return;
                                }

                                progressBar.setVisibility(View.VISIBLE);

                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (task.isSuccessful()) {
                                                        dialogBuilder.dismiss();
                                                        showCustomDialog(getActivity(),"Login password is successfully updated");
                                                    } else {
                                                        Toast.makeText(getContext(), "Not Success", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Snackbar.make(v,"Internet required",Snackbar.LENGTH_LONG).setAction("action",null).show();
                            }
                        }
                    });

                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    });
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.show();
                    return true;
                }
            });

            Preference myPref2=findPreference("changeLockPin");
            myPref2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final AlertDialog dialogBuilder=new AlertDialog.Builder(getContext()).create();
                    LayoutInflater inflater=getLayoutInflater();
                    View dialogView= inflater.inflate(R.layout.dialog_box_for_changelockpin,null);
                    final EditText editCurrentPin=(EditText)dialogView.findViewById(R.id.edit_current_pin);
                    final EditText editNewPin=(EditText)dialogView.findViewById(R.id.edit_new_pin);
                    final EditText editConfirmPin=(EditText)dialogView.findViewById(R.id.edit_confirm_new_pin);
                    Button buttonChange=(Button)dialogView.findViewById(R.id.buttonChange);
                    Button buttonCancel=(Button)dialogView.findViewById(R.id.buttonCancel);
                    final ProgressBar progressBar=(ProgressBar)dialogView.findViewById(R.id.progressbar);

                    final FirebaseAuth mAuth=FirebaseAuth.getInstance();

                    buttonChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ConnectivityManager ConnectionManager = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {

                                String currentPin=editCurrentPin.getText().toString();
                                final String newPin=editNewPin.getText().toString();
                                String confirmPin=editConfirmPin.getText().toString();

                                if(currentPin.length()==0) {
                                    editCurrentPin.setError("Field is empty");
                                    editCurrentPin.requestFocus();
                                    return;
                                }

                                if(newPin.length()==0) {
                                    editNewPin.setError("Field is empty");
                                    editNewPin.requestFocus();
                                    return;
                                }

                                if(confirmPin.length()==0) {
                                    editConfirmPin.setError("Field is empty");
                                    editConfirmPin.requestFocus();
                                    return;
                                }

                                if(newPin.length()!=4) {
                                    editNewPin.setError("length should be 4");
                                    editNewPin.requestFocus();
                                    return;
                                }

                                if(!newPin.equals(confirmPin)) {
                                    editNewPin.setError("Lock pin not matched");
                                    editNewPin.requestFocus();
                                    editConfirmPin.setError("Lock pin not matched");
                                    editConfirmPin.requestFocus();
                                    return;
                                }

                                SharedPreferences pref = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
                                String email = pref.getString("Email", "");
                                String name=pref.getString("Name","");

                                if(!currentPin.equals(pref.getString("LockPin",""))) {
                                    editCurrentPin.setError("Invalid pin");
                                    editCurrentPin.requestFocus();
                                    return;
                                }

                                progressBar.setVisibility(View.VISIBLE);

                                FirebaseUser currentFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(currentFirebaseUser.getUid());
                                User user=new User(email,name,newPin);

                                reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()){
                                            SharedPreferences.Editor ed = getActivity().getSharedPreferences("UserData", MODE_PRIVATE).edit();
                                            ed.putString("LockPin", newPin);
                                            ed.apply();
                                            dialogBuilder.dismiss();
                                            showCustomDialog(getActivity(),"Lock pin is successfully updated");
                                        }
                                        else{
                                            Toast.makeText(getContext(),"not success",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Snackbar.make(v,"Internet required",Snackbar.LENGTH_LONG).setAction("action",null).show();
                            }
                        }
                    });

                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    });
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.show();
                    return true;
                }
            });

            final SwitchPreference pref_contact=(SwitchPreference)findPreference("access_contact");
            final SwitchPreference pref_location=(SwitchPreference)findPreference("access_location");
            final SwitchPreference pref_profile=(SwitchPreference)findPreference("access_profile");
            final SwitchPreference pref_alarm=(SwitchPreference)findPreference("access_alarm");
            final SwitchPreference pref_lock=(SwitchPreference)findPreference("access_lock");

            String permission= Manifest.permission.SEND_SMS;
            int grant= ContextCompat.checkSelfPermission(getContext(),permission);
            if(grant != PackageManager.PERMISSION_GRANTED){
                pref_contact.setChecked(false);
                pref_location.setChecked(false);
                pref_profile.setChecked(false);
                pref_alarm.setChecked(false);
                pref_lock.setChecked(false);
            }


            permission= Manifest.permission.READ_CONTACTS;
            grant= ContextCompat.checkSelfPermission(getContext(),permission);
            if(grant != PackageManager.PERMISSION_GRANTED){
                pref_contact.setChecked(false);
            }


            permission= Manifest.permission.ACCESS_FINE_LOCATION;
            grant= ContextCompat.checkSelfPermission(getContext(),permission);
            if(grant != PackageManager.PERMISSION_GRANTED){
                pref_location.setChecked(false);
            }


            SharedPreferences pref=getActivity().getSharedPreferences("Admin",MODE_PRIVATE);
            if(!pref.getBoolean("enabled",false)){
                pref_lock.setChecked(false);
            }

            pref_contact.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(!pref_contact.isChecked()){
                        String permission_sms= Manifest.permission.SEND_SMS;
                        int grant_sms= ContextCompat.checkSelfPermission(getContext(),permission_sms);
                        String permission_contact= Manifest.permission.READ_CONTACTS;
                        int grant_contact= ContextCompat.checkSelfPermission(getContext(),permission_contact);

                        if(grant_sms!=PackageManager.PERMISSION_GRANTED && grant_contact!=PackageManager.PERMISSION_GRANTED){
                            String[] permission_list=new String[2];
                            permission_list[0]=permission_sms;
                            permission_list[1]=permission_contact;
                            ActivityCompat.requestPermissions(getActivity(),permission_list,REQUEST_ID_MULTIPLE_PERMISSIONS);
                        }
                        else if(grant_sms!= PackageManager.PERMISSION_GRANTED){
                            String[] permission_list=new String[1];
                            permission_list[0]=permission_sms;
                            ActivityCompat.requestPermissions(getActivity(),permission_list,REQUEST_ID_MULTIPLE_PERMISSIONS);
                        }
                        else if(grant_contact!=PackageManager.PERMISSION_GRANTED){

                            String[] permission_list=new String[1];
                            permission_list[0]=permission_contact;
                            ActivityCompat.requestPermissions(getActivity(),permission_list,REQUEST_ID_MULTIPLE_PERMISSIONS);
                        }
                        pref_contact.setChecked(true);
                    }
                    else{
                        pref_contact.setChecked(false);
                    }
                    return true;
                }
            });

            pref_location.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(!pref_location.isChecked()) {
                        String permission_sms= Manifest.permission.SEND_SMS;
                        int grant_sms= ContextCompat.checkSelfPermission(getContext(),permission_sms);
                        String permission_location= Manifest.permission.ACCESS_FINE_LOCATION;
                        int grant_location= ContextCompat.checkSelfPermission(getContext(),permission_location);

                        if(grant_sms!=PackageManager.PERMISSION_GRANTED && grant_location!=PackageManager.PERMISSION_GRANTED){
                            String[] permission_list=new String[2];
                            permission_list[0]=permission_sms;
                            permission_list[1]=permission_location;
                            ActivityCompat.requestPermissions(getActivity(),permission_list,REQUEST_ID_MULTIPLE_PERMISSIONS);
                        }
                        else if(grant_sms!= PackageManager.PERMISSION_GRANTED){
                            String[] permission_list=new String[1];
                            permission_list[0]=permission_sms;
                            ActivityCompat.requestPermissions(getActivity(),permission_list,REQUEST_ID_MULTIPLE_PERMISSIONS);
                        }
                        else if(grant_location!=PackageManager.PERMISSION_GRANTED){

                            String[] permission_list=new String[1];
                            permission_list[0]=permission_location;
                            ActivityCompat.requestPermissions(getActivity(),permission_list,REQUEST_ID_MULTIPLE_PERMISSIONS);
                        }
                        pref_location.setChecked(true);
                    }
                    else {
                        pref_location.setChecked(false);
                    }
                    return true;
                }
            });

            pref_profile.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(!pref_profile.isChecked()) {
                        String permission_sms= Manifest.permission.SEND_SMS;
                        int grant_sms= ContextCompat.checkSelfPermission(getContext(),permission_sms);

                        if(grant_sms!= PackageManager.PERMISSION_GRANTED){
                            String[] permission_list=new String[1];
                            permission_list[0]=permission_sms;
                            ActivityCompat.requestPermissions(getActivity(),permission_list,REQUEST_ID_MULTIPLE_PERMISSIONS);
                        }
                        else{
                            pref_profile.setChecked(true);
                        }
                    }
                    else {
                        pref_profile.setChecked(false);
                    }
                    return true;
                }
            });

            pref_alarm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(!pref_alarm.isChecked()) {
                        String permission_sms= Manifest.permission.SEND_SMS;
                        int grant_sms= ContextCompat.checkSelfPermission(getContext(),permission_sms);

                        if(grant_sms!= PackageManager.PERMISSION_GRANTED){
                            String[] permission_list=new String[1];
                            permission_list[0]=permission_sms;
                            ActivityCompat.requestPermissions(getActivity(),permission_list,REQUEST_ID_MULTIPLE_PERMISSIONS);
                        }
                        else{
                            pref_alarm.setChecked(true);
                        }
                    }
                    else {
                        pref_alarm.setChecked(false);
                    }
                    return true;
                }
            });

            pref_lock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(!pref_lock.isChecked()) {
                        String permission_sms= Manifest.permission.SEND_SMS;
                        int grant_sms= ContextCompat.checkSelfPermission(getContext(),permission_sms);

                        SharedPreferences pref=getActivity().getSharedPreferences("Admin",MODE_PRIVATE);

                        if(grant_sms!= PackageManager.PERMISSION_GRANTED){
                            String[] permission_list=new String[1];
                            permission_list[0]=permission_sms;
                            ActivityCompat.requestPermissions(getActivity(),permission_list,REQUEST_ID_MULTIPLE_PERMISSIONS);
                        }
                        else if(!pref.getBoolean("enabled",false)){
                            DevicePolicyManager devicePolicyManager;
                            ComponentName compName;
                            devicePolicyManager=(DevicePolicyManager)getActivity().getSystemService(DEVICE_POLICY_SERVICE);
                            compName=new ComponentName(getContext(),MyAdmin.class);
                            Intent intent=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,compName);
                            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Additional text explaining why we need this permission");
                            startActivity(intent);
                        }
                        else{
                            pref_lock.setChecked(true);
                        }
                    }
                    else {
                        pref_lock.setChecked(false);
                    }
                    return true;
                }
            });
        }
    }
    private static void showCustomDialog(final Activity activity, String text) {
        final ViewGroup viewGroup = activity.findViewById(android.R.id.content);

        final View dialogView = LayoutInflater.from(activity).inflate(R.layout.sucess_dialog, viewGroup, false);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);

        TextView textView=dialogView.findViewById(R.id.dialog_textView);
        textView.setText(text);
        Button btn=dialogView.findViewById(R.id.buttonOk);

        builder.setView(dialogView);
        final android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}
