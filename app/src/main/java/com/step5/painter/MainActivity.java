package com.step5.painter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class MainActivity extends Activity {

    myView mv;
    BetaDialogFragment apd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Debug", "CREATTTTTTTE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mv = (myView) findViewById(R.id.canvasView);
        apd = new BetaDialogFragment();
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
        mv.drawCircle();
    }

    public void undoClicked(View view)
    {
        mv.undo();
    }

    public void selectTools(View view)
    {
        apd.show(getFragmentManager(), "Beta");
    }

    public void selectPencil(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_PENCIL);
    }
    public void selectLine(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_LINE);
    }
    public void selectRectangle(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_RECTANGLE);
    }
    public void selectCircle(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_CIRCLE);
    }

    public void selectFill(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_FILL);
    }

    public void selectColor(View view)
    {
        AlphaDialogFragment bpd = new AlphaDialogFragment();
        bpd.show(getFragmentManager(), "Alpha");
    }
}

class myView extends View
{
    final int MODE_PENCIL = 0;
    final int MODE_LINE = 1;
    final int MODE_RECTANGLE = 2;
    final int MODE_CIRCLE = 3;

    final int MODE_FILL = 10;

    final int MAX_UNDO = 10;

    Bitmap currentBitmap;
    Bitmap bufferBitmap;
    Deque<Bitmap> undoQueue;

    Canvas currentCanvas;
    int Width;
    int Height;
    boolean resized = false;
    int mode = MODE_PENCIL;

    int currentColor = Color.BLACK;

    float lastX, lastY;
    Path pencilPath;


    public void init()
    {
        Log.d("Debug", "INITTTTTTT!!!");
        measure(Width, Height);
        Width = 1000;
        Height = 700;
        undoQueue = new ArrayDeque<>();
        //Width = getLayoutParams().width;
        //currentBitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
        //bufferBitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
        //currentCanvas = new Canvas(currentBitmap);
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

        deBuffer();
        invalidate();
    }

    void deBuffer()
    {
        undoQueue.addFirst(Bitmap.createBitmap(currentBitmap));
        if(undoQueue.size() > MAX_UNDO)
            undoQueue.removeLast();
        Canvas cv = new Canvas(currentBitmap);
        cv.drawBitmap(bufferBitmap, 0, 0, null);
        clearCanvas(currentCanvas);
    }

    public void undo()
    {
        if(undoQueue.isEmpty()) return;
        Canvas cv = new Canvas(currentBitmap);
        clearCanvas(cv);
        cv.drawBitmap(undoQueue.getFirst(), 0, 0, null);
        undoQueue.removeFirst();
        invalidate();
    }

    public void setMode(int m)
    {
        mode = m;
    }

    public void setColor(int color)
    {
        currentColor = color;
    }

    void clearCanvas(Canvas cv)
    {
        cv.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        if(!resized)
        {
            currentBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            currentCanvas = new Canvas(bufferBitmap);
            Canvas cv = new Canvas(currentBitmap);
            cv.drawColor(Color.WHITE);
            resized = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawBitmap(currentBitmap, 0, 0, null);
        canvas.drawBitmap(bufferBitmap, 0, 0, null);
        //canvas.setBitmap(currentBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();
        int act = event.getAction();

        Paint paint = new Paint();
        //paint.setColor(Color.parseColor("#CD5C5C"));
        paint.setColor(currentColor);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        if(mode == MODE_PENCIL)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                pencilPath = new Path();
                pencilPath.setLastPoint(x, y);
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
                float mx = (x + lastX) / 2;
                float my = (y + lastY) / 2;
                pencilPath.quadTo(lastX, lastY, mx, my);
                clearCanvas(currentCanvas);
                currentCanvas.drawPath(pencilPath, paint);
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                pencilPath.lineTo(x+0.1f, y+0.1f);
                clearCanvas(currentCanvas);
                currentCanvas.drawPath(pencilPath, paint);
                pencilPath = null;
                deBuffer();
            }

            lastX = x;
            lastY = y;
        }
        else if(mode == MODE_LINE)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                lastX = x;
                lastY = y;
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
                clearCanvas(currentCanvas);
                currentCanvas.drawLine(lastX, lastY, x, y, paint);
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                deBuffer();
            }
        }
        else if(mode == MODE_RECTANGLE)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                lastX = x;
                lastY = y;
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
                clearCanvas(currentCanvas);
                currentCanvas.drawRect(lastX, lastY, x, y, paint);
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                deBuffer();
            }
        }
        else if(mode == MODE_CIRCLE)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                lastX = x;
                lastY = y;
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
                clearCanvas(currentCanvas);
                currentCanvas.drawOval(lastX, lastY, x, y, paint);
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                deBuffer();
            }
        }
        else if(mode == MODE_FILL)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                clearCanvas(currentCanvas);
                int cx = (int)x, cy = (int)y;
                int origColor = currentBitmap.getPixel(cx, cy);

                Queue<Point> queue = new ArrayDeque<>();
                queue.add(new Point(cx, cy));

                int dx[] = { 1,-1, 0, 0};
                int dy[] = { 0, 0, 1,-1};

                while(!queue.isEmpty())
                {
                    Point p = queue.element();
                    queue.remove();
                    if(bufferBitmap.getPixel(p.x, p.y) != 0)
                        continue;

                    bufferBitmap.setPixel(p.x, p.y, currentColor);
                    //Log.d("Debug", "Set Pixel " + p.x + "," + p.y + " " + currentColor);

                    for(int i=0; i<4; i++)
                    {
                        int nx = p.x + dx[i], ny = p.y + dy[i];
                        if(currentBitmap.getPixel(nx, ny) == origColor)
                            queue.add(new Point(nx, ny));
                    }
                }
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                deBuffer();
            }
        }

        Log.d("Debug", event.toString());

        invalidate();

        return true;
    }

}

