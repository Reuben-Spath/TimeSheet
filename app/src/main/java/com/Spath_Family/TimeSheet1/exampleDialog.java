package com.Spath_Family.TimeSheet1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;


public class exampleDialog extends AppCompatDialogFragment {

    private EditText editTextHistory;
    private ExampleDialogistener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("History")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String history = editTextHistory.getText().toString();
                        listener.applyTexts(history);
                    }
                });

        editTextHistory = view.findViewById(R.id.edit_week_history);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ExampleDialogistener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogistener {
        void applyTexts(String editTextHistory);
    }
}
