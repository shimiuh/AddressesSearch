package android.under_dash.addresses.search.view.customUI;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

/**
 * Created by shimi on 12/1/17. This class must have a FrameLayout as a parent
 */

public class LoadingTextView extends AppCompatTextView {

    private ProgressBar mProgressBar;
    private boolean mIsRefreshing = false;

    public LoadingTextView(Context context) {
        super(context);
        init();
    }

    public LoadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setEnabled(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        mProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
        mProgressBar.setVisibility(GONE);
    }

    @Override
    protected void onAttachedToWindow() {

        FrameLayout parent = (FrameLayout) getParent();

        if(mProgressBar != null) {
            ViewGroup progressParent = (ViewGroup) mProgressBar.getParent();
            if (progressParent != null && progressParent.getChildCount() > 0) {
                progressParent.removeView(mProgressBar);
                init();
            }
        }

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(mProgressBar, params);
        setLoading(false);
        super.onAttachedToWindow();
    }

    public void setLoading(boolean shouldLoad) {
        if (shouldLoad) {
            if (!mIsRefreshing) {
                mIsRefreshing = true;
                mProgressBar.setVisibility(VISIBLE);
                setAlpha(0.5f);
            }
        } else {
            if (mIsRefreshing) {
                mIsRefreshing = false;
                mProgressBar.setVisibility(GONE);
                setAlpha(1f);
            }
        }


    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }
}
