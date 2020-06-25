package com.example.todomvvm.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.todomvvm.tasks.TaskAdapter;

import java.util.List;

public class Repository {

    TaskDao dao;
    private static Repository instance;
    private static int i = 0;
     int afileDao;


    private Repository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        dao = database.taskDao();
    }

    public Repository(AppDatabase appDatabase){
        dao = appDatabase.taskDao();
    }

    public LiveData<List<TaskEntry>> getTasks(){
       return dao.loadAllTasks();
    }

    public LiveData<TaskEntry> getTaskById(int taskId){
        return dao.loadTAskById(taskId);
    }

    public void updateTask(final TaskEntry task){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.update(task);
            }
        });
        TaskAdapter.count=0;
    }

    public void deleteTask(final TaskEntry task){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
               dao.deleteTask(task);
            }
        });
        TaskAdapter.count=0;
    }

    public  void  insertTask(final TaskEntry task){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insertTask(task);
            }
        });
        TaskAdapter.count=0;
    }

    public static Repository getInstance(Context context) {
        if (instance == null) {
            instance = new Repository(context);
        }
        return instance;
    }

    public void deleteAllTasks(){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteAll();
            }
        });
        TaskAdapter.count=0;
    }








}
