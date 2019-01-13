package android.under_dash.addresses.search.helpers;

import android.support.annotation.NonNull;
import android.under_dash.addresses.search.app.App;



public class Work {

    Job mJob;

    public interface Job{
        Object run();
    }
    public interface Done{
        void onDone(Object object);
    }

    private Work(Job job) {
        this.mJob = job;
    }

    public void onUi(Done workOnUI){
        App.getBackgroundHandler().post(() -> {
            Object object = mJob.run();
            App.getUiHandler().post(() -> {
                workOnUI.onDone(object);
            });
        });
    }

    public void onUiDelayed(@NonNull Done workOnUI, long delayMillis){
        App.getBackgroundHandler().post(() -> {
            Object object = mJob.run();
            App.getUiHandler().postDelayed(() -> {
                workOnUI.onDone(object);
            },delayMillis);
        });
    }

    public static Work job(Job job){
        return new Work(job);
    }

//    private <T> T castObject(Class<T> clazz, Object object) {
//        return (T) object;
//    }
}
