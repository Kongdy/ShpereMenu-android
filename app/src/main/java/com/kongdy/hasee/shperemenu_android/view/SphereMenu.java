package com.kongdy.hasee.shperemenu_android.view;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
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
    private boolean isFolding;

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
        final int l = (int) ((getMeasuredWidth()-view.getMeasuredWidth())/2+toX);
        final int t = (int) (getMeasuredHeight()-view.getMeasuredHeight()-toY);
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("x", view.getX(),l);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("y", view.getY(),t);
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holderX,holderY);
        animator.setInterpolator(new OvershootInterpolator());
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
                MenuStatu = MENU_STATUS.OPEN; // when animation is end ,reset status
                isFolding = false;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setDuration(200);
        animator.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                for (int i = 1;i < getChildCount();i++) {
                    final View view = getChildAt(i);
                    if(isCrash(view,event) && !movingPoss.containsValue(i) && view.isEnabled()) {
                        final int actionIndex = event.getActionIndex();
                        final int pointId = event.getPointerId(actionIndex); // pointId is safe
                        Point tempP = new Point();
                        tempP.x = (int) event.getX(actionIndex); // actionIndex is final ,no need to findPointerIndex
                        tempP.y = (int) event.getY(actionIndex);
                        movingPoss.put(pointId,i);
                        movePoints.put(pointId,tempP);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Set<Integer> keys = movingPoss.keySet();
                for (int i:keys) {
                    // get actionIndex by pointerId
                    final int touchIndex = event.findPointerIndex(i);
                    float touchX = event.getX(touchIndex);
                    float touchY = event.getY(touchIndex);
                    final View view = getChildAt(movingPoss.get(i));
                    final int tempX = (int) (touchX - movePoints.get(i).x);
                    final int tempY = (int) (touchY - movePoints.get(i).y);
                    if(tempX < 1 && tempY < 1 && !beginDrag) { // no drag
                        break;
                    }
                    beginDrag = true;
                    if (view != null) {
                        int l = view.getLeft();
                        int t = view.getTop();
                        int r = view.getRight();
                        int b = view.getBottom();
                        view.layout(l + tempX, t + tempY,r + tempX
                                ,b + tempY);
                        movePoints.get(i).x = (int) touchX;
                        movePoints.get(i).y = (int) touchY;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                final int currentLostPointId = event.getPointerId(event.getActionIndex());
                if(movingPoss.containsKey(currentLostPointId)) {
                    final int pos = movingPoss.get(currentLostPointId);
                    final View view = getChildAt(pos);
                    if(movingPoss.size() <= 1 && !beginDrag) {
                        if(listener != null) {
                            listener.onClick(view,pos);
                        }
                    } else if(beginDrag){
                        backAnimation(view,view.getX(),view.getY(),pos);
                    }
                    movingPoss.remove(currentLostPointId);
                    movePoints.remove(currentLostPointId);
                }
                if(movingPoss.isEmpty()) {
                    movePoints.clear();
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
        final int l = (int) ((getMeasuredWidth()-v.getMeasuredWidth())/2+backX);
        final int t = (int) (getMeasuredHeight()-v.getMeasuredHeight()-backY);

        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("x",touchX,l);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("y",touchY,t);

        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holderX,holderY);
        animator.setInterpolator(new BounceInterpolator());
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(isFolding) {
                    animation.cancel();
                } else {
                    float x = (float) animation.getAnimatedValue("x");
                    float y = (float) animation.getAnimatedValue("y");
                    v.setX(x);
                    v.setY(y);
                    invalidate();
                }
            }
        });
        animator.start();
    }

    public void foldAnimation(final View view, int i) {
        final int l = (getMeasuredWidth()-view.getMeasuredWidth())/2;
        final int t = getMeasuredHeight()-view.getMeasuredHeight();
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("x", view.getX(),l);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("y", view.getY(),t);
        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("a", 1f,0f);
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holderX,holderY,holderAlpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setX((Float) animation.getAnimatedValue("x"));
                view.setY((Float) animation.getAnimatedValue("y"));
                float alpha = (Float) animation.getAnimatedValue("a");
                if(alpha >= 0) {
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
                view.setVisibility(View.GONE);
                MenuStatu = MENU_STATUS.CLOSE;
                isFolding = false;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setDuration(200);
        animator.start();
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
                if(i > 0) {
                    child.setEnabled(false);
                }
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

    /**
     *  Compatible old sdk
     * @param view
     * @return
     */
    private Rect getViewRect(View view) {
        final int l = view.getLeft();
        final int t = view.getTop();
        final int r = view.getRight();
        final int b = view.getBottom();
        return new Rect(l,t,r,b);
    }

    /**
     * Compatible old sdk
     * @param view
     * @return
     */
    private RectF getViewRectF(View view) {
        final int l = view.getLeft();
        final int t = view.getTop();
        final int r = view.getRight();
        final int b = view.getBottom();
        return new RectF(l,t,r,b);
    }

    /**
     * judge both View whether is intersect
     * @param view1
     * @param view2
     * @return
     */
    public boolean isViewIntersect(View view1,View view2) {
        Rect rect1 = getViewRect(view1);
        Rect rect2 = getViewRect(view2);
        return rect1.intersect(rect2);
    }

    public void closeMenu() {
        isFolding = true;
        homeBtn.setEnabled(false);
        for (int i = 1;i < getChildCount();i++) {
            final View view = getChildAt(i);
            view.setEnabled(false);
            foldAnimation(view,i);
        }
    }

    public void openMenu() {
        isFolding = true;
        homeBtn.setEnabled(false);
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
