package com.smallcard.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditFragment extends Fragment {

    FloatingActionButton editOk;

    EditText editText;

    private EditTextListener editTextListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.edit_fragment,container,false);
        editOk=view.findViewById(R.id.edit_ok);
        editText=view.findViewById(R.id.edit_text);
        editOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getFragmentManager();
                EditFragment editFragment=new EditFragment();
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                editTextListener.sendText(editText);
                transaction.remove(editFragment);
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        editTextListener=(EditTextListener) getActivity();
    }

    public interface EditTextListener{
        public void sendText(EditText editText);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onPause() {
        MainActivity mainActivity=(MainActivity)getActivity();
        mainActivity.add_card.setVisibility(View.VISIBLE);
        super.onPause();
    }
}
