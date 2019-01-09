package android.under_dash.addresses.search.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.app.Constants;
import android.under_dash.addresses.search.helpers.UiUtils;
import android.under_dash.addresses.search.helpers.Utils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

public class DialogUtil extends AlertDialog.Builder {

    protected static final int BOTTOM_PADDING = 8;
    protected int mColor;
    protected Runnable mRunOnShow;


    public void setDialogColor(int color) {
        this.mColor = color;
    }

    public void setRunOnShow(Runnable runOnShow) {
        this.mRunOnShow = runOnShow;
    }

    /*
     * do not call this unless you are sure you need a new theme and your theme looks good on android v 16 to 23
     */
    @Deprecated
    public DialogUtil(Context context, int theme, boolean shouldChangeTheme) {
        this(context);
    }

    /*
     * do not call this it will not change the theme call GlideDialogBuiler with boolean shouldChangeTheme
     */
    @Deprecated
    public DialogUtil(Context context, int theme) {
        this(context);
    }

    public DialogUtil(Context context) {
        super(context, R.style.AlertDialogTheme);
        init();
    }

    private void init() {
        mColor = ContextCompat.getColor(App.getAppContext(), R.color.md_green_200);
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public android.support.v7.app.AlertDialog create() {

        final AlertDialog mDialog =  super.create();
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Resources resources = getContext().getResources();
                int divierId = resources.getIdentifier("android:id/titleDivider", null, null);
                View divider = mDialog.findViewById(divierId);
                if(divider != null) {
                    divider.setVisibility(View.GONE);
                }
                ListView listView = mDialog.getListView();
                if(listView != null) {
                    mDialog.getListView().setDividerHeight(0);
                    mDialog.getListView().setPadding(0, 0, 0, UiUtils.pixToDp(BOTTOM_PADDING));
                }
                int width = resources.getDimensionPixelSize(R.dimen.dialog_width);
                mDialog.getWindow().setLayout( width, ActionBar.LayoutParams.WRAP_CONTENT);
                Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                if(negativeButton != null) {
                    negativeButton.setTextColor(mColor);
                    negativeButton.setText((negativeButton.getText().toString()).toUpperCase(Locale.getDefault()));
                }
                Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if(positiveButton != null) {
                    positiveButton.setTextColor(mColor);
                    positiveButton.setText((positiveButton.getText().toString()).toUpperCase(Locale.getDefault()));
                }
                Button neutralButton = mDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                if(neutralButton != null) {
                    neutralButton.setTextColor(mColor);
                    neutralButton.setText((neutralButton.getText().toString()).toUpperCase(Locale.getDefault()));
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    int customPanelId = resources.getIdentifier("android:id/customPanel", null, null);
                    View customPanel = mDialog.findViewById(customPanelId);
                    if(customPanel != null) {
                        customPanel.setPadding(0, 0, 0, (int) UiUtils.pixToDp(10));
                    }
                }

                int messageId = resources.getIdentifier("android:id/message", null, null);

                if (messageId > 0) {
                    TextView dialogMessage = (TextView) mDialog.findViewById(messageId);
                    if (dialogMessage != null) {
                        dialogMessage.setMovementMethod(LinkMovementMethod.getInstance());
                        dialogMessage.setLinkTextColor(mColor);
                        dialogMessage.setPadding(dialogMessage.getPaddingLeft(),UiUtils.pixToDp(15),dialogMessage.getPaddingRight(),dialogMessage.getPaddingBottom());
                    }
                }


//                will set the dialog BG color
//                ((ViewGroup)((ViewGroup)mDialog.getWindow().getDecorView()).getChildAt(0)) //ie LinearLayout containing all the dialog (title, titleDivider, content)
//                        .getChildAt(1) // ie the view titleDivider
//                        .setBackgroundColor(mColor);

                if(mRunOnShow != null) {
                    mRunOnShow.run();
                }
            }
        });
        return mDialog;
    }


    public static Dialog show(int title, int message, Integer negativeButton, DialogInterface.OnClickListener negativeListener, Integer positiveButton,
                                    DialogInterface.OnClickListener positiveLstener, Integer neutralButton,
                                    DialogInterface.OnClickListener neutralLstener, Context context){
        return show(title, message, negativeButton,negativeListener, positiveButton,positiveLstener, neutralButton,  neutralLstener, null,  context, false);
    }

    public static Dialog show(int title, int message, Integer negativeButton, DialogInterface.OnClickListener negativeListener, Integer positiveButton,
                                    DialogInterface.OnClickListener positiveLstener, Context context){
        return show(title, message, negativeButton,negativeListener, positiveButton,positiveLstener, null,  null, null,  context, false);
    }

    public static Dialog show(int title, int message, Integer negativeButton,Integer positiveButton,
                                    DialogInterface.OnClickListener listener, String input, Context context,boolean addProgress){
        return show(title, message, negativeButton,listener, positiveButton,null,  null,  null,input,  context, addProgress);
    }

    public static Dialog show(int title, int message, Integer negativeButton,Integer positiveButton,
                                    DialogInterface.OnClickListener positiveLstener, Context context){
        return show(title, message, negativeButton,null, positiveButton,positiveLstener,  null,  null,null, context, false);
    }

    public static Dialog show(int title, String message, Integer negativeButton,Integer positiveButton,
                                    DialogInterface.OnClickListener positiveLstener, Context context){
        return show(title, message, negativeButton,null, positiveButton,positiveLstener,  null,  null,null, context, false);
    }

    public static Dialog show(int title, int message, Integer negativeButton, DialogInterface.OnClickListener negativeListener, Integer positiveButton,
                                    DialogInterface.OnClickListener positiveLstener, Integer neutralButton,
                                    DialogInterface.OnClickListener neutralLstener, String input, Context context,boolean addProgress) {

        return show(
                title,
                message > 0 ? context.getResources().getText(message).toString() : null,
                negativeButton,
                negativeListener,
                positiveButton,
                positiveLstener,
                neutralButton,
                neutralLstener,
                input,
                context,
                addProgress
        );
    }

    public static Dialog show(int title, String message, Integer negativeButton, DialogInterface.OnClickListener negativeListener, Integer positiveButton,
                                    DialogInterface.OnClickListener positiveLstener, Integer neutralButton,
                                    DialogInterface.OnClickListener neutralLstener, String input, Context context,boolean addProgress) {

        DialogUtil builder = new DialogUtil(context);
        if(title > 0) {
            builder.setTitle(title);
        }
        if(message != null) {
            builder.setMessage(message);
        }


        if(positiveButton != null) {
            builder.setPositiveButton(positiveButton, positiveLstener);
        }

        if(neutralButton != null) {
            builder.setNeutralButton(neutralButton, neutralLstener);
        }

        if(negativeButton != null) {
            builder.setNegativeButton(negativeButton, negativeListener);
        }

        int pad = UiUtils.pixToDp(22);
        if(input != null){
            FrameLayout frame = new FrameLayout(context);
            EditText editText = new EditText(context);
            editText.setId(Constants.DIALOG_EDIT_TEXT_ID);
            frame.setPadding(pad,0,pad,0);
            editText.setHint(input);
            frame.addView(editText);
            builder.setView(frame);
        }else if(addProgress){ //pass null to get a progress
            ProgressBar progress = new ProgressBar(context);
            progress.setPadding(pad,pad,pad,pad);
            builder.setView(progress);
            builder.setCancelable(false);
        }
        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window == null){
            return dialog;
        }

        dialog.show();
        return dialog;
    }

}
