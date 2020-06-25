package com.example.todomvvm.tasks;

import android.app.Application;

import com.example.todomvvm.database.TaskEntry;
import com.example.todomvvm.database.Repository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class DeleteViewModel extends AndroidViewModel {
    Repository repository;
    private LiveData<List<TaskEntry>> tasks;

    public DeleteViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }
    public void deleteAllTasks(){
        repository.deleteAllTasks();
    }

}