package com.step5.painter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Debug", "CREATTTTTTTE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(new myView(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonClicked(View view)
    {
        final myView mv = (myView) findViewById(R.id.canvasView);
        mv.drawCircle();
    }
}

class myView extends View
{
    Bitmap currentBitmap;
    Canvas currentCanvas;
    int Width;
    int Height;
    boolean resized = false;

    public void init()
    {
        Log.d("Debug", "INITTTTTTT!!!");
        measure(Width, Height);
        Width = 1000;
        Height = 700;
        //Width = getLayoutParams().width;
        currentBitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
        currentCanvas = new Canvas(currentBitmap);
    }

    public myView(Context context)
    {
        super(context);
        init();
    }

    public myView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public myView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public void drawCircle()
    {
        int x = 400, y = 400;
        Random rnd = new Random();
        int r = rnd.nextInt(300);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#5DCC5C"));
        currentCanvas.drawCircle(x, y, r, paint);
        //canvas.drawARGB(100, 30, 100, 10);
        Log.d("Debug", "" + getDrawingTime());

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        /*if(!resized)
        {
            currentBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            currentCanvas = new Canvas(currentBitmap);
            resized = true;
        }*/
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawBitmap(currentBitmap, 0, 0, null);
        //canvas.setBitmap(currentBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Paint pt = new Paint();
        pt.setARGB(100, 100, 200, 10);
        //currentCanvas.drawPoint(event.getX(), event.getY(), pt);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#CD5C5C"));
        currentCanvas.drawCircle(event.getX(), event.getY(), 10, paint);

        Log.d("Debug", event.getX() + " " + event.toString());

        invalidate();

        return true;
    }

}
