package com.example.facedetectionwrinkle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.zain.android.internetconnectivitylibrary.ConnectionUtil;

import org.threeten.bp.Duration;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AfterLogInActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyRecyclerViewAdapter recyclerViewAdapter;
    private SubjectsRepository subjectsRepository;
    List<Subjects> subList;
    String user;
    Button addSubBtn;
    Button dialogButton;
    RadioGroup radioGroup;
    RadioButton maleBtn;
    SpinKitView mySKV;
    RadioButton femaleBtn;
    ConnectionUtil connectionUtil;
    androidx.appcompat.widget.Toolbar toolbar;
    int totCount;
    boolean isSelf;
    int mycount;
    public static final String SHARED_PREF = "com.example.facedetectionwrinkle";


    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (exit) {
            finish(); // finish activity
        } else {
//            Toast.makeText(this, "Press Back again to Exit.",
//                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_log_in);
        AndroidThreeTen.init(this);
        connectionUtil = new ConnectionUtil(AfterLogInActivity.this);

        toolbar =  findViewById(R.id.toolbar);
//        toolbar.inflateMenu(R.menu.main_menu);
//
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId() == R.id.developerBTN) {
//                    Intent intent = new Intent(AfterLogInActivity.this, DevPage.class);
//                    startActivity(intent);
//                } else if (item.getItemId() == R.id.supervisorBTN) {
//                    Intent intent = new Intent(AfterLogInActivity.this, SupervisorActivity.class);
//                    startActivity(intent);
//                }
//                return true;
//            }
//        });


        mySKV = findViewById(R.id.spin_kit);

        addSubBtn = (Button) findViewById(R.id.addSub_button);
        addSubBtn.setVisibility(View.GONE);

        SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        user = mPrefs.getString("user", "");
        isSelf = mPrefs.getBoolean("is_self", false);

        subjectsRepository = subjectsRepository.getInstance();

        recyclerView  = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        addSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Subjects mysub = new Subjects(user, "abc123", 1, ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toString(), 13, 'M');
//                int insrtIndex = subList.size();
//                subList.add(insrtIndex, mysub);
//                recyclerViewAdapter.notifyItemInserted(insrtIndex);

                SharedPreferences mPrefs2 = getSharedPreferences(SHARED_PREF, MODE_PRIVATE); //add key
                mycount = mPrefs2.getInt("sub_count", 0);
                if (!connectionUtil.isOnline()) {
                    Toast.makeText(AfterLogInActivity.this, "Please connect your device to internet.", Toast.LENGTH_SHORT).show();
                }
//                else if (!isSelf && mycount != 0) {
//                    Toast.makeText(AfterLogInActivity.this, "You can not add more Subjects.", Toast.LENGTH_SHORT).show();
//                }
                else if (totCount >=15) {
                    Toast.makeText(AfterLogInActivity.this, "Maximum Subject Capacity Reached.", Toast.LENGTH_SHORT).show();
                }
                else {

                    showAlertDialog(R.layout.dialog_layout);
                }



            }
        });



        subjectsRepository.getSubjectsService().getRelatedSubjects(user).enqueue(new Callback<List<Subjects>>() {
            @Override
            public void onResponse(Call<List<Subjects>> call, Response<List<Subjects>> response) {
                if (response.isSuccessful()) {

                    subList = response.body();
                    mySKV.setVisibility(View.GONE);
                    totCount = subList.size();
                    if (subList.size() == 0) {
                        SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        prefsEditor.putBoolean("is_self", true).apply();
                        isSelf = true;

                    }
                    recyclerViewAdapter = new MyRecyclerViewAdapter(AfterLogInActivity.this, subList);

                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            SharedPreferences mPrefs2 = getSharedPreferences(SHARED_PREF, MODE_PRIVATE); //add key
                            boolean accessPos = mPrefs2.getBoolean("can_access"+position, false);
                            if (!connectionUtil.isOnline()) {
                                Toast.makeText(AfterLogInActivity.this, "Please connect your device to internet.", Toast.LENGTH_SHORT).show();
                            }
//                            else if (!accessPos) {
//                                Toast.makeText(AfterLogInActivity.this, "You do not have permission to edit this subject.", Toast.LENGTH_SHORT).show();
//                            }
                            else {
                                SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                prefsEditor.putString("subject", user+"_"+(position+1)).apply();
                                prefsEditor.putInt("selSub_count", recyclerViewAdapter.getItem(position).getCount()).apply();
                                //Toast.makeText(AfterLogInActivity.this, "Capture.", Toast.LENGTH_SHORT).show();

                                if (recyclerViewAdapter.getItem(position).getCount() == 5) {
                                    ZonedDateTime currDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
                                    ZonedDateTime lastUpdateTime = ZonedDateTime.parse(recyclerViewAdapter.getItem(position).getLastUpdate());
                                    Duration diff = Duration.between(lastUpdateTime, currDate);
                                    if (diff.toMinutes() < 720) {
                                        Toast.makeText(AfterLogInActivity.this, "Please come back after 12 hours for Session 2.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(AfterLogInActivity.this, CameraXLivePreviewActivity.class);
                                        startActivity(intent);
                                    }
                                    //Toast.makeText(AfterLogInActivity.this, "Dur: " + diff.toMinutes(), Toast.LENGTH_SHORT).show();

                                }

                                else if (recyclerViewAdapter.getItem(position).getCount() >= 10) {
                                    Toast.makeText(AfterLogInActivity.this, "Uploading is complete for selected subject.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(AfterLogInActivity.this, CameraXLivePreviewActivity.class);
                                    startActivity(intent);
                                }


                            }
                            //Log.d("click901", String.valueOf(position));
                            //Toast.makeText(AfterLogInActivity.this, "Clicked: " + recyclerViewAdapter.getItem(position).getSubjectCode(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    addSubBtn.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(AfterLogInActivity.this, "Make sure you are connected to the internet", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(AfterLogInActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Subjects>> call, Throwable t) {
                Toast.makeText(AfterLogInActivity.this, "Make sure you are connected to the internet", Toast.LENGTH_SHORT).show();
                //Toast.makeText(AfterLogInActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }
    private void showAlertDialog(int layout) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View layoutView = getLayoutInflater().inflate(layout, null);
        EditText subNameEdit = layoutView.findViewById(R.id.subNameEditText);
        EditText subAgeEdit = layoutView.findViewById(R.id.subAgeEditText);
        radioGroup = layoutView.findViewById(R.id.radioGroup);
        maleBtn = layoutView.findViewById(R.id.radioMale);
        femaleBtn = layoutView.findViewById(R.id.radioFemale);
        dialogButton = layoutView.findViewById(R.id.btnDialog);
        dialogBuilder.setView(layoutView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (subNameEdit.getText().toString().trim().equalsIgnoreCase("")) {
                    subNameEdit.setError("This Field cannot be blank.");
                } else if (subAgeEdit.getText().toString().trim().equalsIgnoreCase("")) {
                    subAgeEdit.setError("This Field cannot be blank.");
                } else if ((!maleBtn.isChecked()) && (!femaleBtn.isChecked())) {
                    Toast.makeText(AfterLogInActivity.this, "Please Select Your Gender.", Toast.LENGTH_SHORT).show();
                }
                else if (maleBtn.isChecked()) {
                    //Toast.makeText(AfterLogInActivity.this, "M", Toast.LENGTH_SHORT).show();
                    subjectsRepository.getSubjectsService().getRelatedSubjects(user).enqueue(new Callback<List<Subjects>>() {
                        @Override
                        public void onResponse(Call<List<Subjects>> call, Response<List<Subjects>> response) {
                            if (response.isSuccessful()) {
                                subList.clear();

                                //recyclerViewAdapter.notifyDataSetChanged();
                                recyclerViewAdapter = new MyRecyclerViewAdapter(AfterLogInActivity.this, subList);
                                recyclerView.setAdapter(recyclerViewAdapter);

                                List<Subjects> newLst;
                                newLst = response.body();
                                if (newLst.size() <= 14) {
                                subList.addAll(newLst);
                                recyclerViewAdapter.notifyDataSetChanged();
                                int insrtIndex = subList.size();
                                Subjects mysub = new Subjects(user, user + "_" + (insrtIndex + 1), 0, ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toString(), Integer.parseInt(subAgeEdit.getText().toString().trim()), 'M', subNameEdit.getText().toString().trim());
                                subjectsRepository.getSubjectsService().createSubject(mysub).enqueue(new Callback<Subjects>() {
                                    @Override
                                    public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                                        if (response.isSuccessful()) {
                                            subList.add(insrtIndex, mysub);
                                            totCount = subList.size();
                                            SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                                            SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                            prefsEditor.putInt("sub_count", mycount+1).apply();
                                            prefsEditor.putBoolean("can_access"+insrtIndex,true).apply();
                                            recyclerViewAdapter.notifyItemInserted(insrtIndex);
                                        } else {
                                            Toast.makeText(AfterLogInActivity.this, "Error Adding Subject. Try Again!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Subjects> call, Throwable t) {
                                        Toast.makeText(AfterLogInActivity.this, "Error Adding Subject. Try Again!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else {
                                    Toast.makeText(AfterLogInActivity.this, "Maximum Subject Capacity Reached.", Toast.LENGTH_SHORT).show();
                                    subList.addAll(newLst);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }


                            } else {
                                Toast.makeText(AfterLogInActivity.this, "Error Adding Subject. Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Subjects>> call, Throwable t) {
                            Toast.makeText(AfterLogInActivity.this, "Error Adding Subject, Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertDialog.dismiss();
                } else if (femaleBtn.isChecked()) {
                    //Toast.makeText(AfterLogInActivity.this, "F", Toast.LENGTH_SHORT).show();
                    subjectsRepository.getSubjectsService().getRelatedSubjects(user).enqueue(new Callback<List<Subjects>>() {
                        @Override
                        public void onResponse(Call<List<Subjects>> call, Response<List<Subjects>> response) {
                            if (response.isSuccessful()) {
                                subList.clear();
                                //recyclerViewAdapter.notifyDataSetChanged();
                                recyclerViewAdapter = new MyRecyclerViewAdapter(AfterLogInActivity.this, subList);
                                recyclerView.setAdapter(recyclerViewAdapter);

                                List<Subjects> newLst;
                                newLst = response.body();
                                if (newLst.size() <= 14) {
                                subList.addAll(newLst);
                                recyclerViewAdapter.notifyDataSetChanged();
                                int insrtIndex = subList.size();
                                Subjects mysub = new Subjects(user, user + "_" + (insrtIndex + 1), 0, ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toString(), Integer.parseInt(subAgeEdit.getText().toString().trim()), 'F', subNameEdit.getText().toString().trim());
                                subjectsRepository.getSubjectsService().createSubject(mysub).enqueue(new Callback<Subjects>() {
                                    @Override
                                    public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                                        if (response.isSuccessful()) {
                                            subList.add(insrtIndex, mysub);
                                            totCount = subList.size();
                                            SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                            prefsEditor.putInt("sub_count", mycount+1).apply();
                                            prefsEditor.putBoolean("can_access"+insrtIndex,true).apply();
                                            recyclerViewAdapter.notifyItemInserted(insrtIndex);
                                        } else {
                                            Toast.makeText(AfterLogInActivity.this, "Error Adding Subject. Try Again!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Subjects> call, Throwable t) {
                                        Toast.makeText(AfterLogInActivity.this, "Error Adding Subject. Try Again!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else {
                                    Toast.makeText(AfterLogInActivity.this, "Maximum Subject Capacity Reached.", Toast.LENGTH_SHORT).show();
                                    subList.addAll(newLst);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }


                            } else {
                                Toast.makeText(AfterLogInActivity.this, "Error Adding Subject. Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Subjects>> call, Throwable t) {
                            Toast.makeText(AfterLogInActivity.this, "Error Adding Subject, Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.dismiss();
                }


            }
        });
    }



}