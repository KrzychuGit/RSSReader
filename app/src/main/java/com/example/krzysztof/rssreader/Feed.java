package com.example.krzysztof.rssreader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;


import java.util.Random;

public class Feed {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private TextView linkTextView;
    private Context context;

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public TextView getDescriptionTextView() {
        return descriptionTextView;
    }

    public void setDescriptionTextView(TextView descriptionTextView) {
        this.descriptionTextView = descriptionTextView;
    }

    public TextView getDateTextView() {
        return dateTextView;
    }

    public void setDateTextView(TextView dateTextView) {
        this.dateTextView = dateTextView;
    }

    public TextView getLinkTextView() {
        return linkTextView;
    }

    public void setLinkTextView(TextView linkTextView) {
        this.linkTextView = linkTextView;
    }



    public Feed(String titleTextView, String descriptionTextView, String dateTextView, final String linkTextView, final Context context) {

        this.context =context;
        Random rnd= new Random();
        int horizontalPadding= 20;

        this.titleTextView= new TextView(context);
        this.titleTextView.setPadding(horizontalPadding,50,horizontalPadding,0);
        this.titleTextView.setTextColor(Color.rgb(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255) ));
        this.titleTextView.setText(titleTextView);
        this.titleTextView.setTextSize(20);
        this.titleTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        this.descriptionTextView= new TextView(context);
        this.descriptionTextView.setTextColor(Color.BLACK);
        this.descriptionTextView.setText(descriptionTextView);
        this.descriptionTextView.setTextSize(12);
        this.descriptionTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        this.descriptionTextView.setPadding(horizontalPadding,0,horizontalPadding,0);


        this.dateTextView= new TextView(context);
        this.dateTextView.setTextColor(Color.rgb(0,0,100));
        this.dateTextView.setText(dateTextView);
        this.dateTextView.setTextSize(12);
        this.dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        this.dateTextView.setPadding(horizontalPadding,0,horizontalPadding,0);


        this.linkTextView= new TextView(context);
        this.linkTextView.setTextColor(Color.GRAY);
        this.linkTextView.setText("Czytaj więcej");
        this.linkTextView.setTextSize(12);
        this.linkTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        this.linkTextView.setPadding(horizontalPadding,0,30,0);
        this.linkTextView.setPaintFlags(this.linkTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        this.linkTextView.setWidth(20);


        this.linkTextView.isClickable();
        this.linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String targetURL= linkTextView;
                Intent browseIntent= new Intent(Intent.ACTION_VIEW, Uri.parse(targetURL));
                context.startActivity(browseIntent);
                //setBaseColorOnLink();
            }
        });

        /*this.linkTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                setRedColorOnLink();
                return false;
            }
        });

        this.linkTextView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                setBaseColorOnLink();
                return false;
            }
        });*/
    }




    private void  setRedColorOnLink()
    {
        linkTextView.setTextColor(Color.RED);
    }

    private void  setBaseColorOnLink()
    {
        linkTextView.setTextColor(Color.GRAY);
    }

}
