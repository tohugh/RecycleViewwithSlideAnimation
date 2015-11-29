package com.hugh.recycleviewwithslideanimation.ui.widgets;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by hugh on 2015/11/28.
 */
public class SlideMenu extends FrameLayout {

    private ViewDragHelper viewDragHelper;
    private View mainView;
    private View menuView;
    private int width;
    private int menuWidth;
    private int mainWidth;
    private int dragRange;
    private FloatEvaluator floatEvaluator;//浮点计算器
    private DragState mState = SlideMenu.DragState.Close;//默认关闭

    public DragState getCurrnetState() {
        return mState;
    }
    public enum DragState{
        Open,Close
    }


    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //1.创建一个ViewDragHelper
        viewDragHelper = ViewDragHelper.create(this, callback);
        floatEvaluator = new FloatEvaluator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    /*
    * 当xml布局文件被读取完之后被调用的方法
    * 通常用来获取子view
    * */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount()!=2){
            throw  new RuntimeException("only 2 childView can exist!");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //如果要让viewDragHelper处理拖拽需要让ViewDragHelper处理触摸事件
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    /*
    * onmeasure执行完之后执行
    *
    * */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取slidemenu的宽
        width = getMeasuredWidth();
        //分别获取menuView和mainView的宽
        menuWidth = menuView.getMeasuredWidth();
        mainWidth = mainView.getMeasuredWidth();

        //设定mainView可以被拖拽的宽度
        dragRange = (int)(width*0.6f);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback(){


        /*
        * 是否需要开始捕获触摸事件
        * return true表示要捕获
        * 决定哪个子view可以拖动
        * */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mainView||child == menuView ;
        }


        /*
        * 捕获到了view
        * */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }



        /*
        * 获取水平方向的拖拽范围
        * */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragRange;
        }


        /*
        * 获取竖直方向的拖拽范围
        * */
        @Override
        public int getViewVerticalDragRange(View child) {
            return super.getViewVerticalDragRange(child);
        }

        /*
        * 控制水平方向的移动
        * left=child.getLeft()+dx
        * return的时child要确定的值
        * */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(child==mainView){
                if(left<0){
                    left = 0;//限制左边
                }else if (left>dragRange) {
                    left = (int) dragRange;//限制右边
                }
            }
            return left;
        }


        /*
        * 控制竖直方向的移动
        * */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return super.clampViewPositionVertical(child, top, dy);
        }


        /*
        * view位置改变时回调，作伴随移动的逻辑
        *
        * */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == menuView){
                menuView.layout(0,0,menuWidth,menuView.getMeasuredHeight());
                int newleft = mainView.getLeft() + dx;
                if (newleft<0)newleft = 0;
                if (newleft>dragRange)newleft = dragRange;

                mainView.layout(newleft, mainView.getTop(), newleft + mainWidth, mainView.getBottom());
            }

            float fraction = mainView.getLeft() * 1f / dragRange;
            excuteAnim(fraction);

            if(fraction==0f && mState!= SlideMenu.DragState.Close){
                mState = DragState.Close;
                if(listener!=null){
                    listener.onClose();
                }
            }else if (fraction==1f && mState!= SlideMenu.DragState.Open) {
                mState =DragState.Open;
                if(listener!=null){
                    listener.onOpen();
                }
            }

            //回调onDragging方法
            if(listener!=null){
                listener.onDragging(fraction);
            }
        }


        /*
        * 手指抬起时促发的方法，一般实现平移
        * */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (mainView.getLeft()<dragRange/2){
                close();
            }else{
                open();
            }

            if (xvel>100&&mState== DragState.Close){
                open();
            }
            if (xvel<-100&&mState== DragState.Open){
                close();
            }


        }
    };

    private void open() {
        viewDragHelper.smoothSlideViewTo(mainView,dragRange,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    public void close() {
        viewDragHelper.smoothSlideViewTo(mainView,0,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    private void excuteAnim(float fraction) {
        // return startFloat + fraction * (endValue.floatValue() - startFloat);
        ViewHelper.setScaleX(mainView,floatEvaluator.evaluate(fraction, 1f, 0.8f));
        ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));

        ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
        ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
        ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(fraction, 0.3f, 0.7f));
        ViewHelper.setTranslationX(menuView, floatEvaluator.evaluate(fraction, -menuWidth / 2, 0));

    }

    private OnDragStateChangeListener listener;
    public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
        this.listener = listener;
    }
    public interface OnDragStateChangeListener{
        void onOpen();

        void onClose();

        void onDragging(float fraction);
    }

    @Override
    public void computeScroll() {
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }
}
