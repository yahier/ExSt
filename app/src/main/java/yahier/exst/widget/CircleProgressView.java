package yahier.exst.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.stbl.stbl.R;

/**
 * Created by Administrator on 2016/9/28.
 */

public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressBar";

    private int mMaxProgress = 100;

    private int mProgress = 0;
    //如果需要更改此值，请使用setStokeWidth方法
    private  int mCircleLineStrokeWidth;


    // 画圆所在的距形区域
    private final RectF mRectF;

    private final Paint mPaint;
    private  Paint mPaintGray = null;
    private  Paint mPaintYellow = null;

    private final Context mContext;


    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();
        init();
    }

    void init(){
        //以下距离不可更改。
       // mCircleLineStrokeWidth = (int)mContext.getResources().getDimension(R.dimen.dp_5);
        mPaintGray = new Paint();
        mPaintGray.setAntiAlias(true);
        mPaintGray.setColor(mContext.getResources().getColor(R.color.hongbao_round2_bg));
        mPaintGray.setStyle(Paint.Style.STROKE);

        mPaintYellow = new Paint();
        mPaintYellow.setAntiAlias(true);
        mPaintYellow.setStyle(Paint.Style.STROKE);
        mPaintYellow.setColor(mContext.getResources().getColor(R.color.yellow));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // 设置画笔相关属性
        //mPaint.setAntiAlias(true);
        //mPaint.setColor(mContext.getResources().getColor(R.color.hongbao_round2_bg));
        canvas.drawColor(Color.TRANSPARENT);
        //mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        //mPaint.setStyle(Paint.Style.STROKE);

        mPaintGray.setStrokeWidth(mCircleLineStrokeWidth);
        mPaintYellow.setStrokeWidth(mCircleLineStrokeWidth);
        // 位置
        mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
        mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
        mRectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
        mRectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y

        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, -90, 360, false, mPaintGray);

        canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaintYellow);


    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        this.invalidate();
    }

    public int getProgress(){
        return this.mProgress;
    }

    public void setStrokeWidth(int strokeWidth){
        this.mCircleLineStrokeWidth = strokeWidth;
        this.invalidate();
    }



}
