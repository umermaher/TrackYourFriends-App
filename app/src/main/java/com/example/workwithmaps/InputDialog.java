package com.example.workwithmaps;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class InputDialog extends AppCompatDialogFragment {
    EditText userNameText,phoneNoText;
    InputDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater= requireActivity().getLayoutInflater();
        View view =inflater.inflate(R.layout.layout_dialog,null);

        userNameText=view.findViewById(R.id.user_name);
        phoneNoText=view.findViewById(R.id.phone_no);

        builder.setView(view)
                .setTitle("Get User")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(checkInputs()){
                            String userName=userNameText.getText().toString();
                            String phoneNo=phoneNoText.getText().toString();
                            listener.getData(userName,phoneNo);
                        }else {
                            Toast.makeText(getActivity(), "Enter the requirements!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        return builder.create();
    }

    private boolean checkInputs() {
        return !((userNameText.getText().toString().isEmpty()) || phoneNoText.getText().toString().isEmpty());
    }

    public interface InputDialogListener{
        void getData(String userName,String phoneNo);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener=(InputDialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
