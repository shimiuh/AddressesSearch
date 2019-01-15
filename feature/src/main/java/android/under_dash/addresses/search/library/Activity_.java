package android.under_dash.addresses.search.library;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import io.objectbox.Box;

public class Activity_ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, Fragment_.newInstance())
//                    .commitNow();
//        }
    }


    public <T> Box<T> getBox(Class clazz){
        return Application_.getBoxStore().boxFor(clazz);
    }
}
