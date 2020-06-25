package com.example.todomvvm.tasks;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todomvvm.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mehdi.sakout.aboutpage.Element adaElement = new mehdi.sakout.aboutpage.Element();

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.ic_profile)
                .setDescription("This is Demo Version")
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(adaElement)
                .addGroup("Connect With Me")
                .addEmail("maharjan.anish.yt@gmail.com")
                .addFacebook("Anish Maharjan")
                .addInstagram("p.otato.head")
                .addTwitter("anish353")
                .create();

        setContentView(aboutPage);


    }
}
