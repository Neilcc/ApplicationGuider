package com.zcc.guideline.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hengyun on 16/5/30.
 */
public class GuideView extends View {

    protected int bgColor = Color.argb(102, 0, 0, 0);

    protected Paint mMainPaint = new Paint();
    protected Paint mBgPaintWithBgColor = new Paint();
    protected Bitmap hollowBitmap;
    protected Paint mHollowPaint;
    protected Canvas mHollowCanvas;
    protected Canvas mBgCanvas;
    protected Xfermode xfermode;
    protected Bitmap mOverlay;

    protected Bitmap tipBitmap;
    protected Position tipPostion;
    protected float tipX = -1;
    protected float tipY = -1;
    protected int tipPaddingLeft;
    protected int tipPaddingRight;
    protected int tipPaddingTop;
    protected int tipPaddingBottom;

    protected float hollowX = -1;
    protected float hollowY = -1;
    protected int xOffset = 0;
    protected int yOffset = 0;

    protected int targetX = 0;
    protected int targetY = 0;
    protected int targetWidth = 0;
    protected int targetHeight = 0;
    protected int[] targetCenterPos;

    protected float padding;
    protected boolean shouldRePaint = true;

    public GuideView(Context context) {
        this(context, null);
    }

    public GuideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setTipViewRes(int id, Position position) {
        setTipViewRes(id, position, 0, 0, 0, 0);
    }

    /**
     * @param id            res id
     * @param position      enmus
     * @param paddingLeft   dip
     * @param paddingRight  dip
     * @param paddingTop    dip
     * @param paddingBottom dip
     */
    public void setTipViewRes(int id, Position position, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
        setTipBitmap(BitmapFactory.decodeResource(getResources(), id), position, paddingLeft, paddingRight, paddingTop, paddingBottom);
    }

    public void setTipBitmap(Bitmap tipBitmap, Position position) {
        setTipBitmap(tipBitmap, position, 0, 0, 0, 0);
    }

    /**
     * @param tipBitmap     hollowBitmap
     * @param position      enmus
     * @param paddingLeft   dip
     * @param paddingRight  dip
     * @param paddingTop    dip
     * @param paddingBottom dip
     */
    public void setTipBitmap(Bitmap tipBitmap, Position position, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
        this.tipBitmap = tipBitmap;
        this.tipPostion = position;
        this.tipPaddingLeft = paddingLeft;
        this.tipPaddingRight = paddingRight;
        this.tipPaddingBottom = paddingBottom;
        this.tipPaddingTop = paddingTop;
//      reset
        this.tipX = -1;
        this.tipY = -1;
    }

    public void setPos(int[] targetCenterPos) {
        if (targetCenterPos == null || targetCenterPos.length < 2) {
            return;
        }
        this.targetCenterPos = targetCenterPos;
        if (hollowBitmap != null) {
            hollowX = targetCenterPos[0] - hollowBitmap.getWidth() / 2;
            hollowY = targetCenterPos[1] - hollowBitmap.getHeight() / 2;
        }
    }

    public void setTargetViewParam(int targetWidth, int targetHeight, int targetX, int targetY) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    private void init() {
        //setBackgroundColor(bgColor);
        mHollowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHollowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setBackground(int bgColor) {
        this.bgColor = bgColor;
        shouldRePaint = true;
    }

    public void setHollowBitmap(Bitmap bitmap) {
        this.hollowBitmap = bitmap;
        //      X , Y are not calculated;
        if (targetCenterPos != null && hollowX == -1 && hollowY == -1) {
            hollowX = targetCenterPos[0] - hollowBitmap.getWidth() / 2;
            hollowY = targetCenterPos[1] - hollowBitmap.getHeight() / 2;
        }

        if (xfermode == null) {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hollowBitmap != null) {
            setUpPaintTools();
            mBgCanvas.drawRect(0.0f, 0.0f, mBgCanvas.getWidth(), mBgCanvas.getHeight(), mBgPaintWithBgColor);
            mBgCanvas.drawBitmap(hollowBitmap, hollowX + xOffset, hollowY + yOffset, mHollowPaint);
            mBgCanvas.drawBitmap(hollowBitmap, hollowX + xOffset, hollowY + yOffset, mMainPaint);
            if (tipBitmap != null) {
                if (tipX == -1 && tipY == -1) {
                    calculateTipPosWithHollow();
                }
                mBgCanvas.drawBitmap(tipBitmap, tipX, tipY, mMainPaint);
            }
            canvas.drawBitmap(mOverlay, 0, 0, mMainPaint);
        } else if (tipBitmap != null) {
            setUpPaintTools();
            mBgCanvas.drawRect(0.0f, 0.0f, mBgCanvas.getWidth(), mBgCanvas.getHeight(), mBgPaintWithBgColor);
            if (tipX == -1 && tipY == -1) {
                calculateTipPosNoHollow();
            }
            mBgCanvas.drawBitmap(tipBitmap, tipX, tipY, mMainPaint);
            canvas.drawBitmap(mOverlay, 0, 0, mMainPaint);
        } else {
            canvas.drawColor(bgColor);
        }
    }

    private void calculateTipPosWithHollow() {
        tipX = hollowX;
        tipY = hollowY;
        switch (tipPostion) {
            case TOP:
                tipY -= tipBitmap.getHeight();
                break;
            case BOTTOM:
                tipY += hollowBitmap.getHeight();
                break;
            case RIGHT:
                tipX += hollowBitmap.getWidth();
                break;
            case LEFT:
                tipX -= tipBitmap.getWidth();
                break;
        }
        tipX = tipX + Utils.dip2px(tipPaddingLeft, getContext()) - Utils.dip2px(tipPaddingRight, getContext());
        tipY = tipY + Utils.dip2px(tipPaddingTop, getContext()) - Utils.dip2px(tipPaddingBottom, getContext());
    }

    private void calculateTipPosNoHollow() {
        tipX = targetX;
        tipY = targetY;
        switch (tipPostion) {
            case TOP:
                tipY -= tipBitmap.getHeight();
                break;
            case BOTTOM:
                tipY += targetHeight;
                break;
            case RIGHT:
                tipX += targetWidth;
                break;
            case LEFT:
                tipX -= tipBitmap.getWidth();
                break;
        }
        tipX = tipX + Utils.dip2px(tipPaddingLeft, getContext()) - Utils.dip2px(tipPaddingRight, getContext());
        tipY = tipY + Utils.dip2px(tipPaddingTop, getContext()) - Utils.dip2px(tipPaddingBottom, getContext());
    }

    private void setUpPaintTools() {
        if (!shouldRePaint) {
            return;
        }
        shouldRePaint = false;
        if (mOverlay == null) {
            mOverlay = Bitmap.createBitmap(getMeasuredWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        }
        mBgPaintWithBgColor.setColor(bgColor);
        mBgCanvas = new Canvas(mOverlay);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    protected void onDetachedFromWindow() {
        freeBitmaps();
        super.onDetachedFromWindow();
    }

    private void freeBitmaps() {
        if (mOverlay != null) {
            mOverlay.recycle();
            mOverlay = null;
        }

        if (tipBitmap != null) {
            tipBitmap.recycle();
            tipBitmap = null;
        }
    }

    public static enum Position {
        TOP, BOTTOM, RIGHT, LEFT
    }
}
