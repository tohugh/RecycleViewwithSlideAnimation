package com.hugh.recycleviewwithslideanimation.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hugh on 2015/11/30.
 * 快速检索
 *
 *
 */
// TODO: 2015/11/30 -------paint和canvas------
public class QuickIndexBar extends View {

    private String[] indexArr = { "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z" };
    private int width;
    private float cellHeight;
    private Paint paint;

    public QuickIndexBar(Context context) {
        super(context);
        init();
    }


    public QuickIndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /*
    * 初始化画笔
    * */
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(16);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = getMeasuredWidth();
        cellHeight = getHeight()*1f/ indexArr.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i =0;i<indexArr.length;i++){
            float x = width/2;
            //所占位置的一半+字体的高度的一半+（位置）
            float y = cellHeight/2+getTextHeight(indexArr[i])+i*cellHeight;

            paint.setColor(i!=lastIndex?Color.WHITE:Color.GRAY);
            canvas.drawText(indexArr[i],x,y,paint);
        }

    }


    private int lastIndex = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int y = (int) event.getY();
                int index = (int) (y/cellHeight);
                if (index!=lastIndex){
                    if (index>=0&&index<indexArr.length){
                        if (onLetterChangeListener!=null){
                            onLetterChangeListener.onLetterChange(indexArr[index]);
                        }
                    }
                    lastIndex = index;

                }
                break;
            case MotionEvent.ACTION_UP:
                lastIndex = -1;
                break;
        }
        invalidate();//调用ondraw()//，requestLayout()调用onmeasure()和onlayout()
        return true;
    }

    /*
    * 获取文本高度
    * */
    private int getTextHeight(String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        return rect.height();
    }

    private OnLetterChangeListener onLetterChangeListener;
    public interface OnLetterChangeListener{
        void onLetterChange(String letter);
    }
    public void setOnLetterChangeListener(OnLetterChangeListener onLetterChangeListener){
        this.onLetterChangeListener = onLetterChangeListener;
    }
}
