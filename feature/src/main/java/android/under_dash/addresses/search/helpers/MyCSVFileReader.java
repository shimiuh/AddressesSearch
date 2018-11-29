package android.under_dash.addresses.search.helpers;

import android.app.Activity;
import android.util.Log;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

public class MyCSVFileReader {

    public static void openDialogToReadCSV(final Activity activity) {
//        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
//        FileDialog fileDialog = new FileDialog(activity, mPath);
//        fileDialog.setFileEndsWith(".csv");
//        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
//
//            @Override
//            public void fileSelected(File file) {
//                Log.d("shimi", "selected file " + file.toString());
//                 //execute asyncTask to import data into database from selected file.
//            }
//        });
//        fileDialog.showDialog();

        new ChooserDialog().with(activity)
                .withFilter(false, true, "csv")
                .withStartFile(null)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        Log.d("shimi", "selected file " + pathFile);
                        new ImportCVSToSQLiteDataBase(activity,pathFile).execute();
                    }
                })
                .build()
                .show();
        Log.d("shimi", "call to ChooserDialog show");

    }

}