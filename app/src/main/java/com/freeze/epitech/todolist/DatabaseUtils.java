package com.freeze.epitech.todolist;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by redleader on 29/01/2018.
 */

public class DatabaseUtils {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference countTask = database.getReference("count");

    private static DatabaseUtils mInstance= null;

    public Integer taskCount;

    protected DatabaseUtils(){}

    public static synchronized DatabaseUtils getInstance(){
            if(null == mInstance){
                mInstance = new DatabaseUtils();
            }
            return mInstance;
        }

    public void addTask(String title, String content, String date) {
        DatabaseReference taskTitle = database.getReference("tasks/" + this.taskCount + "/title");
        DatabaseReference taskContent = database.getReference("tasks/" + this.taskCount + "/content");
        DatabaseReference taskNumber = database.getReference("tasks/" + this.taskCount + "/number");
        DatabaseReference taskDate = database.getReference("tasks/" + this.taskCount + "/date");
        DatabaseReference done = database.getReference("tasks/" + this.taskCount + "/done");
        String count = DatabaseUtils.getInstance().taskCount.toString();
        taskNumber.setValue(count);
        this.taskCount += 1;
        String updateCount = DatabaseUtils.getInstance().taskCount.toString();
        countTask.setValue(updateCount);
        taskTitle.setValue(title);
        taskDate.setValue(date);
        taskContent.setValue(content);
        done.setValue("false");
        System.out.println("taskCount in the singleton " + this.taskCount);
    }

    public void updateTask(String title, String content, int id) {
        DatabaseReference taskTitle = database.getReference("tasks/" + id + "/title");
        DatabaseReference taskContent = database.getReference("tasks/" + id + "/content");
        taskTitle.setValue(title);
        taskContent.setValue(content);
    }

    public void removeTask(int id) {
        DatabaseReference taskToDelete = database.getReference("tasks/" + id);
        DatabaseReference nbTask = database.getReference("count/");
        int updateCount = DatabaseUtils.getInstance().taskCount;

        updateCount -= 1;
        String countToPush = String.valueOf(updateCount);
        taskToDelete.removeValue();
        nbTask.setValue(countToPush);
    }

}

