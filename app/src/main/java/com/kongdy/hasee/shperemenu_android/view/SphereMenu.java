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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by kongdy on 2016/5/21.
 */
public class SphereMenu extends FrameLayout {

    private Bitmap homeImageSrc;
    private OnClickListener homeClick;

    public SphereMenu(Context context) {
        super(context);
        initView();
    }

    public SphereMenu(Context context, AttributeSet attrs) {
        super(context, attrs);initView();
    }

    public SphereMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);initView();
    }

    private void initView() {
        setClipChildren(false);
        homeClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0;i < getChildCount();i++) {
                    final View view = getChildAt(i);
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                }
            }
        };
        notifyDataChanged();
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


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

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
            defaultPaint.setColor(Color.argb(100,10,10,10));

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