package com.kongdy.hasee.shperemenu_android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kongdy on 2016/5/21.
 */
public class SphereMenu extends ViewGroup {

    private Bitmap homeImageSrc;
    private OnClickListener homeClick;
    private List<View> mMatchParentChildren = new ArrayList<>();

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
        homeClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final int count = getChildCount();
                if(count > 0) {
                    for (int i = 1;i < getChildCount();i++) {
                        final View view = getChildAt(i);
                        final float x = view.getX();
                        final float y = view.getY();
//                        view.setFocusable(true);
//                        view.setClickable(true);
                        Log.d("sphere","home click");
                        view.startAnimation(unfoldAnimation(x,y, (float) (Math.random()*50*i),60,i));
                    }
                }
            }
        };
        notifyDataChanged();
    }

    private Animation unfoldAnimation(float x,float y,float cx,float cy,int i) {
        AnimationSet animationSet = new AnimationSet(true);
        Animation translateAnimation = new TranslateAnimation(x,x+cx,y,y-cy);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        translateAnimation.setStartOffset((i*100)/getChildCount());
        animationSet.addAnimation(translateAnimation);
        return animationSet;
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

        for (int i = 0;i < count;i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() != View.GONE) {
//                final int width = child.getMeasuredWidth();
//                final int height = child.getMeasuredHeight();
                final int width = child.getMeasuredWidth() != 0 && child.getMeasuredWidth() < getMeasuredWidth()
                        ?child.getMeasuredWidth():getMeasuredWidth();
                final int height = child.getMeasuredHeight() != 0 && child.getMeasuredHeight() < getMeasuredHeight()
                        ?child.getMeasuredHeight():getMeasuredHeight();
                child.layout(0,0,width,height);
            }
        }
    }

    public void notifyDataChanged() {
        CircleImageView homeImage = new CircleImageView(getContext());
        addView(homeImage);
        homeImage.setOnClickListener(homeClick);
    }


    public void setHomeImageSrc(Bitmap bm) {
        this.homeImageSrc = bm;
    }

    public void setHomeImageSrc(int resId) {
        setHomeImageSrc(BitmapFactory.decodeResource(getResources(),resId));
    }

//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//       // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int count = getChildCount();
//        mMatchParentChildren.clear();
//
//        int maxHeight = 0;
//        int maxWidth = 0;
//        int childState = 0;
//
//        for (int i = 0;i < count;i++) {
//            final View child = getChildAt(i);
//            if(child.getVisibility() != GONE) {
//                measureChild(child,widthMeasureSpec,heightMeasureSpec);
//                maxWidth = Math.max(maxWidth,child.getMeasuredWidth());
//                maxHeight = Math.max(maxHeight,child.getMeasuredHeight());
//                childState = combineMeasuredStates(childState,child.getMeasuredState());
//                mMatchParentChildren.add(child);
//            }
//        }
//
//        maxWidth += getPaddingLeft()+getPaddingRight();
//        maxHeight += getPaddingTop()+getPaddingBottom();
//
//        maxWidth = Math.max(maxWidth,getSuggestedMinimumWidth());
//        maxHeight = Math.max(maxHeight,getSuggestedMinimumHeight());
//
//        setMeasuredDimension(resolveSizeAndState(maxWidth,widthMeasureSpec,childState)
//        ,resolveSizeAndState(maxHeight,heightMeasureSpec,childState << MEASURED_HEIGHT_STATE_SHIFT));
//
//        count = mMatchParentChildren.size();
//        if(count > 1) {
//            for (int i = 0;i < count;i++) {
//                final View child = mMatchParentChildren.get(i);
//                final int childWidthMeasureSpec;
//                final int childHeightMeasureSpec;
//                final int width = Math.max(0,getMeasuredWidth()-getPaddingLeft()-getPaddingRight());
//                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);
//                final int height = Math.max(0,getMeasuredHeight()-getPaddingTop()-getPaddingBottom());
//                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
//                child.measure(childWidthMeasureSpec,childHeightMeasureSpec);
//            }
//        }
//
//    }

    /**
     * cricle image view
     */
    private class CircleImageView extends ImageView {

        private Bitmap ImageSrc;

        private Paint defaultPaint;
        private RectF clipOval;
        private PorterDuffXfermode src_out_mode;

        public CircleImageView(Context context) {
            super(context);
            initTools();
        }

        private void initTools() {
            defaultPaint = new Paint();
            defaultPaint.setAntiAlias(true);
            src_out_mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
            defaultPaint.setColor(Color.argb(150,10,10,10));

            clipOval = new RectF();
        }

        public void setMyImageRes(int resId) {
            ImageSrc = BitmapFactory.decodeResource(getResources(),resId);
            setImageBitmap(ImageSrc);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(ImageSrc == null) {
                ImageSrc = getImageBitmap();
            }
            canvas.saveLayer(0,0,getMeasuredWidth(),getMeasuredHeight(),defaultPaint
            ,Canvas.ALL_SAVE_FLAG);
            if(ImageSrc != null) {
                canvas.drawBitmap(ImageSrc,null,clipOval,defaultPaint);
                defaultPaint.setXfermode(src_out_mode);
                canvas.drawArc(clipOval,0f,360f,true,defaultPaint);
                defaultPaint.setXfermode(null);
            } else {
                canvas.drawArc(clipOval,0f,360f,true,defaultPaint);
            }
            canvas.restore();
           // super.onDraw(canvas);

        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            clipOval.left = 0;
            clipOval.top = 0;
            clipOval.right = widthSize;
            clipOval.bottom = heightSize;
        }

        public Bitmap getImageBitmap() {
            if(ImageSrc == null) {
                final int width = getMeasuredWidth();
                final int height = getMeasuredHeight();
                Drawable dra = getDrawable();
                if(dra == null) {
                    return null;
                }
                Bitmap bitmap = Bitmap.createBitmap(width,height
                        ,dra.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888: Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);
                dra.setBounds(0,0,width,height);
                dra.draw(canvas);
                return bitmap;
            } else {
                return ImageSrc;
            }
        }
    }
}
