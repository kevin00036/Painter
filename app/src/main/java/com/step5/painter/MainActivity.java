package com.step5.painter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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

    public void selectSelect(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_SELECT);
        mv.select_selected = false;
    }

    public void clickSave(View view)
    {
        mv.saveBitmap();
    }
}

class myView extends View
{
    final int MODE_PENCIL = 0;
    final int MODE_LINE = 1;
    final int MODE_RECTANGLE = 2;
    final int MODE_CIRCLE = 3;

    final int MODE_FILL = 10;
    final int MODE_SELECT = 11;

    final int MAX_UNDO = 10;

    final float SELECT_TOL = 50.0f;

    Bitmap currentBitmap;
    Bitmap bufferBitmap;
    Deque<Bitmap> undoQueue;

    Canvas currentCanvas;
    int Width;
    int Height;
    boolean resized = false;
    int mode = MODE_PENCIL;
    boolean select_selected;

    int currentColor = Color.BLACK;

    float lastX, lastY;
    float selectSX, selectSY, selectTX, selectTY;
    Path pencilPath;


    public void init()
    {
        Log.d("Debug", "INITTTTTTT!!!");
        measure(Width, Height);
        Width = 1000;
        Height = 700;
        undoQueue = new ArrayDeque<>();
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
        clearCanvas(currentCanvas);
        mode = m;
        invalidate();
    }

    public void setColor(int color)
    {
        currentColor = color;
    }

    void clearCanvas(Canvas cv)
    {
        cv.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    public void saveBitmap()
    {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        String filename = "Image_" + sdf.format(date) + ".png";

        MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                currentBitmap, filename, "Step5 Painter");

        Toast toast = Toast.makeText(getContext(), "Image saved : " + filename, Toast.LENGTH_LONG);
        toast.show();
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

                currentCanvas.drawBitmap(currentBitmap, 0, 0, null);
                floodFill_array(bufferBitmap, new Point(cx, cy), origColor, currentColor);
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                deBuffer();
            }
        }
        else if(mode == MODE_SELECT)
        {
            Paint paint2 = new Paint();
            paint2.setStrokeWidth(3);
            paint2.setStyle(Paint.Style.STROKE);
            float itvl[] = {10.0f, 10.0f};

            if(select_selected && act == MotionEvent.ACTION_DOWN)
            {
                if(selectSX - x > SELECT_TOL || x - selectTX > SELECT_TOL ||
                   selectSY - y > SELECT_TOL || y - selectTY > SELECT_TOL)
                    select_selected = false;
            }

            if(!select_selected)
            {
                if (act == MotionEvent.ACTION_DOWN) {
                    selectSX = selectTX = x;
                    selectSY = selectTY = y;
                } else if (act == MotionEvent.ACTION_MOVE) {
                    selectTX = x;
                    selectTY = y;
                    clearCanvas(currentCanvas);
                    paint2.setColor(Color.BLACK);
                    paint2.setPathEffect(new DashPathEffect(itvl, 0.0f));
                    currentCanvas.drawRect(
                            selectSX, selectSY, selectTX, selectTY, paint2);
                    paint2.setColor(Color.WHITE);
                    paint2.setPathEffect(new DashPathEffect(itvl, 10.0f));
                    currentCanvas.drawRect(
                            selectSX, selectSY, selectTX, selectTY, paint2);
                } else if (act == MotionEvent.ACTION_UP) {
                    if(selectSX > selectTX) {
                        float tmp = selectSX;
                        selectSX = selectTX;
                        selectTX = tmp;
                    }
                    if(selectSY > selectTY) {
                        float tmp = selectSY;
                        selectSY = selectTY;
                        selectTY = tmp;
                    }
                    select_selected = true;
                }
            }
            else
            {
                if (act == MotionEvent.ACTION_DOWN) {
                    selectSX = selectTX = x;
                    selectSY = selectTY = y;
                } else if (act == MotionEvent.ACTION_MOVE) {
                    clearCanvas(currentCanvas);
                    currentCanvas.drawRect(lastX, lastY, x, y, paint);
                } else if (act == MotionEvent.ACTION_UP) {
                }
            }
        }

        Log.d("Debug", event.toString());

        invalidate();

        return true;
    }

    private void floodFill_array(Bitmap bmp, Point pt, int targetColor, int replacementColor)
    {
        if(targetColor == replacementColor)
            return;

        int width, height;
        int[] arrPixels;

        width = bmp.getWidth();
        height = bmp.getHeight();

        arrPixels = new int[width*height];
        bmp.getPixels(arrPixels, 0, width, 0, 0, width, height);

        Queue<Point> q = new LinkedList<>();
        q.add(pt);

        while (q.size() > 0) {
            Point n = q.poll();
            if (arrPixels[width*n.y + n.x] != targetColor)
                continue;

            Point w = n, e = new Point(n.x + 1, n.y);
            while ((w.x > 0) && (arrPixels[width*w.y + w.x] == targetColor)) {

                arrPixels[width*w.y + w.x] = replacementColor;  // setPixel

                if ((w.y > 0) && (arrPixels[width*(w.y-1) + w.x] == targetColor))
                    q.add(new Point(w.x, w.y - 1));
                if ((w.y < height - 1)
                        && (arrPixels[width*(w.y+1) + w.x] == targetColor))
                    q.add(new Point(w.x, w.y + 1));
                w.x--;
            }

            while ((e.x < width - 1)
                    && (arrPixels[width*e.y + e.x] == targetColor)) {

                arrPixels[width*e.y + e.x] = replacementColor;  // setPixel

                if ((e.y > 0) && (arrPixels[width*(e.y-1) + e.x] == targetColor))
                    q.add(new Point(e.x, e.y - 1));
                if ((e.y < height - 1)
                        && (arrPixels[width*(e.y+1) + e.x] == targetColor))
                    q.add(new Point(e.x, e.y + 1));
                e.x++;
            }
        }

        bmp.setPixels(arrPixels, 0, width, 0, 0, width, height);
    }
}

