package com.example.todo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Dialog insertIntoDatabaseDialog;
    private DatabaseHelper databaseHelper;
    private EditText timePick;
    private EditText datePick;
    Button implicit_btn;
    Button btnAlertDialogFragment;
    TextView fragmenttextView;


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        implicit_btn = (Button) findViewById(R.id.implicit_Intent);

        fragmenttextView = findViewById(R.id.fragmenttextView);
        btnAlertDialogFragment = findViewById(R.id.btnAlertDialogFragment);

        //btnAlertDialogFragment.setOnClickListener(this);


        SharedPreferences prefs = getSharedPreferences("tutorial", MODE_PRIVATE);
        int tutorial = prefs.getInt("tutorial", 0);
        if(tutorial == 1) {
            findViewById(R.id.tutorial).setVisibility(View.GONE);
        }


        databaseHelper = new DatabaseHelper(this);

        insertIntoDatabaseDialog = new Dialog(this);
        insertIntoDatabaseDialog.setContentView(R.layout.new_task);

        showTODOList();

        final Calendar myCalendar = Calendar.getInstance();
        datePick = insertIntoDatabaseDialog.findViewById(R.id.calendar);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            String myFormat = "MM.dd.yyyy";
            SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.ENGLISH);
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            datePick.setText(sdformat.format(myCalendar.getTime()));
        };

        datePick.setOnClickListener(v -> new DatePickerDialog(MainActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        timePick = insertIntoDatabaseDialog.findViewById(R.id.timerPicker);
        timePick.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(MainActivity.this, (timePicker, selectedHour, selectedMinute) ->
                    timePick.setText(String.format("%02d:%02d", selectedHour, selectedMinute)), hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        implicit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.google.com"));
                startActivity(intent);
            }
        });

        btnAlertDialogFragment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnAlertDialogFragment:
                MyDialogFragment dialogFragment = new MyDialogFragment();

               FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialogFragment = new MyDialogFragment();

                ft = getSupportFragmentManager().beginTransaction();

                ft.addToBackStack(null);

                dialogFragment.show(ft, "dialog");
                break;
        }
    }

        });






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        insertIntoDatabaseDialog.show();
        insertTaskIntoDatabase();

        SharedPreferences.Editor editor = getSharedPreferences("tutorial", MODE_PRIVATE).edit();
        editor.putInt("tutorial", 1);
        editor.apply();
        findViewById(R.id.tutorial).setVisibility(View.GONE);
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("InflateParams")
    private void showTODOList() {
        Map<String, ArrayList<String>> map = databaseHelper.selectAll();
        ArrayList<String> taskCheckArrayList = map.get("taskCheck");
        ArrayList<String> tittle = map.get("Tittle");
        ArrayList<String> time = map.get("Time");
        ArrayList<String> date = map.get("Date");

        TableLayout item = findViewById(R.id.tabela);
        item.removeAllViews();

        if(tittle == null) return;
        for(int i = 0; i < tittle.size(); i++) {
            @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.horizontal_line, null);
            item.addView(child);
            child = getLayoutInflater().inflate(R.layout.task_list, null);
            item.addView(child);

            EditText task = child.findViewById(R.id.task);
            task.setText(tittle.get(i));

            TextView timePicker = child.findViewById(R.id.TODOTime);
            assert time != null;
            timePicker.setText(time.get(i));

            TextView datePicker = child.findViewById(R.id.datePicker);
            assert date != null;
            datePicker.setText(date.get(i));

            if(time.get(i).equals("") && date.get(i).equals("")) {
                TableLayout tl = child.findViewById(R.id.timeAndDateTable);
                tl.setVisibility(View.GONE);
            }

            CheckBox taskCheck = child.findViewById(R.id.taskCheck);
            taskCheck.setTag(i);

            taskCheck.setOnClickListener(e -> {
                int id = (Integer) taskCheck.getTag();
                String checked = "";
                if(taskCheck.isChecked()) {
                    checked = "true";
                    task.setPaintFlags(task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else {
                    task.setPaintFlags(0);
                }
                databaseHelper.taskCheckUpdate(id, checked);
            });

            assert taskCheckArrayList != null;
            if(taskCheckArrayList.get(i).equals("true")) {
                taskCheck.setChecked(true);
                task.setPaintFlags(task.getPaintFlags()|     Paint.STRIKE_THRU_TEXT_FLAG);
            }

            ImageButton deleteButton = child.findViewById(R.id.removeRowButton);
            deleteButton.setTag(i);
            deleteButton.setOnClickListener(e -> {
                int id = (Integer) deleteButton.getTag();
                databaseHelper.deleteRow(id);
                showTODOList();
            });
        }

    }

    private void insertTaskIntoDatabase() {
        Button insertButton = insertIntoDatabaseDialog.findViewById(R.id.insertIntoDatabaseButton);
        insertButton.setOnClickListener(e->{
            EditText n = insertIntoDatabaseDialog.findViewById(R.id.todo);
            EditText time = insertIntoDatabaseDialog.findViewById(R.id.timerPicker);
            EditText date = insertIntoDatabaseDialog.findViewById(R.id.calendar);
            databaseHelper.insertIntoDatabase(n.getText().toString(), time.getText().toString(), date.getText().toString());
            n.setText("");
            timePick.setText("");
            datePick.setText("");
            showTODOList();
            insertIntoDatabaseDialog.hide();

            Toast.makeText(getApplicationContext(),"Successfully Added", Toast.LENGTH_SHORT).show();

        });
    }






}
