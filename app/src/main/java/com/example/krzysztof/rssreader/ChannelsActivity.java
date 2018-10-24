package com.example.krzysztof.rssreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChannelsActivity extends AppCompatActivity {


    Button bSelect;
    Button bAdd;
    Button bDelete;
    LinearLayout channelsLayout;


    ArrayList<TextView> myChannels;
    ArrayList<Channel> myChannelList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String selectedChannelTitle="";
    final int CHANNEL_ADD=2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);

        initComponents();

        channelsToTextViewList();


        showMyChannels();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== CHANNEL_ADD && resultCode== RESULT_OK)
        {
            channelListFromGson();
            channelsLayout.removeAllViews();
            channelsToTextViewList();
            showMyChannels();
            Toast.makeText(ChannelsActivity.this, "Dodano nowy kanał", Toast.LENGTH_SHORT).show();
        }
    }

    public void channelsToTextViewList()
    {
        myChannels= new ArrayList<>();

        int numbOfChannels= myChannelList.size();
        String title="";

        for(int i=0; i<numbOfChannels; i++)
        {
            title= myChannelList.get(i).getTitle();

            TextView tv= new TextView(this);
            tv.setPadding(40,10,10,10);
            tv.setTextColor(Color.BLACK);
            tv.setText(title);
            tv.setTextSize(25);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tv.isClickable();
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView obj=  (TextView) view;

                    for(TextView item: myChannels)
                        item.setTextColor(Color.BLACK);

                    obj.setTextColor(Color.GREEN);
                    selectedChannelTitle= obj.getText().toString();
                }
            });
            myChannels.add(tv);
        }
    }

    private void showMyChannels()
    {
        channelsLayout.removeAllViews();

        for(TextView item: myChannels)
        {
            channelsLayout.addView(item);
        }
    }

    public void initComponents()
    {
        bSelect= findViewById(R.id.bSelect);
        bSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent();

                int i= myChannelList.size();

                for(int j=0; j<i;j++)
                {
                    if(myChannelList.get(j).getTitle()== selectedChannelTitle)
                    {
                        String url= myChannelList.get(j).getUrl();
                        intent.putExtra("newURL", url);
                        setResult(RESULT_OK, intent);
                        ChannelsActivity.this.finish();
                    }
                }
            }
        });

        bAdd= findViewById(R.id.bAdd);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(ChannelsActivity.this, com.example.krzysztof.rssreader.AddChannelActivity.class);
                startActivityForResult(intent,CHANNEL_ADD);

            }
        });

        bDelete= findViewById(R.id.bDelete);
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(Channel item: myChannelList)
                {
                    if(item.getTitle()== selectedChannelTitle)
                    {
                        myChannelList.remove(item);
                        Gson gson= new Gson();
                        String newGson= gson.toJson(myChannelList);
                        editor.putString("channels", newGson);
                        editor.commit();
                        channelsLayout.removeAllViews();
                        channelsToTextViewList();
                        showMyChannels();
                        Toast.makeText(ChannelsActivity.this, "Kanał został usunięty", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        sharedPreferences=  getSharedPreferences("com.example.krzysztof.rssreader", Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();
        channelsLayout= findViewById(R.id.channelsLayout);
        myChannels= new ArrayList<>();

        channelListFromGson();
    }



    public void channelListFromGson()
    {
        myChannels= new ArrayList<>();
        Gson gson= new Gson();
        String gsonChannelsList= sharedPreferences.getString("channels", "");
        Type typeOfList = new TypeToken<List<Channel>>() {}.getType();
        myChannelList= gson.fromJson(gsonChannelsList, typeOfList);
    }
}
