package android.under_dash.addresses.search.managers;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

public class FlexWrapLayoutManager extends FlexboxLayoutManager {
    public FlexWrapLayoutManager(Context context) {
        super(context);
        init();
    }

    public FlexWrapLayoutManager(Context context, int flexDirection) {
        super(context, flexDirection);
        init();
    }

    public FlexWrapLayoutManager(Context context, int flexDirection, int flexWrap) {
        super(context, flexDirection, flexWrap);
        init();
    }

    public FlexWrapLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setFlexWrap(FlexWrap.WRAP);
        setFlexDirection(FlexDirection.ROW);
        setJustifyContent(JustifyContent.FLEX_START);
    }
}
