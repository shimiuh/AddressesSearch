package android.under_dash.addresses.search.helpers;

import android.under_dash.addresses.search.app.App;



public class Work {

    Object res;

    public interface Job{
        Object run();
    }
    public interface Done{
        void run(Object object);
    }

    private Work(Job job) {
        App.getBackgroundHandler().post(() -> {
            res = job.run();
        });
    }

    public void onUi(Done workOnUI){
        App.getUiHandler().post(() -> {
            workOnUI.run(res);
        });
    }

    public void onUiDelayed(Done workOnUI, long delayMillis){
        App.getUiHandler().postDelayed(() -> {
            workOnUI.run(castObject(res.getClass(),res));
        },delayMillis);
    }

    public static Work job(Job job){
        return new Work(job);
    }

    private <T> T castObject(Class<T> clazz, Object object) {
        return (T) object;
    }
}
