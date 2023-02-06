package com.jannat.lionsave;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {

    Bitmap background, ground, lion;
    Rect rectBackground, rectGround;
    Context context;
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint texPaint = new Paint();
    Paint healthPaint = new Paint();
    float TEXT_SIZE = 120;
    int points = 0;
    int life = 3;
    static int dWidth,dHeight;
    Random random;
    float lionX,lionY;
    float oldX;
    float oldLionX;
    ArrayList<Spiker> spikers;
    ArrayList<Explosion> explosions;



    public GameView(Context context) {
        super(context);
        this.context = context;
        background = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        ground = BitmapFactory.decodeResource(getResources(),R.drawable.ground);
        lion = BitmapFactory.decodeResource(getResources(),R.drawable.lion);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        rectBackground = new Rect(0,0,dWidth,dHeight);
        rectGround = new Rect(0,dHeight - ground.getHeight(),dWidth,dHeight);
        handler = new android.os.Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        texPaint.setColor(Color.rgb(255,166,0));
        texPaint.setTextSize(TEXT_SIZE);
        texPaint.setTextAlign(Paint.Align.LEFT);
      //  texPaint.setTypeface(ResourcesCompat.getFont(context,R.font.i))
        healthPaint.setColor(Color.GREEN);
        random = new Random();
        lionX = dWidth /2 - lion.getWidth() / 2;
        lionY = dHeight - ground.getHeight() - lion.getHeight();
        spikers = new ArrayList<>();
        explosions = new ArrayList<>();
        for (int i =0; i<3; i++){
            Spiker spiker = new Spiker(context);
            spikers.add(spiker);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);
        canvas.drawBitmap(lion, lionX, lionY, null);
        for (int i = 0; i < spikers.size(); i++) {
            canvas.drawBitmap(spikers.get(i).getSpike(spikers.get(i).spikeFrame), spikers.get(i).spikeX, spikers.get(i).spikeY, null);
            spikers.get(i).spikeFrame++;
            if (spikers.get(i).spikeFrame > 2) {
                spikers.get(i).spikeFrame = 0;
            }
            spikers.get(i).spikeY += spikers.get(i).spikeVelocity;
            if (spikers.get(i).spikeY + spikers.get(i).getSpikeWidth() >= dHeight - ground.getHeight()) {
                points += 10;
                Explosion explosion = new Explosion(context);
                explosion.explosionX = spikers.get(i).spikeX;
                explosion.explosionY = spikers.get(i).spikeY;
                explosions.add(explosion);
                spikers.get(i).resetPosition();
            }

        }
        for (int i = 0; i < spikers.size(); i++) {
            if (spikers.get(i).spikeX + spikers.get(i).getSpikeWidth() >= lionX
                    && spikers.get(i).spikeX <= lionX + lion.getWidth()
                    && spikers.get(i).spikeY + spikers.get(i).getSpikeWidth() >= lionY
                    && spikers.get(i).spikeY + spikers.get(i).getSpikeWidth() <= lionY + lion.getHeight()) {
                life--;
                spikers.get(i).resetPosition();
                if (life == 0) {
                    Intent intent = new Intent(context, GameOver.class);
                    intent.putExtra("points", points);
                    context.startActivity(intent);
                    ((Activity) context).finish();

                }
            }
        }
        for (int i = 0; i<explosions.size();i++){
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame),explosions.get(i).explosionX,
                 explosions.get(i).explosionY,null);
            explosions.get(i).explosionFrame++;
            if (explosions.get(i).explosionFrame > 3){
                explosions.remove(i);
            }
        }
        if (life == 2){
            healthPaint.setColor(Color.YELLOW);
        }
        else if (life == 1){
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth - 200, 30, dWidth-200+60*life,80,healthPaint);
        canvas.drawText(""+points,20,TEXT_SIZE,texPaint);
        handler.postDelayed(runnable,UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= lionY){
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN){
                oldX = event.getX();
                oldLionX = lionX;
            }
            if (action == MotionEvent.ACTION_MOVE){
                float shift = oldX - touchX;
                float newLionX = oldLionX - shift;
                if (newLionX <= 0)
                    lionX = 0;
                else if (newLionX >= dWidth - lion.getWidth())
                    lionX = dWidth - lion.getWidth();
                else
                    lionX = newLionX;
            }
        }
        return true;
    }
}

