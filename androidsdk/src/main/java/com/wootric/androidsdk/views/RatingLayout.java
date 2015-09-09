package com.wootric.androidsdk.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wootric.androidsdk.Constants;
import com.wootric.androidsdk.R;
import com.wootric.androidsdk.utils.ScreenUtils;

/**
 * Created by maciejwitowski on 9/7/15.
 */
public class RatingLayout extends RelativeLayout
    implements RatingBar.OnScoreChangedListener {

    private Context mContext;

    private RatingBar mRatingBar;
    private LinearLayout mScoreLayout;
    private TextView[] mScoreViews;
    private TextView mAnchorNotLikely;
    private TextView mAnchorLikely;

    private int mScoreLayoutPaddingHorizontal;
    private float mScoreTextSizeSelected;
    private float mScoreTextSizeNotSelected;
    private int mAnchorsMarginBottom;

    private int mColorSelected;
    private int mColorNotSelected;
    private int mColorAnchorNotSelected = Color.BLACK;

    private int mScoresCount;

    private static final float ALPHA_ANCHOR_SELECTED = 1f;
    private static final float ALPHA_ANCHOR_NOT_SELECTED = 0.38f;

    private static final int SCORE_LAYOUT_ID = 1;
    private static final int RATING_BAR_ID = 2;

    public RatingLayout(Context context) {
        super(context);
        init(context);
    }

    public RatingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RatingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setAnchorNotLikely(String value) {
        mAnchorNotLikely.setText(value);
    }

    public void setAnchorLikely(String value) {
        mAnchorLikely.setText(value);
    }

    private void init(Context context) {
        mContext = context;

        initResources();
        initScoreLayout();
        initRatingBar();
        initAnchors();

        mRatingBar.setOnScoreChangedListener(this);
    }

    private void initResources() {
        Resources res = mContext.getResources();
        mColorNotSelected = res.getColor(R.color.wootric_dark_gray);
        mColorSelected = res.getColor(R.color.wootric_brand_color);

        mScoreLayoutPaddingHorizontal = res.getDimensionPixelSize(
                R.dimen.wootric_score_layout_padding_horizontal);

        mScoreTextSizeSelected = res.getDimension(R.dimen.wootric_selected_score_text_size);
        mScoreTextSizeNotSelected = res.getDimension(R.dimen.wootric_not_selected_score_text_size);
        mAnchorsMarginBottom = res.getDimensionPixelSize(R.dimen.wootric_anchors_margin_bottom);

        mScoresCount = res.getInteger(R.integer.wootric_max_score) + 1;
    }

    private void initScoreLayout() {
        mScoreLayout = new LinearLayout(mContext);
        mScoreLayout.setId(SCORE_LAYOUT_ID);

        mScoreLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mScoreLayout.setLayoutParams(layoutParams);
        mScoreLayout.setPadding(mScoreLayoutPaddingHorizontal, 0, mScoreLayoutPaddingHorizontal, 0);

        mScoreViews = new TextView[mScoresCount];
        for(int score = 0; score < mScoreViews.length; score++) {
            TextView scoreView = buildScoreView(score);
            mScoreViews[score] = scoreView;
            mScoreLayout.addView(scoreView);
        }

        addView(mScoreLayout);
    }

    private void initRatingBar() {
        mRatingBar = new RatingBar(mContext);
        mRatingBar.setId(RATING_BAR_ID);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(BELOW, mScoreLayout.getId());
        mRatingBar.setLayoutParams(layoutParams);

        addView(mRatingBar);
    }

    private TextView buildScoreView(int score) {
        TextView scoreView = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        scoreView.setLayoutParams(params);

        scoreView.setGravity(Gravity.CENTER);
        scoreView.setText(String.valueOf(score));
        scoreView.setTextSize(ScreenUtils.pxToDp(mScoreTextSizeNotSelected));
        scoreView.setTextColor(mColorNotSelected);

        return scoreView;
    }

    private void initAnchors() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(BELOW, mRatingBar.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.setMargins(0, 0, 0, mAnchorsMarginBottom);

        mAnchorNotLikely = getAnchorTextView();
        mAnchorNotLikely.setLayoutParams(layoutParams);
        addView(mAnchorNotLikely);

        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(BELOW, mRatingBar.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0, 0, 0, mAnchorsMarginBottom);

        mAnchorLikely = getAnchorTextView();
        mAnchorLikely.setLayoutParams(layoutParams);
        addView(mAnchorLikely);
    }

    private TextView getAnchorTextView() {
        TextView anchorTextView = new TextView(mContext);
        anchorTextView.setTextColor(mColorAnchorNotSelected);
        anchorTextView.setTextSize(ScreenUtils.pxToDp(mScoreTextSizeNotSelected));
        anchorTextView.setAlpha(ALPHA_ANCHOR_NOT_SELECTED);
        return anchorTextView;
    }

    @Override
    public void onScoreChanged(int oldScore, int newScore) {
        updateSelectedScore(oldScore, newScore);
        updateAnchors(newScore);
    }

    private void updateSelectedScore(int oldScore, int newScore) {
        if(oldScore != Constants.INVALID_ID) {
            TextView oldScoreView = mScoreViews[oldScore];
            oldScoreView.setTextColor(mColorNotSelected);
            oldScoreView.setTextSize(ScreenUtils.pxToDp(mScoreTextSizeNotSelected));
        }

        TextView newScoreView = mScoreViews[newScore];
        newScoreView.setTextColor(mColorSelected);
        newScoreView.setTextSize(ScreenUtils.pxToDp(mScoreTextSizeSelected));
    }

    private void updateAnchors(int newScore) {
        boolean selectAnchorNotLikely = (newScore == 0);
<<<<<<< HEAD
        mAnchorNotLikely.setTextColor(selectAnchorNotLikely ? mColorSelected : mColorNotSelected);
        mAnchorNotLikely.setAlpha(selectAnchorNotLikely ? ALPHA_ANCHOR_SELECTED : ALPHA_ANCHOR_NOT_SELECTED);

        boolean selectAnchorLikely = (newScore == 10);
        mAnchorLikely.setTextColor(selectAnchorLikely ? mColorSelected : mColorNotSelected);
=======
        mAnchorNotLikely.setTextColor(selectAnchorNotLikely ? mColorSelected : mColorAnchorNotSelected);
        mAnchorNotLikely.setAlpha(selectAnchorNotLikely ? ALPHA_ANCHOR_SELECTED : ALPHA_ANCHOR_NOT_SELECTED);

        boolean selectAnchorLikely = (newScore == 10);
        mAnchorLikely.setTextColor(selectAnchorLikely ? mColorSelected : mColorAnchorNotSelected);
>>>>>>> Add anchors
        mAnchorLikely.setAlpha(selectAnchorLikely ? ALPHA_ANCHOR_SELECTED : ALPHA_ANCHOR_NOT_SELECTED);
    }
}
