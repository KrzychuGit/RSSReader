package com.example.krzysztof.rssreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddChannelActivity extends AppCompatActivity {

    Button bOKChannel;
    EditText etNewTitle, etNewURL;

    ArrayList<Channel> myChannelList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_channel);

        initComponents();
    }

    public void initComponents()
    {
        sharedPreferences= getSharedPreferences("com.example.krzysztof.rssreader", Context.MODE_PRIVATE);
        spEditor= sharedPreferences.edit();
        gson= new Gson();

        channelListFromGson();

        etNewTitle= findViewById(R.id.etNewTitle);
        etNewURL= findViewById(R.id.etNewURL);
        bOKChannel= findViewById(R.id.bOKChannel);
        bOKChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nTitle= etNewTitle.getText().toString();
                String nURL= etNewURL.getText().toString();

                if( nTitle != "" && nURL != "")
                {
                    myChannelList.add(new Channel(nTitle, nURL));

                    String newGson= gson.toJson(myChannelList);
                    spEditor.putString("channels", newGson);
                    spEditor.commit();
                    Intent intent= new Intent();
                    setResult(RESULT_OK, intent);
                    AddChannelActivity.this.finish();
                }
                else
                {
                    Toast.makeText(AddChannelActivity.this, "Wprowadź wszystkie dane!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void channelListFromGson()
    {
        String gsonChannelsList= sharedPreferences.getString("channels", "");
        Type typeOfList = new TypeToken<List<Channel>>() {}.getType();
        myChannelList= gson.fromJson(gsonChannelsList, typeOfList);
    }
}
