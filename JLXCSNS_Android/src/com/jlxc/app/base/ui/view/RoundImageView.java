package com.jlxc.app.base.ui.view;

import com.jlxc.app.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * 圆角ImageView
 * 
 * @author skg
 * 
 */
public class RoundImageView extends ImageView {

//	public RoundImageView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
//		setRectAdius(attributes.getDimensionPixelOffset(R.styleable.RoundImageView_rect_adius, 10));
//		
//		init();
//	}
//
//	public RoundImageView(Context context) {
//		super(context);
//		init();
//	}
//
//	private final RectF roundRect = new RectF();
//	private float rect_adius = 10;
//	private final Paint maskPaint = new Paint();
//	private final Paint zonePaint = new Paint();
//
//	private void init() {
//		maskPaint.setAntiAlias(true);
//		maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//		//
//		zonePaint.setAntiAlias(true);
//		zonePaint.setColor(Color.WHITE);
//		//
//		float density = getResources().getDisplayMetrics().density;
//		rect_adius = rect_adius * density;
//	}
//
//	public void setRectAdius(float adius) {
//		rect_adius = adius;
//		invalidate();
//	}
//
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
//		int w = getWidth();
//		int h = getHeight();
//		roundRect.set(0, 0, w, h);
//	}
//
//	@Override
//	public void draw(Canvas canvas) {
//		canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
//		canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
//		//
//		canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
//		super.draw(canvas);
//		canvas.restore();
//	}
	
	  public static final String TAG = "RoundedImageView";
	  public static final float DEFAULT_RADIUS = 0f;
	  public static final float DEFAULT_BORDER_WIDTH = 0f;
	  private static final ScaleType[] SCALE_TYPES = {
	      ScaleType.MATRIX,
	      ScaleType.FIT_XY,
	      ScaleType.FIT_START,
	      ScaleType.FIT_CENTER,
	      ScaleType.FIT_END,
	      ScaleType.CENTER,
	      ScaleType.CENTER_CROP,
	      ScaleType.CENTER_INSIDE
	  };

	  private float rect_adius = DEFAULT_RADIUS;
	  private float borderWidth = DEFAULT_BORDER_WIDTH;
	  private ColorStateList borderColor =
	      ColorStateList.valueOf(R.color.main_white);
	  private boolean isOval = false;
	  private boolean mutateBackground = false;

	  private int mResource;
	  private Drawable mDrawable;
	  private Drawable mBackgroundDrawable;

	  private ScaleType mScaleType;

	  public RoundImageView(Context context) {
	    super(context);
	  }

	  public RoundImageView(Context context, AttributeSet attrs) {
	    this(context, attrs, 0);
	  }

	  public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);

	    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyle, 0);

	    int index = a.getInt(R.styleable.RoundImageView_android_scaleType, -1);
	    if (index >= 0) {
	      setScaleType(SCALE_TYPES[index]);
	    } else {
	      // default scaletype to FIT_CENTER
	      setScaleType(ScaleType.FIT_CENTER);
	    }

	    rect_adius = a.getDimensionPixelSize(R.styleable.RoundImageView_rect_adius, -1);
//	    borderWidth = a.getDimensionPixelSize(R.styleable.RoundedImageView_border_width, -1);

	    // don't allow negative values for radius and border
	    if (rect_adius < 0) {
	      rect_adius = DEFAULT_RADIUS;
	    }
	    if (borderWidth < 0) {
	      borderWidth = DEFAULT_BORDER_WIDTH;
	    }

	    borderColor = a.getColorStateList(R.styleable.RoundImageView_riv_border_color);
	    if (borderColor == null) {
	      borderColor = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
	    }

	    mutateBackground = a.getBoolean(R.styleable.RoundImageView_riv_mutate_background, false);
	    isOval = a.getBoolean(R.styleable.RoundImageView_riv_oval, false);

	    updateDrawableAttrs();
	    updateBackgroundDrawableAttrs(true);

	    a.recycle();
	  }

	  @Override
	  protected void drawableStateChanged() {
	    super.drawableStateChanged();
	    invalidate();
	  }

	  /**
	   * Return the current scale type in use by this ImageView.
	   *
	   * @attr ref android.R.styleable#ImageView_scaleType
	   * @see android.widget.ImageView.ScaleType
	   */
	  @Override
	  public ScaleType getScaleType() {
	    return mScaleType;
	  }

	  /**
	   * Controls how the image should be resized or moved to match the size
	   * of this ImageView.
	   *
	   * @param scaleType The desired scaling mode.
	   * @attr ref android.R.styleable#ImageView_scaleType
	   */
	  @Override
	  public void setScaleType(ScaleType scaleType) {
	    assert scaleType != null;

	    if (mScaleType != scaleType) {
	      mScaleType = scaleType;

	      switch (scaleType) {
	        case CENTER:
	        case CENTER_CROP:
	        case CENTER_INSIDE:
	        case FIT_CENTER:
	        case FIT_START:
	        case FIT_END:
	        case FIT_XY:
	          super.setScaleType(ScaleType.FIT_XY);
	          break;
	        default:
	          super.setScaleType(scaleType);
	          break;
	      }

	      updateDrawableAttrs();
	      updateBackgroundDrawableAttrs(false);
	      invalidate();
	    }
	  }

	  @Override
	  public void setImageDrawable(Drawable drawable) {
	    mResource = 0;
	    mDrawable = RoundedDrawable.fromDrawable(drawable);
	    updateDrawableAttrs();
	    super.setImageDrawable(mDrawable);
	  }

	  @Override
	  public void setImageBitmap(Bitmap bm) {
	    mResource = 0;
	    mDrawable = RoundedDrawable.fromBitmap(bm);
	    updateDrawableAttrs();
	    super.setImageDrawable(mDrawable);
	  }

	  @Override
	  public void setImageResource(int resId) {
	    if (mResource != resId) {
	      mResource = resId;
	      mDrawable = resolveResource();
	      updateDrawableAttrs();
	      super.setImageDrawable(mDrawable);
	    }
	  }

	  @Override public void setImageURI(Uri uri) {
	    super.setImageURI(uri);
	    setImageDrawable(getDrawable());
	  }

	  private Drawable resolveResource() {
	    Resources rsrc = getResources();
	    if (rsrc == null) { return null; }

	    Drawable d = null;

	    if (mResource != 0) {
	      try {
	        d = rsrc.getDrawable(mResource);
	      } catch (Exception e) {
	        Log.w(TAG, "Unable to find resource: " + mResource, e);
	        // Don't try again.
	        mResource = 0;
	      }
	    }
	    return RoundedDrawable.fromDrawable(d);
	  }

	  @Override
	  public void setBackground(Drawable background) {
	    setBackgroundDrawable(background);
	  }

	  private void updateDrawableAttrs() {
	    updateAttrs(mDrawable);
	  }

	  private void updateBackgroundDrawableAttrs(boolean convert) {
	    if (mutateBackground) {
	      if (convert) {
	        mBackgroundDrawable = RoundedDrawable.fromDrawable(mBackgroundDrawable);
	      }
	      updateAttrs(mBackgroundDrawable);
	    }
	  }

	  private void updateAttrs(Drawable drawable) {
	    if (drawable == null) { return; }

	    if (drawable instanceof RoundedDrawable) {
	      ((RoundedDrawable) drawable)
	          .setScaleType(mScaleType)
	          .setCornerRadius(rect_adius)
	          .setBorderWidth(borderWidth)
	          .setBorderColor(borderColor)
	          .setOval(isOval);
	    } else if (drawable instanceof LayerDrawable) {
	      // loop through layers to and set drawable attrs
	      LayerDrawable ld = ((LayerDrawable) drawable);
	      for (int i = 0, layers = ld.getNumberOfLayers(); i < layers; i++) {
	        updateAttrs(ld.getDrawable(i));
	      }
	    }
	  }

	  @Override
	  @Deprecated
	  public void setBackgroundDrawable(Drawable background) {
	    mBackgroundDrawable = background;
	    updateBackgroundDrawableAttrs(true);
	    super.setBackgroundDrawable(mBackgroundDrawable);
	  }

	  public float getrect_adius() {
	    return rect_adius;
	  }

	  public void setrect_adius(int resId) {
	    setrect_adius(getResources().getDimension(resId));
	  }

	  public void setrect_adius(float radius) {
	    if (rect_adius == radius) { return; }

	    rect_adius = radius;
	    updateDrawableAttrs();
	    updateBackgroundDrawableAttrs(false);
	  }

	  public float getBorderWidth() {
	    return borderWidth;
	  }

	  public void setBorderWidth(int resId) {
	    setBorderWidth(getResources().getDimension(resId));
	  }

	  public void setBorderWidth(float width) {
	    if (borderWidth == width) { return; }

	    borderWidth = width;
	    updateDrawableAttrs();
	    updateBackgroundDrawableAttrs(false);
	    invalidate();
	  }

	  public int getBorderColor() {
	    return borderColor.getDefaultColor();
	  }

	  public void setBorderColor(int color) {
	    setBorderColor(ColorStateList.valueOf(color));
	  }

	  public ColorStateList getBorderColors() {
	    return borderColor;
	  }

	  public void setBorderColor(ColorStateList colors) {
	    if (borderColor.equals(colors)) { return; }

	    borderColor =
	        (colors != null) ? colors : ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
	    updateDrawableAttrs();
	    updateBackgroundDrawableAttrs(false);
	    if (borderWidth > 0) {
	      invalidate();
	    }
	  }

	  public boolean isOval() {
	    return isOval;
	  }

	  public void setOval(boolean oval) {
	    isOval = oval;
	    updateDrawableAttrs();
	    updateBackgroundDrawableAttrs(false);
	    invalidate();
	  }

	  public boolean isMutateBackground() {
	    return mutateBackground;
	  }

	  public void setMutateBackground(boolean mutate) {
	    if (mutateBackground == mutate) { return; }

	    mutateBackground = mutate;
	    updateBackgroundDrawableAttrs(true);
	    invalidate();
	  }
	}

