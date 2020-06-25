package com.example.todomvvm.tasks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.todomvvm.addedittask.AddEditTaskActivity;
import com.example.todomvvm.R;
import com.example.todomvvm.database.AppDatabase;
import com.example.todomvvm.database.Repository;
import com.example.todomvvm.database.TaskDao;
import com.example.todomvvm.database.TaskEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;
    private NavigationView nv;
    Toolbar toolbar;
    Context context;
    private Switch darkModeSwitch;
    Button delete;
    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    DeleteViewModel deleteViewModel;

    private final String CHANNEL_ID = "personal_notification";
    private final int NOTIFICATION_ID=001;

    MainActivityViewModel viewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        deleteViewModel=  ViewModelProviders.of(this).get(DeleteViewModel.class);
    /*  if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        setDarkModeSwitch();
    }
    private void setDarkModeSwitch() {


        darkModeSwitch = findViewById(R.id.drawer_switch);
        darkModeSwitch.setChecked(new DarkModePrefManager(this).isNightMode());
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DarkModePrefManager darkModePrefManager = new DarkModePrefManager(MainActivity.this);
                darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
            }
        });*/

        setContentView(R.layout.activity_main);
delete = findViewById(R.id.deleteAll);
        nv = findViewById(R.id.nv);
        dl = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(this);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        context = this;
        toggle = new ActionBarDrawerToggle(this, dl,R.string.Navigation_Drawer_Open,R.string.Navigation_Drawer_Close);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon);


        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                final int position = viewHolder.getAdapterPosition();
                final List<TaskEntry> todoList = mAdapter.getTasks();
                final TaskEntry deletedTask = todoList.get(position);
                viewModel.deleteTask(todoList.get(position));
                todoList.remove(position);
                mAdapter.setTasks(todoList);
                mAdapter.notifyDataSetChanged();
                Snackbar snackbar = Snackbar
                        .make(mRecyclerView, "Task removed do you want to undo?", Snackbar.LENGTH_LONG);
                snackbar.setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        todoList.add(position, deletedTask);
                        mAdapter.setTasks(todoList);
                        mAdapter.notifyDataSetChanged();
                        viewModel.addTask(todoList.get(position));

                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        }).attachToRecyclerView(mRecyclerView);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteViewModel.deleteAllTasks();;

            }
        });
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                mAdapter.setTasks(taskEntries);
            }
        });
    }

    public void displayNotification(View view) {



        createNotificationChannel();
NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.about);
        builder.setContentTitle("Notification");
        builder.setContentText("You have " + TaskAdapter.count + " tasks");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());

    }

    public void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {

            CharSequence name = "Notification";
            String discription ="You have " +TaskAdapter.count + " tasks";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(discription);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }




    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (dl.isDrawerOpen(GravityCompat.START)) {
                dl.closeDrawer(GravityCompat.START);
            } else {
                dl.openDrawer(GravityCompat.START);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {

            case R.id.add1:
                Intent intent = new Intent(getApplicationContext(), AddEditTaskActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Add List", Toast.LENGTH_SHORT).show();
                break;

            case R.id.darkModeSwitch:
               //Intent darkMode = new Intent(getApplicationContext(),MainActivity.class);
               //startActivity(darkMode);
                Toast.makeText(MainActivity.this, "Dark Mode", Toast.LENGTH_SHORT).show();
                break;
            case R.id.aboutUs:
                 Intent intent2 = new Intent(getApplicationContext(),AboutUsActivity.class);
                 startActivity(intent2);
            Toast.makeText(MainActivity.this, "About Us", Toast.LENGTH_SHORT).show();
                break;

            case R.id.Share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = "https://www.facebook.com/anish.maharjan.353";
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Share the App");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                startActivity(Intent.createChooser(shareIntent, "Share via"));

                Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                break;

            case R.id.Feedback:
                Intent intentF = new Intent(Intent.ACTION_SEND);
                intentF.setType("message/html");
                intentF.putExtra(Intent.EXTRA_EMAIL, new String("maharjan.anish.yt@gmail.com"));
                intentF.putExtra(Intent.EXTRA_SUBJECT, "Feedback Form");
                try{
                    startActivity(Intent.createChooser(intentF,"Please Select Email"));
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no Email Clients", Toast.LENGTH_LONG).show();
                }

        }
        return true;
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }


}
