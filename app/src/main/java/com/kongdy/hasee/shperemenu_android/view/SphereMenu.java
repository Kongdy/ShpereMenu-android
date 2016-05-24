package com.kongdy.hasee.shperemenu_android.view;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

/**
 * Created by kongdy on 2016/5/21.
 */
public class SphereMenu extends ViewGroup implements View.OnClickListener{

    private float radius = 0;
    private MENU_STATUS MenuStatu = MENU_STATUS.CLOSE;
    private View moveView;
    private Point movePoint;

    public enum MENU_STATUS{
        OPEN,
        CLOSE
    }

    public SphereMenu(Context context) {
        super(context);initView();
    }

    public SphereMenu(Context context, AttributeSet attrs) {
        super(context, attrs);initView();
    }

    public SphereMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);initView();
    }

    private void initView() {
        setBackgroundColor(Color.argb(150,255,255,0));
        movePoint = new Point();
    }

    private void unfoldAnimation(final View view,int i) {
        final float angle = (float) ((Math.PI/(getChildCount()))*i-Math.PI/2);
        final float toX = (float) (Math.sin(angle)*radius);
        final float toY = (float) (Math.cos(angle) *radius);
        view.setAlpha(1f);
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("x", view.getX(),view.getX()+toX);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("y", view.getY(),view.getY()-toY);
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holderX,holderY);
        animator.setInterpolator(new BounceInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setX((Float) animation.getAnimatedValue("x"));
                view.setY((Float) animation.getAnimatedValue("y"));
                invalidate();
            }
        });
        animator.setDuration(1000);
        animator.start();
//        final float speedX = 1000/toX;
//        final float speedY = 1000/toY;
//        final long unitTime = (long) (1000/radius);
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                float x = view.getX();
//                float y = view.getY();
//                while(x < orgX+toX && y < orgY+toY) {
//                    long startTime = System.currentTimeMillis();
//                    view.layout((int)(x+5),(int)(y+5),(int)(x+5+view.getMeasuredWidth())
//                    ,(int)(y+5+view.getMeasuredHeight()));
//                    Log.v("thread move","view.getX():"+view.getX()+",view.getY():"+view.getY());
//                    x = view.getX();
//                    y = view.getX();
//                    long endTime = System.currentTimeMillis();
//                    invalidate();
//                    if(endTime - startTime < 50) {
//                        try {
//                            Thread.sleep(50-(endTime - startTime));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//        thread.run();
//        AnimationSet animationSet = new AnimationSet(true);
//        Animation translateAnimation = new TranslateAnimation(0,toX,0,-toY);
//        translateAnimation.setStartOffset(i*50);
//        translateAnimation.setFillAfter(true);
//        translateAnimation.setFillEnabled(true);
//        animationSet.addAnimation(translateAnimation);
//        animationSet.setDuration(1000);
//        animationSet.setFillAfter(true);
//        animationSet.setFillEnabled(true);
//        animationSet.setInterpolator(new BounceInterpolator());
//        animationSet.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                // update coord
////                view.layout((int)(view.getX()+toX),(int)(view.getY()-toY),
////                        (int)(view.getX()+toX+view.getMeasuredWidth())
////                        ,(int)(view.getY()-toY+view.getMeasuredHeight()));
//                view.layout((int)(view.getX()),(int)(view.getY()),
//                        (int)(view.getX()+view.getMeasuredWidth())
//                        ,(int)(view.getY()+view.getMeasuredHeight()));
//                Log.v("ac","view.getLeft():"+view.getLeft()+",view.getTop():"+view.getTop());
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        animationSet.setStartOffset((i*100)/getChildCount());
        MenuStatu = MENU_STATUS.OPEN;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 1;i < getChildCount();i++) {
                    final View view = getChildAt(i);
                    if(isCrash(view,event)) {
                        Toast.makeText(getContext(),"touch position"+i,Toast.LENGTH_SHORT).show();
                        moveView = view;
                        movePoint.x = (int) event.getX();
                        movePoint.y = (int) event.getY();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(moveView != null) {
                    final int tempX = (int) (event.getX()-movePoint.x);
                    final int tempY = (int) (event.getY()-movePoint.y);
                    moveView.layout((int)(moveView.getX()+tempX),(int)(moveView.getY()+tempY),
                            (int)(moveView.getX()+moveView.getWidth()+tempX)
                    ,(int)(moveView.getY()+moveView.getHeight()+tempY));
                    movePoint.x = (int) event.getX();
                    movePoint.y = (int) event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                moveView = null;
                break;
        }
        return true;
    }

    public Animation backAnimation(int pos,float touchX,float touchY) {
        float angle = (float) ((Math.PI/(getChildCount()))*pos-Math.PI/2);
        float backX = (float) (Math.sin(angle)*radius);
        float backY = (float) Math.cos(angle) *radius;
        AnimationSet animationSet = new AnimationSet(true);
        Animation transAnimation = new TranslateAnimation(touchX,backX,touchY,backY);
        animationSet.addAnimation(transAnimation);
        animationSet.setDuration(500);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    public void foldAnimation(final View view, int i) {
        final float angle = (float) ((Math.PI/(getChildCount()))*i-Math.PI/2);
        final float fromX = (float) (Math.sin(angle)*radius);
        final float fromY = (float) (Math.cos(angle) *radius);
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("x", view.getX(),view.getX()-fromX);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("y", view.getY(),view.getY()+fromY);
        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("a", 1f,0f);
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holderX,holderY,holderAlpha);
        animator.setInterpolator(new BounceInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setX((Float) animation.getAnimatedValue("x"));
                view.setY((Float) animation.getAnimatedValue("y"));
                float alpha = (Float) animation.getAnimatedValue("a");
                if(alpha > 0) {
                    view.setAlpha(alpha);
                } else {
                    view.setAlpha(0);
                }
                invalidate();
            }
        });
        animator.setDuration(1000);
        animator.start();
        MenuStatu = MENU_STATUS.CLOSE;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();
        for (int i = 0;i < count;i++) {
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(changed) {
            layoutChildren();
        }
    }

    private void layoutChildren() {
        final int count = getChildCount();
        int childWidth = 0;
        for (int i = 0;i < count;i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() != View.GONE) {
                final int width =  child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();
                int l = (getMeasuredWidth()-width)/2;
                int t = (getMeasuredHeight()-height);
                childWidth = width;
                child.layout(l,t,l+width,t+height);
            }
        }
        radius = getMeasuredWidth()/2-childWidth;
        CircleImageView homeBtn = (CircleImageView) getChildAt(0);
        homeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d("sphere","home click");
        final int count = getChildCount();
        if(count > 1) {
            if(MenuStatu == MENU_STATUS.CLOSE) {
                for (int i = 1;i < getChildCount();i++) {
                    final View view = getChildAt(i);
                    unfoldAnimation(view,i);
                }
            } else {
                for (int i = 1;i < getChildCount();i++) {
                    final View view = getChildAt(i);
                    foldAnimation(view,i);
                }
            }
        }
    }

    public boolean isOpen() {
        return MenuStatu == MENU_STATUS.OPEN;
    }

    /**
     * 检测触摸点和空间是否有接触
     * @return
     */
    private boolean isCrash(View v,MotionEvent event) {
        final float touchX = event.getX();
        final float touchY = event.getY();
        final float x = v.getX();
        final float y = v.getY();
        final int w = v.getMeasuredWidth();
        final int h = v.getMeasuredHeight();
        Log.v("info","touchX:"+touchX+",touchY:"+touchY+",w:"+w+",h:"+h+",x:"+x+",y:"+y);
        if(touchX > x && touchX < x+w && touchY > y && touchY < y + h) {
            return true;
        }
        return false;
    }
}
