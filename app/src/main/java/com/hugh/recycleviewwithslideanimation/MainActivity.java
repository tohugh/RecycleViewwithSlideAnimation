package com.hugh.recycleviewwithslideanimation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hugh.recycleviewwithslideanimation.ui.widgets.QuickIndexBar;
import com.hugh.recycleviewwithslideanimation.ui.widgets.SlideMenu;
import com.hugh.recycleviewwithslideanimation.utils.Friend;
import com.hugh.recycleviewwithslideanimation.utils.MyLineatLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {


    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.main_recycleview)
    RecyclerView mainRecycleview;
    @Bind(R.id.quickIndexBar)
    QuickIndexBar quickIndexBar;
    @Bind(R.id.myLayout)
    MyLineatLayout myLayout;
    @Bind(R.id.slideMenu)
    SlideMenu slideMenu;
    @Bind(R.id.currentWord)
    TextView currentWord;
    private ArrayList<Friend> friends = new ArrayList<Friend>();
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ViewHelper.setScaleX(currentWord, 0);
        ViewHelper.setScaleY(currentWord, 0);

        initView();
        initRecycleView();
        initQuickIndexBar();
        //通过缩小currentWord来隐藏



    }


    private void initQuickIndexBar() {
        quickIndexBar.setOnLetterChangeListener(new QuickIndexBar.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                for (int i = 0; i < friends.size(); i++) {
                    String word = friends.get(i).getPinyin().charAt(0) + "";
                    if (word.equals(letter)) {
                        //listview.setSelection(i);
//                        Log.i("----------------",i+"");
//                        mainRecycleview.scrollToPosition(i);这句话不行，误差很大
                        //==listview.setSelection(i);
                        mLayoutManager.scrollToPositionWithOffset(i,0);
                        break;//找到之后立即中断
                    }
                }

                //显示当前所触摸的字母
                Log.i("++++++++++++++", letter);
                showCurrentWord(letter);
            }
        });

        //填充数据
        fillList();
        //对数据进行排序？？？？？？？？？？？？？？？
        Collections.sort(friends);

    }

    private boolean isAnimaing = false;
    private Handler handler = new Handler();

    /**
     * 显示当前的字母
     *
     * @param letter
     */
    private void showCurrentWord(String letter) {
        currentWord.setText(letter);
//		currentWord.setVisibility(View.VISIBLE);
        if (!isAnimaing) {
            isAnimaing = true;
            ViewPropertyAnimator.animate(currentWord).scaleX(1f)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(400)
                    .start();
            ViewPropertyAnimator.animate(currentWord).scaleY(1f)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(400)
                    .start();
        }

        //先移除之前的
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//				currentWord.setVisibility(View.GONE);

                ViewPropertyAnimator.animate(currentWord).scaleX(0f)
                        .setDuration(400)
                        .start();
                ViewPropertyAnimator.animate(currentWord).scaleY(0f)
                        .setDuration(400)
                        .start();

                isAnimaing = false;
            }
        }, 1000);
    }

    private void initView() {
        myLayout.setSlideMenu(slideMenu);
        slideMenu.setOnDragStateChangeListener(new SlideMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
            }

            @Override
            public void onClose() {
                //头像震动
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
        mLayoutManager = new LinearLayoutManager(this);
        mainRecycleview.setLayoutManager(mLayoutManager);
        //设置Adapter
        mainRecycleview.setAdapter(new MyAdapter(this, friends));
    }

    // TODO: 2015/11/30  
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.NormalTextViewHolder> {


        private ArrayList<Friend> firends;
        private LayoutInflater mLayoutInflater;
        private Context context;


        public MyAdapter(Context context, ArrayList<Friend> friends) {
            this.context = context;
            this.firends = friends;
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
            Friend friend = friends.get(position);
            String firstWord = friend.getPinyin().charAt(0) + "";
            if (position > 0) {
                //获取上一个item的首字母
                String lastWord = friends.get(position - 1).getPinyin().charAt(0) + "";
                if (firstWord.equals(lastWord)) {
                    //当前首字母和上一个首字母相同，应该隐藏当前的首字母
                    holder.tvLetter.setVisibility(View.GONE);
                } else {
                    //不相同，应该显示
                    holder.tvLetter.setVisibility(View.VISIBLE);
                    holder.tvLetter.setText(firstWord);
                }
            } else {
                holder.tvLetter.setVisibility(View.VISIBLE);
                holder.tvLetter.setText(firstWord);
            }
            holder.tvName.setText(friend.getName());
        }


        //返回数据的数量
        @Override
        public int getItemCount() {
            return firends.size();
        }


        //自定义的viewholder，拥有每个item的元素
        public class NormalTextViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tv_letter)
            TextView tvLetter;
            @Bind(R.id.tv_name)
            TextView tvName;

            public NormalTextViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, tvName.getText(), Toast.LENGTH_SHORT).show();

                    }
                });
            }

        }

    }

    private void fillList() {
        // 虚拟数据
        friends.add(new Friend("李伟"));
        friends.add(new Friend("张三"));
        friends.add(new Friend("阿三"));
        friends.add(new Friend("阿四"));
        friends.add(new Friend("段誉"));
        friends.add(new Friend("段正淳"));
        friends.add(new Friend("张三丰"));
        friends.add(new Friend("陈坤"));
        friends.add(new Friend("林俊杰1"));
        friends.add(new Friend("陈坤2"));
        friends.add(new Friend("王二a"));
        friends.add(new Friend("林俊杰a"));
        friends.add(new Friend("张四"));
        friends.add(new Friend("林俊杰"));
        friends.add(new Friend("王二"));
        friends.add(new Friend("王二b"));
        friends.add(new Friend("赵四"));
        friends.add(new Friend("杨坤"));
        friends.add(new Friend("赵子龙"));
        friends.add(new Friend("杨坤1"));
        friends.add(new Friend("李伟1"));
        friends.add(new Friend("宋江"));
        friends.add(new Friend("宋江1"));
        friends.add(new Friend("李伟3"));
    }
}
