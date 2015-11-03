package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * View for listening to touch event and add stoke to the view
 * Created by Leung Pui Kuen on 18/10/2015.
 */
public class CanvasView extends View {
    Context context;

    List<Path> paths;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        paths = new ArrayList<>();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(5f);
        minX = Integer.MAX_VALUE;
        maxX = 0;
        minY = Integer.MAX_VALUE;
        maxY = 0;
    }



    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        for (Path path : paths) {
            canvas.drawPath(path, mPaint);
        }
    }

    public void clear() {
        paths.clear();
        minX = Integer.MAX_VALUE;
        maxX = 0;
        minY = Integer.MAX_VALUE;
        maxY = 0;
        invalidate();
    }

    public void undo() {
        if (paths.size() == 1) {
            clear();
        } else if (!isEmpty()) {
            paths.remove(paths.size() - 1);
        }
        invalidate();
    }

    public boolean isEmpty() {
        return paths.size() == 0;
    }

    public Bitmap getBitmap() {
        if (!isEmpty() && maxX > minX && maxY > minY) {
            Bitmap bmp = Bitmap.createBitmap((int) (maxX - minX), (int) (maxY - minY), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            canvas.drawColor(Color.WHITE);
            canvas.translate(-minX, -minY);
            for (Path path : paths) {
                canvas.drawPath(path, mPaint);
            }
            canvas.translate(minX, minY);
            return bmp;
        } else {
            return null;
        }
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        minX = Math.min(minX, x);
        maxX = Math.max(maxX, x);
        minY = Math.min(minY, y);
        maxY = Math.max(maxY, y);
        Path path = new Path();
        path.moveTo(x, y);
        paths.add(path);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        minX = Math.min(minX, x);
        maxX = Math.max(maxX, x);
        minY = Math.min(minY, y);
        maxY = Math.max(maxY, y);
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            Path path = paths.get(paths.size() - 1);
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        Path path = paths.get(paths.size() - 1);
        path.lineTo(mX, mY);
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }
}