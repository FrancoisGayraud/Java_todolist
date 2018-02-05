package com.freeze.epitech.todolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class AddTaskActivity extends AppCompatActivity {

    private EditText mEditTextTitle;
    private EditText mEditTextContent;
    private Button mButtonDone;
    private int id = -1;
    private String textEdit;
    private String date;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Bundle b = getIntent().getExtras();

        mEditTextTitle = (EditText) findViewById(R.id.title);
        mEditTextContent = (EditText) findViewById(R.id.content);
        mButtonDone = (Button) findViewById(R.id.buttonDone);
        mButtonDone.setOnClickListener(onClickDate());
        if(b != null) {
            textEdit = b.getString("text");
            if (textEdit != null) {
                id = b.getInt("id");
                String lines[] = textEdit.split("\n", 3);
                System.out.println(lines[0] + ":" + lines[1] + ":" + lines[2]);
                mEditTextTitle.setText(lines[1]);
                mEditTextContent.setText(trimString(lines[2]));
            }
            date = b.getString("date");
            System.out.println("DATE PICKED IS : " + date);
        }
    }

    public String trimString(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
            str= str.substring(1, str.length());
        }
        return str;
    }

    private View.OnClickListener onClickDate() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id == -1)
                    DatabaseUtils.getInstance().addTask(mEditTextTitle.getText().toString(), mEditTextContent.getText().toString(), date);
                else
                    DatabaseUtils.getInstance().updateTask(mEditTextTitle.getText().toString(), mEditTextContent.getText().toString(), id);
                Intent i = new Intent(AddTaskActivity.this, MainActivity.class);
                startActivity(i);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.edit_task);
        item.setIcon(R.drawable.ic_done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (id == -1)
            DatabaseUtils.getInstance().addTask(mEditTextTitle.getText().toString(), mEditTextContent.getText().toString(), date);
        else
            DatabaseUtils.getInstance().updateTask(mEditTextTitle.getText().toString(), mEditTextContent.getText().toString(), id);
        Intent i = new Intent(AddTaskActivity.this, MainActivity.class);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

}