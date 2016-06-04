package com.zcc.guideline.lib;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by Hengyun on 16/5/30.
 */
public class GuideGenerator {
    private static final String GUIDE_GENNERATOR_SP = GuideGenerator.class.getName();
    private static final String GUIDE_CURRENT_VERSION = GuideGenerator.class.getName() + "current_vErsion";

    private Activity context;
    private View bindedView;

    private Bitmap hollowImage;
    private int hollowXOffset;
    private int hollowYOffset;

    private GuideView guideView;

    private String targetVersion;
    private String pageTag;
    private SharedPreferences mSp;

    private boolean startVersionControl = false;
    private boolean isHollowClickOnly = false;


    private GuideGenerator(Activity context) {
        this.context = context;
        guideView = new GuideView(context);
    }


    public static GuideGenerator init(Activity context) {
        return new GuideGenerator(context);
    }

    public GuideGenerator setTargetView(View v) {
        bindedView = v;
        return this;
    }

    public GuideGenerator setHollowImageRes(int id) {
        return setHollowBitmap(BitmapFactory.decodeResource(context.getResources(), id));
    }

    public GuideGenerator setHollowBitmap(Bitmap bitmap) {
        hollowImage = bitmap;
        return this;
    }


    public GuideGenerator setTipViewRes(int id, GuideView.Position position) {
        guideView.setTipViewRes(id, position, 0, 0, 0, 0);
        return this;
    }

    /**
     * @param id            res id
     * @param position      enmus
     * @param paddingLeft   dip
     * @param paddingRight  dip
     * @param paddingTop    dip
     * @param paddingBottom dip
     */
    public GuideGenerator setTipViewRes(int id, GuideView.Position position, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
        guideView.setTipBitmap(BitmapFactory.decodeResource(context.getResources(), id), position, paddingLeft, paddingRight, paddingTop, paddingBottom);
        return this;
    }

    public GuideGenerator setTipBitmap(Bitmap tipBitmap, GuideView.Position position) {
        guideView.setTipBitmap(tipBitmap, position, 0, 0, 0, 0);
        return this;
    }

    /**
     * @param tipBitmap     hollowBitmap
     * @param position      enmus
     * @param paddingLeft   dip
     * @param paddingRight  dip
     * @param paddingTop    dip
     * @param paddingBottom dip
     */
    public GuideGenerator setTipBitmap(Bitmap tipBitmap, GuideView.Position position, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
        guideView.setTipBitmap(tipBitmap, position, paddingLeft, paddingRight, paddingTop, paddingBottom);
        return this;
    }


    public void show() {
        if (startVersionControl && !Utils.getVersionName(context).equals(targetVersion)) {
//            if version un equals,
            return;
        }
        bindTarget();
    }

    public void setOverlayColor(int color) {
        guideView.setBackground(color);
    }

    public GuideGenerator setTargetVersion(String version, String pageTag) {
        if (pageTag.equals(GUIDE_CURRENT_VERSION)) {
            throw new IllegalStateException("page Tag has been used");
        }
        this.targetVersion = version;
        this.pageTag = pageTag;
        if (mSp == null) {
            mSp = context.getSharedPreferences(GUIDE_GENNERATOR_SP, Context.MODE_PRIVATE);
        }
        String reCordedVersion = mSp.getString(GUIDE_CURRENT_VERSION, "");
        if (!reCordedVersion.equals(version)) {
            SharedPreferences.Editor editor = mSp.edit();
            editor.clear();
            editor.putString(GUIDE_CURRENT_VERSION, version);
            editor.commit();
        }
        startVersionControl = true;

        return this;
    }

    protected void setUpView() {
        if (startVersionControl && getIsShown()) {
            return;
        }
        if (hollowImage != null) {
            guideView.setHollowBitmap(hollowImage);
        }
        guideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideView.setVisibility(View.GONE);
            }
        });
        startBind();
        inflateGuideView();
        if (startVersionControl) {
            SharedPreferences.Editor editor = mSp.edit();
            editor.putBoolean(pageTag, true);
            editor.apply();
        }
    }

    protected void bindTarget() {
        if (bindedView == null) {
            return;
        }
        if (ViewCompat.isAttachedToWindow(bindedView)) {
            setUpView();
        } else {
            final ViewTreeObserver viewTreeObserver = bindedView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        //noinspection deprecation
                        bindedView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        bindedView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    setUpView();
                }
            });
        }
    }

    protected void startBind() {
        int[] pos = new int[2];
        bindedView.getLocationOnScreen(pos);
        pos[0] = pos[0] + bindedView.getWidth() / 2;
        pos[1] = pos[1] + bindedView.getHeight() / 2;
        guideView.setPos(pos);
    }

    public GuideGenerator setHollowOffSet(int xOffset, int yOffset) {
        this.hollowXOffset = xOffset;
        this.hollowYOffset = yOffset;
        guideView.setOffset(xOffset, yOffset);
        return this;
    }

    protected void inflateGuideView() {
        if (guideView == null) {
            return;
        }
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        ViewGroup contentArea = (ViewGroup) context.getWindow().getDecorView().
                findViewById(android.R.id.content);
        int[] pos = new int[2];
        contentArea.getLocationOnScreen(pos);
        int size = contentArea.getChildCount();
        if (contentArea.getChildAt(size - 1) instanceof GuideView) {
            contentArea.removeViewAt(size - 1);
        }
        // frameLayoutWithHole's coordinates are calculated taking full screen height into account
        // but we're adding it to the content area only, so we need to offset it to the same Y value of contentArea
        layoutParams.setMargins(0, -pos[1], 0, 0);
        contentArea.addView(guideView, layoutParams);
    }

    protected boolean getIsShown() {
        if (mSp == null) {
            mSp = context.getSharedPreferences(GUIDE_GENNERATOR_SP, Context.MODE_PRIVATE);
        }
        return mSp.getBoolean(pageTag, false);
    }

    public GuideGenerator setHollowDiscClickOnly(boolean enabled) {
        this.isHollowClickOnly = enabled;
        return this;
    }
}
