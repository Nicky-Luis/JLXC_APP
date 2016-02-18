package com.jlxc.app.base.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Pinch Image View class
 * @author unknown hero
 * @author a.s.mironov
 */
public class PinchImageView extends ImageView implements View.OnTouchListener {

    private static float MAX_SCALE = 2.0f;
    private static float MIN_MOVE_DISTANCE = 5.0f;

    private Matrix mMatrix;
    private final float[] mMatrixValues = new float[9];
    private int mWidth;
    private int mHeight; 
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private float mScale;
    private float mMinScale;
    private float mPrevDistance;
    private boolean isScaling;
    private int mPrevMoveX;
    private int mPrevMoveY;
    private GestureDetector mDetector;
    
    private OnClickListener listener;
    
    public PinchImageView(Context context, AttributeSet attr) {
        super(context, attr);
        initialize();
    }

    public PinchImageView(Context context) {
        super(context);
        initialize();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initialize();
    }
    
    @Override
    public void setImageDrawable(Drawable drawable) {
    	super.setImageDrawable(drawable);
    	initialize();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        initialize();
    }

    private void initialize() {
        this.setScaleType(ScaleType.MATRIX);
        this.mMatrix = new Matrix();
        Drawable d = getDrawable();
        if (d != null) {
            mIntrinsicWidth = d.getIntrinsicWidth();
            mIntrinsicHeight = d.getIntrinsicHeight();
            setOnTouchListener(this);
        }
        mDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                maxZoomTo((int) e.getX(), (int) e.getY());
                cutting();
                return super.onDoubleTap(e);
            }
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
            	if (listener != null) {
					listener.onClick();
				}
            	return super.onSingleTapConfirmed(e);
            }
        });
        minZoomOut(mIntrinsicWidth / 2, mIntrinsicHeight / 2);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        mWidth = r - l;
        mHeight = b - t;

        mMatrix.reset();
        int r_norm = r - l;
        mScale = (float) r_norm / (float) mIntrinsicWidth;

        int paddingHeight;
        int paddingWidth;
        // scaling vertical
        if (mScale * mIntrinsicHeight > mHeight) {
            mScale = (float) mHeight / (float) mIntrinsicHeight;
            mMatrix.postScale(mScale, mScale);
            paddingWidth = (r - mWidth) / 2;
            paddingHeight = 0;
            // scaling horizontal
        } else {
            mMatrix.postScale(mScale, mScale);
            paddingHeight = (b - mHeight) / 2;
            paddingWidth = 0;
        }
        mMatrix.postTranslate(paddingWidth, paddingHeight);

        setImageMatrix(mMatrix);
        mMinScale = mScale;
        zoomTo(mScale, mWidth / 2, mHeight / 2);
        cutting();
        return super.setFrame(l, t, r, b);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    protected float getScale() {
        return getValue(mMatrix, Matrix.MSCALE_X);
    }

    public float getTranslateX() {
        return getValue(mMatrix, Matrix.MTRANS_X);
    }

    protected float getTranslateY() {
        return getValue(mMatrix, Matrix.MTRANS_Y);
    }

    protected void maxZoomTo(int x, int y) {
        if (mMinScale != getScale() && (getScale() - mMinScale) > 0.1f) {
            minZoomOut(x, y);
        } else {
            float scale = MAX_SCALE / getScale();
            zoomTo(scale, x, y);
        }
    }

    protected void minZoomOut(int x, int y) {
        // threshold 0.1f
        float scale = mMinScale / getScale();
        zoomTo(scale, x, y);
    }

    public void zoomTo(float scale, int x, int y) {
        if (getScale() * scale < mMinScale) {
            return;
        }
        if (scale >= 1 && getScale() * scale > MAX_SCALE) {
            return;
        }
        mMatrix.postScale(scale, scale);
        // move to center
        mMatrix.postTranslate(-(mWidth * scale - mWidth) / 2, -(mHeight * scale - mHeight) / 2);

        // move x and y distance
        mMatrix.postTranslate(-(x - (mWidth / 2)) * scale, 0);
        mMatrix.postTranslate(0, -(y - (mHeight / 2)) * scale);
        setImageMatrix(mMatrix);
    }

    public void cutting() {
        int width = (int) (mIntrinsicWidth * getScale());
        int height = (int) (mIntrinsicHeight * getScale());
        if (getTranslateX() < -(width - mWidth)) {
            mMatrix.postTranslate(-(getTranslateX() + width - mWidth), 0);
        }
        if (getTranslateX() > 0) {
            mMatrix.postTranslate(-getTranslateX(), 0);
        }
        if (getTranslateY() < -(height - mHeight)) {
            mMatrix.postTranslate(0, -(getTranslateY() + height - mHeight));
        }
        if (getTranslateY() > 0) {
            mMatrix.postTranslate(0, -getTranslateY());
        }
        if (width < mWidth) {
            mMatrix.postTranslate((mWidth - width) / 2, 0);
        }
        if (height < mHeight) {
            mMatrix.postTranslate(0, (mHeight - height) / 2);
        }
        setImageMatrix(mMatrix);
    }

    private float distance(float x0, float x1, float y0, float y1) {
        float x = x0 - x1;
        float y = y0 - y1;
        return FloatMath.sqrt(x * x + y * y);
    }

    private float dispDistance() {
        return FloatMath.sqrt(mWidth * mWidth + mHeight * mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDetector.onTouchEvent(event)) {
            return true;
        }
        int touchCount = event.getPointerCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_1_DOWN:
            case MotionEvent.ACTION_POINTER_2_DOWN:
                if (touchCount >= 2) {
                    mPrevDistance = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                    isScaling = true;
                } else {
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchCount >= 2 && isScaling) {
                    float dist = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                    float scale = (dist - mPrevDistance) / dispDistance();
                    mPrevDistance = dist;
                    scale += 1;
                    scale = scale * scale;
                    zoomTo(scale, mWidth / 2, mHeight / 2);
                    cutting();
                } else if (!isScaling) {
                    int distanceX = mPrevMoveX - (int) event.getX();
                    int distanceY = mPrevMoveY - (int) event.getY();
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();
                    if(Math.abs(distanceX) > MIN_MOVE_DISTANCE || Math.abs(distanceY) > MIN_MOVE_DISTANCE) {
                        if(getScale() > mScale) {
                            mMatrix.postTranslate(-distanceX, -distanceY);
                            cutting();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_2_UP:
                if (event.getPointerCount() <= 1) {
                    isScaling = false;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return onTouchEvent(event);
    }
    
    public void setOnSingleClickListener(OnClickListener listener) {
    	this.listener = listener;
	}
    
    public interface OnClickListener {
    	public abstract void onClick();
    }

}