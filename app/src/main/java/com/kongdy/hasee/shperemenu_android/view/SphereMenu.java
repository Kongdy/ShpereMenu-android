package com.kongdy.hasee.shperemenu_android.view;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kongdy on 2016/5/21.
 */
public class SphereMenu extends ViewGroup implements View.OnClickListener{

    private float radius = 0;
    private MENU_STATUS MenuStatu = MENU_STATUS.CLOSE;

    private boolean beginDrag = false;
    private onSphereMenuItemClickListener listener;
    private Map<Integer,Integer> movingPoss; // moving view
    private Map<Integer,Point> movePoints;
    CircleImageView homeBtn; // home btn

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
        movePoints = new HashMap<>();
        movingPoss = new HashMap<>();
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
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                homeBtn.setEnabled(true);
                view.setEnabled(true);
                view.setFocusable(true);
                view.setClickable(true);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setDuration(1000);
        animator.start();
        MenuStatu = MENU_STATUS.OPEN;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                for (int i = 1;i < getChildCount();i++) {
                    final View view = getChildAt(i);
                    if(isCrash(view,event) && !movingPoss.containsValue(i)) {
                        final int actionIndex = event.getActionIndex();
                        Point tempP = new Point();
                        tempP.x = (int) event.getX(actionIndex);
                        tempP.y = (int) event.getY(actionIndex);
                        movingPoss.put(actionIndex,i);
                        movePoints.put(actionIndex,tempP);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Set<Integer> keys = movingPoss.keySet();
                for (int i:keys) {
                    beginDrag = true;
                    final int tempX = (int) (event.getX(i) - movePoints.get(i).x);
                    final int tempY = (int) (event.getY(i) - movePoints.get(i).y);
                    final View view = getChildAt(movingPoss.get(i));
                    if (view != null) {
                        int l = view.getLeft();
                        int t = view.getTop();
                        view.layout(l + tempX, t + tempY,
                                l + view.getMeasuredWidth() + tempX
                                , t + view.getMeasuredHeight() + tempY);
                        movePoints.get(i).x = (int) event.getX(i);
                        movePoints.get(i).y = (int) event.getY(i);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                final int lostTouchIndex = event.getActionIndex();
                if(movingPoss.containsKey(lostTouchIndex)) {
                    final int pos = movingPoss.get(lostTouchIndex);
                    final View view = getChildAt(pos);
                    if(movingPoss.size() <= 1 && !beginDrag) {
                        if(listener != null) {
                            listener.onClick(view,pos);
                        }
                        closeMenu();
                    } else if(beginDrag){
                        backAnimation(view,view.getX(),view.getY(),pos);
                    }
                    movingPoss.remove(lostTouchIndex);
                    movePoints.remove(lostTouchIndex);
                }
                if(movingPoss.size() < 1) {
                    beginDrag = false;
                }

                break;
        }
        return true;
    }

    public void backAnimation(final View v,float touchX,float touchY,int pos) {
        float angle = (float) ((Math.PI/(getChildCount()))*pos-Math.PI/2);
        float backX = (float) (Math.sin(angle)*radius);
        float backY = (float) (Math.cos(angle) *radius);
        /*
         * calculate org pos
         */
        int l = (int) ((getMeasuredWidth()-v.getMeasuredWidth())/2+backX);
        int t = (int) (getMeasuredHeight()-v.getMeasuredHeight()-backY);

        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("x",touchX,l);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("y",touchY,t);

        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holderX,holderY);
        animator.setInterpolator(new BounceInterpolator());
        animator.setDuration(800);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue("x");
                float y = (float) animation.getAnimatedValue("y");
                v.setX(x);
                v.setY(y);
                invalidate();
            }
        });
        animator.start();
    }

    public void foldAnimation(final View view, int i) {
        final float angle = (float) ((Math.PI/(getChildCount()))*i-Math.PI/2);
        final float fromX = (float) (Math.sin(angle)*radius);
        final float fromY = (float) (Math.cos(angle)*radius);
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
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                homeBtn.setEnabled(true);
                view.setEnabled(false);
                view.setVisibility(View.GONE);
                view.setFocusable(false);
                view.setClickable(false);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
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
        homeBtn = (CircleImageView) getChildAt(0);
        homeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int count = getChildCount();
        if(count > 1) {
            homeBtn.setEnabled(false);
            if(MenuStatu == MENU_STATUS.CLOSE) {
                openMenu();
            } else {
               closeMenu();
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
        final float touchX = event.getX(event.getActionIndex());
        final float touchY = event.getY(event.getActionIndex());
        final float x = v.getX();
        final float y = v.getY();
        final int w = v.getMeasuredWidth();
        final int h = v.getMeasuredHeight();
        if(touchX > x && touchX < x+w && touchY > y && touchY < y + h) {
            return true;
        }
        return false;
    }

    public void closeMenu() {
        for (int i = 1;i < getChildCount();i++) {
            final View view = getChildAt(i);
            foldAnimation(view,i);
        }
    }

    public void openMenu() {
        for (int i = 1;i < getChildCount();i++) {
            final View view = getChildAt(i);
            view.setVisibility(View.VISIBLE);
            unfoldAnimation(view,i);
        }
    }

    public void addOnSphereMenuClickListener(onSphereMenuItemClickListener listener) {
        this.listener = listener;
    }

    public interface onSphereMenuItemClickListener {
        void onClick(View v,int pos);
    }

}
