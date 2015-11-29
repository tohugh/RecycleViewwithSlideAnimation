package com.hugh.recycleviewwithslideanimation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hugh.recycleviewwithslideanimation.ui.widgets.SlideMenu;
import com.hugh.recycleviewwithslideanimation.utils.Constant;
import com.hugh.recycleviewwithslideanimation.utils.MyLineatLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {


    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.main_recycleview)
    RecyclerView mainRecycleview;
    @Bind(R.id.myLayout)
    MyLineatLayout myLayout;
    @Bind(R.id.slideMenu)
    SlideMenu slideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        initRecycleView();


    }

    private void initView() {
        myLayout.setSlideMenu(slideMenu);
        slideMenu.setOnDragStateChangeListener(new SlideMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
            }

            @Override
            public void onClose() {
                ViewPropertyAnimator.animate(ivHead).translationXBy(20)
                        .setInterpolator(new CycleInterpolator(10))
                        .setDuration(500)
                        .start();
            }

            @Override
            public void onDragging(float fraction) {
                ViewHelper.setAlpha(ivHead, 1 - fraction);
            }
        });
    }

    private void initRecycleView() {
        //创建默认的线性layoutmanager
        mainRecycleview.setLayoutManager(new GridLayoutManager(this, 3));
        //设置Adapter
        mainRecycleview.setAdapter(new MyAdapter(this));
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.NormalTextViewHolder> {


        private LayoutInflater mLayoutInflater;
        private Context context;


        public MyAdapter(Context context) {
            this.context = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        //创建新view。被layoutmanager调用
        @Override
        public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_text, parent,
                    false));
        }

        //给界面绑定数据
        @Override
        public void onBindViewHolder(NormalTextViewHolder holder, int position) {
            holder.textView.setText(Constant.NAMES[position]);
        }


        //返回数据的数量
        @Override
        public int getItemCount() {
            return Constant.NAMES.length;
        }


        //自定义的viewholder，拥有每个item的元素
        public class NormalTextViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.text_view)
            TextView textView;

            public NormalTextViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, textView.getText(), Toast.LENGTH_SHORT).show();

                    }
                });
            }

        }

    }
}
