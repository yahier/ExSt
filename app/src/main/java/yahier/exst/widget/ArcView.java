package yahier.exst.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
/**
 * Created by Administrator on 2016/9/27.
 * 此类有Bug,圆形弧线还是用CircleProgressView类
 */

public class ArcView extends View {

    public ArcView(Context context) {
        super(context);
        init();
    }

    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private Paint  mPaint;



    private RectF mRectF;

    public void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
    }


    int progress = 360;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //圆弧的外轮廓矩形区域
        mRectF = new RectF((float) (0), (float) (0), (float) (180), (float) (180));
        // 画弧线
        canvas.drawArc(mRectF, 0, progress, false, mPaint);

    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }


}