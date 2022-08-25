package com.example.bmrcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //EditText editPassword;
    Button create,modify,delete;
    ListView listView;
    List<String> list = new ArrayList<String>();
    DecimalFormat precision = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewElement();
        GetRecord("Data");
        create.setOnClickListener(view -> {
            Bundle bundle=new Bundle();
            bundle.putString("mode", "Create Record");
            Intent it =new Intent();
            it.putExtras(bundle);
            it.setClass(MainActivity.this, MainActivity2.class);
            startActivity(it);
        });
        /*listView = (ListView) findViewById(R.id.listView);
        ListAdapter adapter = new ArrayAdapter<>( this , android.R.layout.simple_list_item_1 ,list);
        listView.setAdapter(adapter);*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adpterView, View view, int i, long l) {
                String s = listView.getItemAtPosition(i).toString();
                char[] s_char = s.toCharArray();
                String output = "";
                for(int c = 0; s_char[c] != '\t'; c++)
                    output += s_char[c];
                GetRecord2(i);
                //Toast.makeText(      MainActivity.this, "點選第"+(i+1)+" 個\n 內容:"+values[i], Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                list.remove(i);
                DeleteRecord(i);
                listView.invalidateViews();
                return true;
            }
        });

    }
    private synchronized void GetRecord(String var){
        Thread thread = new Thread(new Runnable() {
            String _var;
            @Override
            public void run() {
                executeHttpPost("http://10.0.2.2/bmr/read.php",_var);
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
    private void executeHttpPost(String path, String var){
        try{
            URL urlObj = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset","UTF-8");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.connect();

            int responseCode = conn.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = bufferedReader.readLine();
                JSONArray dataJson = new JSONArray(line);
                int l = dataJson.length();
                int count = 0;
                //list.add("           Name"+"                  "+"BMR          ");
                while(count < l){
                    //Log.d("line:",line);
                    //int i = dataJson.length()-2;
                    JSONObject info = dataJson.getJSONObject(count);
                    String name = info.getString("name");
                    String sex = info.getString("sex");
                    String weight = info.getString("weight");
                    String height = info.getString("height");
                    String age = info.getString("age");
                    double _bmr = bmr(sex,Double.parseDouble(height),Double.parseDouble(weight),Integer.parseInt(age));
                    list.add(name+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+Double.toString(Double.parseDouble(precision.format(_bmr))));
                    count++;
                }
                ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);
                listView.setAdapter(adapter);
                inputStream.close();
            }

            conn.disconnect();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private double bmr(String sex, double h, double w, int age){
        //male
        if(sex.equals("Male")){
            return 66+(13.7*w+5*h-6.8*age);
        }
        //female
        return 65.5+(9.6*w+1.8*h-4.7*age);
    }
    private synchronized void GetRecord2(int pos){
        Thread thread = new Thread(new Runnable() {
            int _pos;
            @Override
            public void run() {
                executeHttpPost2("http://10.0.2.2/bmr/read.php",_pos);
            }
            public Runnable init(int pos){
                _pos = pos;
                return this;
            }
        }.init(pos));
        thread.start();
        while(thread.isAlive()){
            //waiting for internet thread
        }
    }
    private void executeHttpPost2(String path, int pos){
        try{
            URL urlObj = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset","UTF-8");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.connect();

            int responseCode = conn.getResponseCode();

            String name = "";
            String sex = "";
            String weight = "";
            String height = "";
            String age = "";

            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = bufferedReader.readLine();
                JSONArray dataJson = new JSONArray(line);
                int l = dataJson.length();
                int count = pos;
                JSONObject info = dataJson.getJSONObject(count);
                name = info.getString("name");
                sex = info.getString("sex");
                weight = info.getString("weight");
                height = info.getString("height");
                age = info.getString("age");
                //create.setText(Double.toString(Double.parseDouble(precision.format(_bmr))));
                //Log.d("data :", name+" "+sex+" "+weight+" "+height+" "+age);

                inputStream.close();
            }

            conn.disconnect();
            Bundle bundle = new Bundle();
            bundle.putString("mode","Modify Record");
            bundle.putString("name",name);
            bundle.putString("sex",sex);
            bundle.putString("weight",weight);
            bundle.putString("height",height);
            bundle.putString("age",age);
            Intent it = new Intent();
            it.putExtras(bundle);
            it.setClass(MainActivity.this, MainActivity2.class);
            startActivity(it);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private synchronized void DeleteRecord(int pos){
        Thread thread = new Thread(new Runnable() {
            int _pos;
            @Override
            public void run() {
                executeHttpPost3("http://10.0.2.2/bmr/read.php",_pos);
            }
            public Runnable init(int pos){
                _pos = pos;
                return this;
            }
        }.init(pos));
        thread.start();
        while(thread.isAlive()){
            //waiting for internet thread
        }
    }
    private void executeHttpPost3(String path, int pos){
        try{
            URL urlObj = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset","UTF-8");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.connect();

            int responseCode = conn.getResponseCode();

            String name = "";
            String sex = "";
            String weight = "";
            String height = "";
            String age = "";

            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = bufferedReader.readLine();
                JSONArray dataJson = new JSONArray(line);
                int l = dataJson.length();
                int count = pos;
                JSONObject info = dataJson.getJSONObject(count);
                name = info.getString("name");
                sex = info.getString("sex");
                weight = info.getString("weight");
                height = info.getString("height");
                age = info.getString("age");
                //create.setText(Double.toString(Double.parseDouble(precision.format(_bmr))));
                //Log.d("data :", name+" "+sex+" "+weight+" "+height+" "+age);

                inputStream.close();
            }

            conn.disconnect();

            String path2 = "http://10.0.2.2/bmr/delete.php";

            URL urlObj2 = new URL(path2);
            HttpURLConnection conn2 = (HttpURLConnection) urlObj2.openConnection();
            conn2.setDoOutput(true);
            conn2.setRequestMethod("POST");
            conn2.setRequestProperty("Accept-Charset","UTF-8");
            conn2.setReadTimeout(10000);
            conn2.setConnectTimeout(15000);
            conn2.connect();

            DataOutputStream wr = new DataOutputStream(conn2.getOutputStream());
            //id=parameter
            String post = "name="+name+"&age="+age+"&weight="+weight+"&height="+height+"&sex="+sex;
            wr.writeBytes(post);
            //Log.d("output:",var);
            wr.flush();
            wr.close();

            InputStream in = new BufferedInputStream(conn2.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                result.append(line);
            }

            //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);
            //listview.setAdapter(adapter);


            conn2.disconnect();
            //Log.d("tag",post);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private void initViewElement() {
        create = (Button) findViewById(R.id.create);
        //modify = (Button) findViewById(R.id.modify);
        //delete = (Button) findViewById(R.id.delete);
        listView =(ListView) findViewById(R.id.listView);
        //editPassword=(EditText) findViewById(R.id.editPassword);

    }

}