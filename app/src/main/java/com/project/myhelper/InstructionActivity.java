package com.project.myhelper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InstructionActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_instruction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("INSTRUCTIONS");

        CardView cardView_contact=(CardView)getActivity().findViewById(R.id.cardview_contact);
        CardView cardView_location=(CardView)getActivity().findViewById(R.id.cardview_location);
        CardView cardView_sound_profile=(CardView)getActivity().findViewById(R.id.cardview_sound_profile);
        CardView cardView_alarm=(CardView)getActivity().findViewById(R.id.cardview_alarm);
        CardView cardView_lock_mobile_screen=(CardView)getActivity().findViewById(R.id.cardview_lock_mobile_screen);
        CardView cardView_additional_help=(CardView)getActivity().findViewById(R.id.cardview_additional_help);

        cardView_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ContactInstructionActivity.class));
            }
        });

        cardView_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),LocationInstructionActivity.class));
            }
        });

        cardView_sound_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SoundProfileInstructionActivity.class));
            }
        });

        cardView_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AlarmInstructionActivity.class));
            }
        });

        cardView_lock_mobile_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),MobileLockScreenInstructionActivity.class));
            }
        });

        cardView_additional_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AdditionalHelpInstructionActivity.class));
            }
        });
    }
}
