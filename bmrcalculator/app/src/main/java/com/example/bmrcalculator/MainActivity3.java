package com.example.bmrcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    Button cancel_save;
    Button save_result;
    TextView showname,showbmi,showbmr;
    String name,age,height,weight,sex,mode,o_name,o_weight,o_sex,o_age,o_height;
    DecimalFormat precision = new DecimalFormat("0.00");
    ListView listView;
    List<String> list = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        initViewElement();
        Intent it = this.getIntent();
        if (it != null)
        {
            showbmr.setText("1");
            Bundle bundle= it.getExtras();
            //Bundle bundle2= it.getExtras();
            if(bundle !=null){
                String inputStr =bundle.getString("name");
                if(inputStr !=null && !inputStr.equals("")){
                    showname.setText(inputStr);
                }
                String wei =bundle.getString("weight");
                String hei =bundle.getString("height");
                String ages =bundle.getString("age");
                int w = Integer.parseInt(wei);
                int h = Integer.parseInt(hei);
                int a = Integer.parseInt(ages);
                weight=wei;
                height=hei;
                age=ages;
                name=inputStr;
                if(wei !=null && hei!=null && !wei.equals("") && !hei.equals("")) {
                    //showbmi.setText(wei);
                    double dh;
                    dh = 1.00 * h / 100;
                    dh *= dh;
                    double bmi;
                    bmi = 1.00 * w / dh;
                    String s_bmi;
                    s_bmi = Double.toString(Double.parseDouble(precision.format(bmi)));
                    showbmi.setText(s_bmi);
                }
                sex =bundle.getString("sex");
                double bmr = 0;
                if(sex !=null && !sex.equals("")){
                    if(sex.equals("Male")){
                        bmr= 1.0*66 + 1.0*13.7 * w + 1.0*5 * h - 1.0*6.8 * a;
                    }
                    else if(sex.equals("Female")){
                        bmr= 65.5 + 1.0*9.6 * w + 1.0*1.8 * h - 1.0*4.7 * a;
                    }
                    String s_bmr = Double.toString(Double.parseDouble(precision.format(bmr)));
                    showbmr.setText(s_bmr);
                }
                mode=bundle.getString("mode");
                if(bundle.getString("mode").equals("Modify Record"))
                {
                    inputStr = bundle.getString("o_name");
                    o_name = inputStr;
                    inputStr = bundle.getString("o_weight");
                    o_weight = inputStr;
                    inputStr = bundle.getString("o_height");
                    o_height = inputStr;
                    inputStr = bundle.getString("o_sex");
                    o_sex = inputStr;
                    inputStr = bundle.getString("o_age");
                    o_age = inputStr;
                }
            }
        }
        /*listView = (ListView) findViewById(R.id.listView);
        ListAdapter adapter = new ArrayAdapter<>( this , android.R.layout.simple_list_item_1 ,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adpterView, View view, int i, long l) {
                //Toast.makeText(      MainActivity.this, "點選第"+(i+1)+" 個\n 內容:"+values[i], Toast.LENGTH_SHORT).show();
            }
        });*/
        cancel_save.setOnClickListener(view -> finish());
        save_result.setOnClickListener(view -> {
            HashMap<String,String> datass;
            datass = new HashMap<String,String>();
            datass.put("name",name);
            datass.put("sex",sex);
            datass.put("weight",weight);
            datass.put("height",height);
            datass.put("age",age);
            if(mode.equals("Create Record"))
            {
                insertRecord(datass);
            }
            else if(mode.equals("Modify Record"))
            {
                UpdateRecord("Data");
            }
            Intent itt = new Intent();
            itt.setClass(MainActivity3.this, MainActivity.class);
            startActivity(itt);
        });
    }


    private void initViewElement(){
        cancel_save=(Button) findViewById(R.id.cancel_save);
        save_result=(Button) findViewById(R.id.save_result);
        showname =(TextView) findViewById(R.id.showname);
        showbmi =(TextView) findViewById(R.id.showbmi);
        showbmr=(TextView) findViewById(R.id.showbmr);
    }
    //insert_java
    private synchronized void insertRecord(HashMap<String, String> map) {
        Thread thread = new Thread(new Runnable() {
            HashMap<String, String> _map;

            @Override
            public void run() {
                String path = "http://10.0.2.2/bmr/insert.php";
                executeHttpPost(path, _map);
                Log.d( "internet thread", "End");
            }

            public Runnable init(HashMap<String, String> map) {
                _map = map;
                return this;
            }
        }.init(map));
        thread.start();
        while (thread.isAlive()){
            //Main Thread do nothing wait for internet thread
        }
        Log.d( "insertRecord()", "End");
    }
    private void executeHttpPost(String path, HashMap<String, String> map) {
        try {
            // request method is POST
            initViewElement();
            URL urlObj = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty( "Accept-Charset", "UTF-8");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.connect();

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            String post = "name="+name+"&sex="+sex+"&age="+age+"&weight="+weight+"&height="+height;
            Log.d("post",post);
            wr.writeBytes(post);
            wr.flush();
            wr.close();

            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("Save Record", "result: " + result.toString());
            conn.disconnect();
        } catch (IOException e) {
            Log.v(  "Save Record", "Record saved failed");
            e.printStackTrace();
        }
    }
    //update
    private synchronized void UpdateRecord(String var){
        Thread thread = new Thread(new Runnable() {
            String _var;
            @Override
            public void run() {
                executeHttpPost2("http://10.0.2.2/bmr/update.php",_var);
            }
            public Runnable init(String var){
                _var = var;
                return this;
            }
        }.init(var));
        thread.start();
        while(thread.isAlive()){
            //waiting for internet thread
        }
    }
    private void executeHttpPost2(String path, String var){
        try{
            URL urlObj = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset","UTF-8");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.connect();

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            //id=parameter
            String post = "name="+name+"&age="+age+"&weight="+weight+"&height="+height+"&sex="+sex+"&oname="+o_name+"&oage="+o_age+"&oweight="+o_weight+"&oheight="+o_height+"&osex="+o_sex;
            wr.writeBytes(post);
            Log.d("update",post);
            //Log.d("output:",var);
            wr.flush();
            wr.close();

            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                result.append(line);
            }

            conn.disconnect();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}