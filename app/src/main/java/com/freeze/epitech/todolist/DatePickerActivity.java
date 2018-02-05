package com.freeze.epitech.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

/**
 * Created by redleader on 31/01/2018.
 */

public class DatePickerActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Button button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker);

        datePicker = findViewById(R.id.datePicker);
        datePicker.setCalendarViewShown(false);;
        datePicker = findViewById(R.id.datePicker);
        button = findViewById(R.id.buttonDate);
        button.setOnClickListener(onClick());
    }

    private View.OnClickListener onClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                Intent i = new Intent(DatePickerActivity.this, AddTaskActivity.class);
                Bundle b = new Bundle();

                b.putString("date", day + "/" + month + "/" + year);

                i.putExtras(b);
                startActivity(i);
            }
        };
    }


}
