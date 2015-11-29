package com.hugh.recycleviewwithslideanimation.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.hugh.recycleviewwithslideanimation.ui.widgets.SlideMenu;
import com.hugh.recycleviewwithslideanimation.ui.widgets.SlideMenu.DragState;


/**
 * Created by hugh on 2015/11/29.
 */
public class MyLineatLayout extends LinearLayout {

    public MyLineatLayout(Context context) {
        super(context);
    }

    public MyLineatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu slideMenu;
    public void setSlideMenu(SlideMenu slideMenu) {
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isSlideMenuOpen()){
            if(event.getAction()==MotionEvent.ACTION_UP){
                //按下的时候需要关闭SlideMenu
                slideMenu.close();
            }

            //需要消费掉事件
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isSlideMenuOpen()){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isSlideMenuOpen() {
        return slideMenu!=null&&slideMenu.getCurrnetState() == DragState.Open;
    }
}
