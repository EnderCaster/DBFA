package io.github.opuserzangying.dbmanageapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner dbKind;
        final EditText host, port, dbName, dbArgument, dbUsername, dbPassword;
        Button connect;

        dbKind = (Spinner) findViewById(R.id.main_dbKind);
        host = (EditText) findViewById(R.id.main_host);
        port = (EditText) findViewById(R.id.main_port);
        dbName = (EditText) findViewById(R.id.main_dbName);
        dbArgument = (EditText) findViewById(R.id.main_dbArgument);
        dbUsername = (EditText) findViewById(R.id.main_dbUsername);
        dbPassword = (EditText) findViewById(R.id.main_dbPassword);
        connect = (Button) findViewById(R.id.main_connect);
        Button test=(Button) findViewById(R.id.main_testButton);
        //TODO check input data
        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int portNumber = 0;
                if (!port.getText().toString().equals("")) {
                    portNumber=Integer.parseInt(port.getText().toString());
                }
                if (portNumber > 65535) {
                    port.setText(port.getText().toString().substring(0, 4));
                    Toast.makeText(getApplicationContext(), "port number should LE 65535", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //TODO process data in text box
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ifVerifyFailed=host.getText().toString().equals("")||port.getText().toString().equals("")||dbName.getText().toString().equals("")||dbUsername.getText().toString().equals("")||dbPassword.getText().toString().equals("");
                if(!ifVerifyFailed){
                    jump();
                }else{
                    stay();
                }
            }
            private void stay(){
                //do nothing but alert user these text should not empty
                Toast.makeText(getApplicationContext(), R.string.error_connString_empty,Toast.LENGTH_SHORT).show();
            }
            private void jump(){
                Intent intent = new Intent();
                //put texts in Extra
                String connString[] = {host.getText().toString(), port.getText().toString(), dbName.getText().toString(), dbArgument.getText().toString(), dbUsername.getText().toString(), dbPassword.getText().toString()};
                intent.putExtra("connString", connString);
                intent.putExtra("dbKind", dbKind.getSelectedItemPosition());
                intent.setClass(MainActivity.this, ManagementActivity.class);
                startActivity(intent);
                finish();
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host.setText("172.16.1.129");
                port.setText("3306");
                dbName.setText("mysql");
                dbUsername.setText("root");
                dbPassword.setText("mysql_cU8r2IyDZK");
            }
        });
    }
}
