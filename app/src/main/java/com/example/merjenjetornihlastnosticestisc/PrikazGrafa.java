package com.example.merjenjetornihlastnosticestisc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class PrikazGrafa extends View {

    Paint paintGreen = new Paint();
    Rect rect = new Rect();
    // Definiranje parametrov barve in osnovne oblike prikaza grafa
    Bitmap posnetekAvta = BitmapFactory.decodeResource(getResources(), R.drawable.posnetek_avta);
    int sirinaSlikeAvta = 290;
    int visinaSlikeAvta = 382;
    Bitmap resized = Bitmap.createScaledBitmap(posnetekAvta, sirinaSlikeAvta, visinaSlikeAvta, true);
    // Uvoz in definiranje dizmenij prikaza slike avtomobila za boljšo uporabniško izkušnjo


    public PrikazGrafa(Context context) {
        super(context);
        init(null);
    }

    public PrikazGrafa(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PrikazGrafa(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){

    }

    @Override
    protected void onDraw(Canvas canvas){

        final int height = getHeight();
        final int width = getWidth();
        paintGreen.setColor(Color.GREEN);
        int pozicijaSlikeLevo = width/2 - sirinaSlikeAvta/2;
        int pozicijaSlikeTop = height/2 - visinaSlikeAvta/2;

        rect.left = width/2 -50;
        rect.right = rect.left + 100;
        rect.top = rect.bottom - (int)((height/10)* LogikaMedVoznjo.rezultantaPospeska);
        rect.bottom = pozicijaSlikeTop;

        canvas.drawRect(rect, paintGreen);

        canvas.drawBitmap(resized, pozicijaSlikeLevo, pozicijaSlikeTop, null);
        invalidate();  //poskrbi da se canvas osvezuje
        // Dejanski prikaz grafa in slike avtomobila, ter osveževanje teh
    }
}
