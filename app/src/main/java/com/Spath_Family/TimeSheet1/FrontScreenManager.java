package com.Spath_Family.TimeSheet1;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FrontScreenManager extends FragmentActivity {

    private static final String TAG = "FrontScreenManager";

    Calendar calendar = Calendar.getInstance();
    private FirebaseAuth mAuth;
    private TextView week_ending;
    private TextView mng_code;
    private Button send;
    private ImageView logoM;

    private String letter;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> username = new ArrayList<>();
    //    private String onetwothree;
    private Planet[] planets;
    private ArrayAdapter<Planet> listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen_manager);


        mAuth = FirebaseAuth.getInstance();

        logoM = findViewById(R.id.logoM);
        week_ending = findViewById(R.id.tvWeekEnding);
        mng_code = findViewById(R.id.tvmgCode);



        String week_end = "Week Ending: " + weekEnding();
        week_ending.setText(week_end);
        empListner();

    }

    @Override
    protected void onStart() {
        super.onStart();
        logoM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent special_code_pass = new Intent(FrontScreenManager.this, Profile.class);
                special_code_pass.putExtra("comp", true);
                startActivity(special_code_pass);
            }
        });
        employerCounter();
    }

    public void empListner() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            final DocumentReference docRef = db.collection("Company").document(user.getUid());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Toast.makeText(FrontScreenManager.this, "success", Toast.LENGTH_SHORT).show();
                        mng_code.setText(snapshot.getString("empCode"));
                    }
                }
            });
        }
    }

    public String weekEnding() {
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        int days = Calendar.SUNDAY - weekday;
        if (days < 0) {
            // this will usually be the case since Calendar.SUNDAY is the smallest
            days += 7;
        }
        calendar.add(Calendar.DAY_OF_YEAR, days);

        DateFormat df = new SimpleDateFormat("dd-MMM", Locale.getDefault());

        return df.format(calendar.getTime());
    }

    public void employerCounter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final ArrayList<String> names = new ArrayList<>();
        if (currentUser != null) {
            String mUid = currentUser.getUid();

            db.collection("Company").document(mUid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            setLetter(documentSnapshot.getString("empCode"));


                            FirebaseFirestore db1 = FirebaseFirestore.getInstance();

                            db1.collection("Users")
                                    .whereEqualTo("empCode", getLetter())// <-- This line
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (final DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                                    Log.d(TAG, "onComplete: " + document.getString("name"));

                                                    String name = document.getString("name");
                                                    String id = document.getId();

                                                    names.add(name);
                                                    username.add(id);
                                                }
                                                ListView mainListView = findViewById(R.id.mainListView);

                                                // When item is tapped, toggle checked properties of CheckBox and Planet.
                                                mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View item,
                                                                            int position, long id) {
                                                        Planet planet = listAdapter.getItem(position);
                                                        Objects.requireNonNull(planet).toggleChecked();
                                                        PlanetViewHolder viewHolder = (PlanetViewHolder) item.getTag();

                                                        Intent i = new Intent(FrontScreenManager.this, Employee.class);

                                                        String foundername = username.get(position);

                                                        i.putExtra("name", planet.getName());
                                                        i.putExtra("id", foundername);
                                                        startActivity(i);
                                                    }
                                                });

                                                planets = (Planet[]) getLastCustomNonConfigurationInstance();

                                                Planet[] planets = new Planet[names.size()];
                                                for (int i = 0; i < names.size(); i++) {
                                                    planets[i] = new Planet(names.get(i));
                                                }

                                                ArrayList<Planet> planetList = new ArrayList<>(Arrays.asList(planets));

                                                // Set our custom array adapter as the ListView's adapter.
                                                listAdapter = new PlanetArrayAdapter(getApplicationContext(), planetList);
                                                mainListView.setAdapter(listAdapter);

                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return planets;
    }

    /**
     * Holds planet data.
     */
    private static class Planet {
        private String name = "";
        private String id = "";
        private boolean checked = false;

        public Planet() {
        }

        Planet(String name) {
            this.name = name;
        }

        public Planet(String name, boolean checked) {
            this.name = name;
            this.checked = checked;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        boolean isChecked() {
            return checked;
        }

        void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String toString() {
            return name;
        }

        void toggleChecked() {
            checked = !checked;
        }
    }

    /**
     * Holds child views for one row.
     */
    private static class PlanetViewHolder {
        private CheckBox checkBox;
        private TextView textView;

        public PlanetViewHolder() {
        }

        PlanetViewHolder(TextView textView, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.textView = textView;
        }

        CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    /**
     * Custom adapter for displaying an array of Planet objects.
     */
    private static class PlanetArrayAdapter extends ArrayAdapter<Planet> {

        private LayoutInflater inflater;

        PlanetArrayAdapter(Context context, List<Planet> planetList) {
            super(context, R.layout.simplerow, R.id.rowTextView, planetList);
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context);
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Planet to display
            Planet planet = this.getItem(position);

            // The child views in each row.
            CheckBox checkBox;
            TextView textView;

            // Create a new row view
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.simplerow, null);

                // Find the child views.
                textView = convertView.findViewById(R.id.rowTextView);
                checkBox = convertView.findViewById(R.id.CheckBox01);

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag(new PlanetViewHolder(textView, checkBox));

                // If CheckBox is toggled, update the planet it is tagged with.
//                checkBox.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        CheckBox cb = (CheckBox) v;
//                        Planet planet = (Planet) cb.getTag();
//                        planet.setChecked(cb.isChecked());
//                    }
//                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                PlanetViewHolder viewHolder = (PlanetViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox();
                textView = viewHolder.getTextView();
            }

            // Tag the CheckBox with the Planet it is displaying, so that we can
            // access the planet in onClick() when the CheckBox is toggled.
            checkBox.setTag(planet);

            // Display planet data
            if (planet != null) {
                checkBox.setChecked(planet.isChecked());
            }
            if (planet != null) {
                textView.setText(planet.getName());
            }

            return convertView;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            mAuth.signOut();
            Toast.makeText(FrontScreenManager.this, "Signed Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}