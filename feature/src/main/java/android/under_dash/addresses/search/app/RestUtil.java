package android.under_dash.addresses.search.app;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestUtil {
    private static RestUtil self;
    private String API_BASE_URL = "https://maps.googleapis.com/";
    private Retrofit retrofit;

    private RestUtil() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES).build();


        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(JacksonConverterFactory.create());
        retrofit = builder.client(httpClient).build();
    }

    public static RestUtil getInstance() {
        if (self == null) {
            synchronized(RestUtil.class) {
                if (self == null) {
                    self = new RestUtil();
                }
            }
        }
        return self;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

}
