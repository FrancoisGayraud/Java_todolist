package com.freeze.epitech.todolist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mLayout;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private HashMap<Integer, String> allTask = new HashMap<>();
    private List<Integer> removeTask = new ArrayList<>();
    private Menu menu;
    private boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isNetworkAvailable(getApplicationContext())) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DatabaseReference countTask = database.getReference("count");
            DatabaseReference tasks = database.getReference("tasks");
            mLayout = (LinearLayout) findViewById(R.id.Tasks);
            mLayout.addView(createNewTextView("\n", 16, true, Color.WHITE, -1));

            countTask.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    DatabaseUtils.getInstance().taskCount = Integer.parseInt(value);
                    System.out.println("value from db = " + value + " integer is " + DatabaseUtils.getInstance().taskCount);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.out.println("Failed to read value." + error.toException());
                }
            });

            tasks.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();
                    assert value != null;
                    if (value.values().toArray().length > 2) {
                        int color;
                        int id = Integer.parseInt(value.get("number"));
                        String title = value.get("title");
                        String date = value.get("date");
                        String content = value.get("content");
                        String done = value.get("done");
                        if (done.equals("true"))
                            color = Color.DKGRAY;
                        else
                            color = Color.LTGRAY;

                        mLayout.addView(createNewTextView("\n" + date + "\n", 15, true, Color.DKGRAY, -1));
                        mLayout.addView(createNewTextView("\n" + title + "\n\n" + content + "\n", 17, true, color, id));
                        mLayout.addView(createNewButton(id + 1000, color));
                        mLayout.addView(createNewTextView("\n", 16, true, Color.WHITE, -1));
                        allTask.put(id, done);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.out.println("Failed to read value." + error.toException());
                }
            });

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, DatePickerActivity.class);
                    startActivity(i);
                }
            });

        } else {
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            ab.setTitle("Internet Error");
            ab.setMessage("You need a working connection to use this app");
            ab.show();
        }

    }

    private View.OnClickListener editListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (!edit) {
                if (DatabaseUtils.getInstance().taskCount <= 1000) {
                    int id = view.getId();
                    TextView text = findViewById(id);
                    Intent i = new Intent(MainActivity.this, AddTaskActivity.class);
                    Bundle b = new Bundle();

                    b.putString("text", text.getText().toString());
                    b.putInt("id", view.getId());
                    i.putExtras(b);
                    startActivity(i);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("You can't have more than 1000 things to do !");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
            else {
                TextView text = findViewById(view.getId());

                if (DatabaseUtils.getInstance().taskCount > 0) {
                    if (removeTask.contains(view.getId())) {
                        ListIterator<Integer> it = removeTask.listIterator();
                        while(it.hasNext()) {
                            Integer id = it.next();
                            if (id == view.getId())
                                it.remove();
                        }
                        text.setBackgroundColor(Color.LTGRAY);
                    }
                    else {
                        removeTask.add(view.getId());
                        text.setBackgroundResource(R.color.colorAccent);
                    }
                }
            }
        }
    };

    private TextView createNewTextView(String text, int size, boolean match, int color, int id) {
        final ViewGroup.LayoutParams lparams;
        if (match) {
            lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        final TextView textView = (TextView) getLayoutInflater().inflate(R.layout.task_layout, null);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(lparams);
        textView.setTextSize(size);
        textView.setText(text);
        textView.setBackgroundColor(color);
        if (color == Color.DKGRAY)
            textView.setTextColor(Color.WHITE);
        else
            textView.setTextColor(Color.BLACK);
        textView.setId(id);
        System.out.println("ID of textview " + textView.getId());
        if (id != -1)
            textView.setOnClickListener(editListener);
        return textView;
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View view) {
            int textId = view.getId();
            textId -= 1000;
            System.out.println("ID of ITEM SELECTED" + view.getId());
            DatabaseReference task = database.getReference("tasks/" + textId + "/done");
            TextView textView = findViewById(textId);
            Button button = findViewById(view.getId());

            if (allTask.get(textId).equals("true")) {
                task.setValue("false");
                textView.setBackgroundColor(Color.LTGRAY);
                textView.setTextColor(Color.BLACK);
                allTask.put(textId, "false");
                button.setText("Done");
            }
            else {
                task.setValue("true");
                textView.setBackgroundColor(Color.DKGRAY);
                allTask.put(textId, "true");
                textView.setTextColor(Color.WHITE);
                button.setText("Undo");
            }
           /* finish();
            overridePendingTransition( 0, 0);
            startActivity(getIntent());
            overridePendingTransition( 0, 0);*/
        }
    };

    private Button createNewButton(int id, int color) {
        Button button = new Button(this);
        if (color == Color.LTGRAY)
            button.setText("Done");
        else
            button.setText("Undo");
        button.setTextColor(Color.parseColor("#ffffff"));
        button.setBackgroundResource(R.drawable.button_style);
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setId(id);
        System.out.println("ID of button : " + button.getId());
        button.setOnClickListener(buttonListener);
        return button;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.edit_task:
                changeIcon();
                break;

            default:
                break;
        }
        System.out.println("sdqjlflsfjlqfjl EDIT SELECT ");
        return super.onOptionsItemSelected(item);
    }

    public void changeIcon(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (menu != null) {
                    MenuItem item = menu.findItem(R.id.edit_task);
                    if (item != null && !edit) {
                        item.setIcon(R.drawable.ic_done);
                        edit = true;
                    }
                    else if (item != null) {
                        item.setIcon(R.drawable.ic_delete);
                        edit = false;
                        ListIterator<Integer> it = removeTask.listIterator();
                        while(it.hasNext()) {
                            Integer id = it.next();
                            DatabaseUtils.getInstance().removeTask(id);
                        }
                        finish();
                        startActivity(getIntent());
                    }
                }
            }
        });
    };

    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

