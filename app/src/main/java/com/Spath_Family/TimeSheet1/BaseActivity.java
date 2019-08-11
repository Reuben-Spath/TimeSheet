package com.Spath_Family.TimeSheet1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

//import android.content.Context;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if(user!=null){
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            final DocumentReference docRef = db.collection("Company").document(user.getUid());
//            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                    @Nullable FirebaseFirestoreException e) {
//                    if (e != null) {
//                        return;
//                    }
//                    if (snapshot != null && snapshot.exists()) {
//                        snapshot.getBoolean("company");
//                        if( snapshot.getBoolean("company")!=null){
//                            Intent intent = new Intent(BaseActivity.this,FrontScreenManager.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            finish();
//                            startActivity(intent);
//                            Toast.makeText(BaseActivity.this, "Welcome Back!", Toast.LENGTH_SHORT).show();
//
//                        }
//                    } else {
//                        Intent intent = new Intent(BaseActivity.this,FrontScreenEmployee.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        finish();
//                        startActivity(intent);
//                        Toast.makeText(BaseActivity.this, "Welcome Back!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//        }else{
//            Intent i = new Intent(this, MainActivity .class);
//            startActivity(i);
//        }
    }

    public void goToFrontScreen() {
        Intent i = new Intent(this, FrontScreenEmployee.class);
        startActivity(i);
    }

    public void goToFrontScreenManager() {
        Intent i = new Intent(this, FrontScreenManager.class);
        startActivity(i);
    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

//    public void hideKeyboard(View view) {
//        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
