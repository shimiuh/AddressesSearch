package android.under_dash.addresses.search.helpers;

import android.app.Activity;
import android.util.Log;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.concurrent.Future;

public class MyCSVFileReader {

    public interface OnChoosePath {
        void onChoose(File pathFile);
    }

    public static void openDialogToReadCSV(final Activity activity, OnChoosePath onChoosePath) {

        new ChooserDialog(activity)
                .withFilter(false, true, "csv")
                .withStartFile(null)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        Log.d("shimi", "selected file " + pathFile+" path = -"+path+"-");
                        if(onChoosePath != null) {
                            onChoosePath.onChoose(pathFile);
                        }
                    }
                })
                .build()
                .show();
        Log.d("shimi", "call to ChooserDialog show");

    }


}