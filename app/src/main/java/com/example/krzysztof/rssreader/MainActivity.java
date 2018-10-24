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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    LinearLayout newsList;

    ArrayList<Feed> feedList= new ArrayList<>();

    ArrayList<String> titlesList= new ArrayList<>();
    ArrayList<String> descriptionsList= new ArrayList<>();
    ArrayList<String> dataList= new ArrayList<>();
    ArrayList<String> linksList= new ArrayList<>();

    String url;
    Document doc ;
    Elements titles;
    Elements descriptions;
    Elements links;
    Elements datas;
    TextView errorText;
    TextView sourceText;
    ScrollView scrollView;
    Button myRSS;

    Button refresh;

    final int CHANNEL_ACTIVITY= 2;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;
    Channels myChannels;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url= "http://www.rmf24.pl/fakty/polska/feed";

        initComponents();

        makeConnection(null);

        addStandardChannels();
    }




    public void initComponents()
    {
        newsList= findViewById(R.id.contentLayout);

        sourceText= findViewById(R.id.MarqueeText);
        scrollView= findViewById(R.id.scrollView);
        refresh= findViewById(R.id.button3);

        myRSS= findViewById(R.id.bMyChannels);
        myRSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, com.example.krzysztof.rssreader.ChannelsActivity.class);
                startActivityForResult(intent, CHANNEL_ACTIVITY);
            }
        });

        errorText= new TextView(this);
        errorText.setText("Błąd wczytywania danych!\nBrak połączenia z internetem lub nieprawidłowy adres URL.");
        errorText.setTextSize(20);
        errorText.setTextColor(Color.LTGRAY);
        errorText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        errorText.setPadding(0,100,0,0);


        sharedPreferences= getSharedPreferences("com.example.krzysztof.rssreader", Context.MODE_PRIVATE);
        spEditor= sharedPreferences.edit();
        myChannels= new Channels();


    }

    public void addStandardChannels()
    {


        Gson gson= new Gson();
        String newGson= sharedPreferences.getString("channels", "");
        ArrayList<Channel> list= new ArrayList<>();
        Type typeOfList = new TypeToken<List<Channel>>() {}.getType();
        list= gson.fromJson(newGson, typeOfList);



        try
        {
            for(Channel item: list)
            {
                myChannels.channelsList.add(item);

            }
        }
        catch(Exception ex)
        {
            myChannels.channelsList.add(new Channel("rmf24", "http://www.rmf24.pl/fakty/polska/feed"));
            myChannels.channelsList.add(new Channel("interia", "http://kanaly.rss.interia.pl/kraj.xml"));
        }




        String gsonChannels= gson.toJson(myChannels.channelsList);

        spEditor.putString("channels", gsonChannels);
        spEditor.commit();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== CHANNEL_ACTIVITY && resultCode== RESULT_OK)
        {
            url= data.getStringExtra("newURL");
            makeConnection(null);
        }
        if(resultCode== RESULT_CANCELED)
        {
            Toast.makeText(MainActivity.this, "Nie wybrano żadnago kanału", Toast.LENGTH_SHORT).show();
        }

    }


    public void makeConnection(View myView)
    {
        refresh.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    cleanAncillaryObjects();

                    doc = Jsoup.connect(url).get();
                    titles = doc.getElementsByTag("item").select("title");
                    descriptions = doc.getElementsByTag("item").select("description");
                    links = doc.getElementsByTag("item").select("link");
                    datas = doc.getElementsByTag("item").select("pubDate");

                    if(titles != null)
                    {
                        createTitleList(titles);
                        createDescriptionList(descriptions);
                        createDataList(datas);
                        createLinkList(links);
                    }

                    if(titles != null)
                        createFeeds();

                } catch (IOException e) { }
                catch(IllegalArgumentException e){}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                        sourceText.setText(url);
                        showFeeds();
                        refresh.setEnabled(true);

                    }
                });
            }
        }).start();
    }


    public void cleanAncillaryObjects()
    {
        doc=null;
        descriptions=null;
        links= null;
        datas= null;
        titles= null;
        titlesList= new ArrayList<>();
        linksList=new ArrayList<>();
        dataList= new ArrayList<>();
        descriptionsList=new ArrayList<>();
    }

    public void createFeeds()
    {
        for(int i=0; i< titles.size()-1;i++)
        {
            feedList.add(new Feed(titlesList.get(i), descriptionsList.get(i), dataList.get(i), linksList.get(i), this));
        }
    }


    public void showFeeds()
    {
        newsList.removeAllViews();

        for(Feed feedItem: feedList)
        {
            newsList.addView(feedItem.getTitleTextView());
            newsList.addView(feedItem.getDescriptionTextView());
            newsList.addView(feedItem.getDateTextView());
            newsList.addView(feedItem.getLinkTextView());
        }

        if(feedList.size() == 0)
        {
            newsList.addView(errorText);
        }

        feedList= new ArrayList<>();
    }


    public void createTitleList(Elements titles)
    {
        for(Element el: titles)
        {
            String str= el.toString();
            String pattern= "<title>";
            int ind= str.indexOf(pattern);
            str= str.substring(ind+pattern.length(), str.indexOf("</title>"));
            ind= str.indexOf("CDATA[");
            if(ind >0)
                str= str.substring(ind+6, str.length()-3);
            titlesList.add(str);
        }
    }

    public void createDescriptionList(Elements descriptions)
    {
        for(Element el: descriptions)
        {
            String str= el.text();
            String pattern = "</a>";
            int ind= str.indexOf(pattern);
            try
            {
                descriptionsList.add(str.substring(ind+pattern.length(), str.indexOf("</p>")).replaceAll("&quot;", ""));
            }catch(Exception ex){
                descriptionsList.add(el.text());
            }
        }
    }

    public void createDataList(Elements datas)
    {
        for(Element el: datas)
        {
            String str= el.toString();
            String pattern= "<pubDate>";
            int ind= str.indexOf(pattern);
            str= str.substring(ind+pattern.length(), str.indexOf("</pubDate>"));
            dataList.add(str);
        }
    }

    public void createLinkList(Elements links)
    {
        for(Element el: links)
        {
            String str= el.toString();
            String pattern= "http";
            int ind= str.indexOf(pattern);
            str= str.substring(ind, str.indexOf("</link>"));
            linksList.add(str);
        }
    }
}
