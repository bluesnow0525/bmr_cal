package com.example.bmrcalculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class MainActivity2 extends AppCompatActivity {
    //TextView toShowInput;
    Button cancel_record;
    Button save_record;
    EditText name,age,height,weight;
    TextView record;
    RadioButton male,female;
    RadioGroup sex;
    String o_sex, o_name, o_age, o_weight, o_height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initViewElement();
        Intent it= this.getIntent();
        if(it !=null) {
            Bundle bundle2 = it.getExtras();
            if(bundle2 !=null) {
                String mode_now = bundle2.getString("mode");
                record.setText(mode_now);
                if(mode_now.equals("Modify Record")){
                    name.setText(bundle2.getString("name"));
                    weight.setText(bundle2.getString("weight"));
                    height.setText(bundle2.getString("height"));
                    age.setText(bundle2.getString("age"));

                    o_name = bundle2.getString("name");
                    o_weight = bundle2.getString("weight");
                    o_height = bundle2.getString("height");
                    o_age = bundle2.getString("age");

                    if(bundle2.getString("sex").equals("Male")){
                        male.setChecked(true);
                        o_sex = bundle2.getString("sex");
                    }
                    else if(bundle2.getString("sex").equals("Female")){
                        female.setChecked(true);
                        o_sex = bundle2.getString("sex");
                    }
                }
            }
        }

        save_record.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("o_name",o_name);
            bundle.putString("o_age",o_age);
            bundle.putString("o_weight",o_weight);
            bundle.putString("o_height",o_height);
            bundle.putString("o_sex",o_sex);
            bundle.putString("mode",record.getText().toString());
            bundle.putString("name", name.getText().toString());
            bundle.putString("age", age.getText().toString());
            bundle.putString("weight", weight.getText().toString());
            bundle.putString("height", height.getText().toString());
            // get gender
            int id = sex.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) sex.findViewById(id);
            String genders = radioButton == null ? "" :radioButton.getText().toString();
            Intent it2 =new Intent();
            bundle.putString("sex", genders);
            it2.putExtras(bundle);
            it2.setClass(MainActivity2.this, MainActivity3.class);
            startActivity(it2);
        });

        cancel_record.setOnClickListener(view -> finish());

    }

    private void initViewElement(){
        cancel_record=(Button) findViewById(R.id.back);
        save_record=(Button) findViewById(R.id.send);
        name=(EditText) findViewById(R.id.name);
        age=(EditText) findViewById(R.id.age);
        weight=(EditText) findViewById(R.id.weight);
        height=(EditText) findViewById(R.id.height);
        sex = (RadioGroup) findViewById(R.id.sex);
        record = (TextView) findViewById(R.id.record);
        female = (RadioButton) findViewById(R.id.female);
        male = (RadioButton) findViewById(R.id.male);
    }
}