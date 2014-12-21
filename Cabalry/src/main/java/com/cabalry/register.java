package com.cabalry;

import com.cabalry.db.*;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class register extends ActionBarActivity {

    EditText username, email, password, number, name;
    Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        number = (EditText) findViewById(R.id.number);
        name = (EditText) findViewById(R.id.name);
        register = (Button) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });


    }

    private void register() {
        System.out.println("register");
        String u, p, e, num, n;
        u = username.getText().toString();
        p = password.getText().toString();
        e = email.getText().toString();
        num = number.getText().toString();
        n = name.getText().toString();

        DB db = new DB();
        db.register(u, p, e, n, num);
    }

}
