package com.di.walker.allen.simplepokedex1;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button gotoButton;
    private Button SquadButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();

        gotoButton.setOnClickListener(this);
    }


    private void bindViews() {

        gotoButton = (Button) findViewById(R.id.listButton);
        SquadButton = (Button) findViewById(R.id.squadButton);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.listButton) {
            Intent i = new Intent(this, monListActivity1.class);
            startActivity(i);
        } else {

            if (v.getId() == R.id.listButton) {
                Intent i = new Intent(this, Squadra.class);
                startActivity(i);
            }
        }
    }


}
