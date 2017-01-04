package io.github.opuserzangying.dbmanageapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import io.github.opuserzangying.util.JDBCUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ManagementActivity extends AppCompatActivity {

    static ResultSet rs=null;

    static int affectedRows = 0;

    static boolean wait=true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_management);
        EditText sql = (EditText) findViewById(R.id.sql);
        Button executeButton = (Button) findViewById(R.id.manage_executeButton);
        TextView hintArea = (TextView) findViewById(R.id.manage_hintArea);
        //TODO change this area's Class next version
        TextView dataArea = (TextView) findViewById(R.id.manage_dataArea);

        //use main intent's Extra data to connect database
        Intent intent = getIntent();
        String connString[] = intent.getStringArrayExtra("connString");
        int dbKind=intent.getIntExtra("dbKind",0);
        Thread connectThread=new Thread(() -> {
                //while(!Thread.interrupted()){
                JDBCUtils.connect(connString[0], //host
                        connString[1], //port
                        connString[2], //database name
                        connString[3], //extra arguments for database
                        connString[4], //database username
                        connString[5], //database password
                        dbKind);
            //}
        });
        if((connectThread!=null)&&(!connectThread.isAlive())){
            connectThread.start();
        }
        executeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sql_S = sql.getText().toString();
                if (checkSyntax(sql_S)) {
                    //check if select
                    if (sql_S.charAt(0) == 's' || sql_S.charAt(0) == 'S') {
                        //select
                        //get rs columns and rows
                        int cols = 0, rows = 0;
                        Thread selectThread=new Thread(() -> {
                            rs = JDBCUtils.select(sql_S);
                            wait=false;
                        });
                        selectThread.start();
                        try {
                            selectThread.join();
                            /*rs.last();//TODO check rs error
                            rows = rs.getRow();
                            rs.beforeFirst();*/
                            ResultSetMetaData resultSetMetaData = rs.getMetaData();
                            cols = resultSetMetaData.getColumnCount();
                            //print column name
                            String dataAreaContent = "";
                            while (rs.next()) {
                                for (int col = 1; col <= cols; col++) {
                                    dataAreaContent += rs.getString(col);
                                    if (col != cols - 1) {
                                        dataAreaContent += "\t";
                                    }
                                }
                                //if (rs.getRow() != rows) {
                                    dataAreaContent += "\n";
                                //}
                            }
                            dataArea.setText(dataAreaContent);
                        } catch (SQLException e) {
                            JDBCUtils.rollback();
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            JDBCUtils.close();
                        }
                        showDataArea();

                    } else {
                        //update
                        Thread updateThread=new Thread(() -> {
                            affectedRows =JDBCUtils.update(sql_S);
                        });
                        updateThread.start();
                        try {
                            updateThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String sql_result = "Modify success " + affectedRows + " affected";
                        hintArea.setText(sql_result);
                        showHintArea();
                    }
                } else {
                    hintArea.setText(R.string.error_sql_syntax);
                    showHintArea();
                }
            }

            void showHintArea() {
                dataArea.setVisibility(View.INVISIBLE);
                hintArea.setVisibility(View.VISIBLE);
            }

            void showDataArea() {
                hintArea.setVisibility(View.INVISIBLE);
                dataArea.setVisibility(View.VISIBLE);
            }

            boolean checkSyntax(String sql_S) {
                boolean ifRight = false;
                //TODO check SQL syntax
                ifRight = true;
                return ifRight;
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle("Quit Confirm")
                    .setMessage("Close Connection and Quit Application?")
                    .setPositiveButton("Quit", (dialogInterface, i) -> {
                        JDBCUtils.close();
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JDBCUtils.close();
    }
}
