package com.Spath_Family.TimeSheet1;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import android.util.Log;
//import com.Spath_Family.TimeSheet1.R;

public class Confirmation extends FrontScreenEmployee {

    private static final String TAG = "Confirmation";
    //    private String pass;
    private float long_pass;

    private Button cancel;
    private Button confirm;

    private CheckBox lunch;

    private TextView hours;
    private TextView signInTime;
    private TextView signOffTime;

    private boolean hadLunch = false;

    private FirebaseAuth mAuth;

//    public String getPass() {
//        return pass;
//    }
//
//    public void setPass(String pass) {
//        this.pass = pass;
//    }

    public boolean getHadLunch() {
        return hadLunch;
    }

    public void setHadLunch(boolean hadLunch) {
        this.hadLunch = hadLunch;
    }

    public float getLong_pass() {
        return long_pass;
    }

    public void setLong_pass(float long_pass) {
        this.long_pass = long_pass;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        cancel = findViewById(R.id.btCancel);
        confirm = findViewById(R.id.btConfirm);

        hours = findViewById(R.id.tvHours);
        signInTime = findViewById(R.id.tvSignInTime);
        signOffTime = findViewById(R.id.tvSignOffTime);

        mAuth = FirebaseAuth.getInstance();

        hours.setText(change_lunch());

        checkbox();
        init_Confirmation();
//        start_finish();
    }


    public String change_lunch() {
        String outcome = String.valueOf(timeworked(getHadLunch(), getLunch(), getStart_time(), getFinish_time()));
        return outcome;
    }


    public void checkbox() {
//        setHadLunch(false);

//        setPass(hours_worked_without_lunch());

//        setLong_pass(getTimeWorked());
        lunch = findViewById(R.id.cbLunch);
//        if (getTimeWorked() < 1) {
//            lunch.setEnabled(false);
//        }
        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lunch.isChecked()) {
                    setHadLunch(true);
                    hours.setText(change_lunch());
//                    setPass(hours_worked_with_lunch());
//                    setLong_pass(getTimeWorkedWLunch());

                } else {
                    setHadLunch(false);
                    hours.setText(change_lunch());
//                    setPass(hours_worked_without_lunch());
//                    setLong_pass(getTimeWorked());
                }
            }
        });
    }

    public void fullTimeSave() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            String mUid = mAuth.getCurrentUser().getUid();

//            NoteEmployee noteEmployee = new NoteEmployee(getStart_time(),getFinish_time(),getHadLunch());

//            noteEmployee.setStart(getStart_time());
//            noteEmployee.setFinish(getFinish_time());

//            noteEmployee.setStartH(getStartHr());
//            noteEmployee.setStartM(getStartMin());
//
//            noteEmployee.setFinishH(getFinishHr());
//            noteEmployee.setFinishM(getFinishMin());
//
//            noteEmployee.setH4t(getHours4today());
//            noteEmployee.setM4t(getMinutes4today());


//            db.collection("Users").document(mUid).collection(history_maker()).document(asWeek())
//                    .set(noteEmployee, SetOptions.merge())
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.w(TAG, "onSuccess: ");
//                        }
////                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("Failure to add", e.toString());

//                        }
//                    });
        }
    }

//    public void start_finish() {
//        signInTime.setText(getSignedIn());
//        signOffTime.setText(getSignedOff());
//    }

    public void init_Confirmation() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fullTimeSave();
                finish();
                Intent confirm_pass = new Intent(Confirmation.this, EmployeeWeek.class);
                startActivity(confirm_pass);

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
