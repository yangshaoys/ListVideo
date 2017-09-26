package video.carryyang.com.listvideo;

import android.app.Application;
import android.media.MediaPlayer;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by yangshao on 2017/9/26.
 */

public class MainApplication extends Application{
    public static String appName = "listVideo";
    private MediaPlayer mediaPlayer;

    private static MainApplication instance = null;

    public static MainApplication getInstance(){
        return  instance ;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response;
            }
        };
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName;
        createDirectory(directory);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(MainApplication.this)
                .setBaseDirectoryName("caches")
                .setBaseDirectoryPath(new File(directory + "/tmp"))
                .setMaxCacheSize(128 * ByteConstants.MB)
                .setMaxCacheSizeOnLowDiskSpace(16 * ByteConstants.MB)
                .setMaxCacheSizeOnVeryLowDiskSpace(4 * ByteConstants.MB)
                .build();
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(MainApplication.this, okHttpClient)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(MainApplication.this, config);
    }

    public static void createDirectory(String directory) {
        File f = new File(directory + "/");
        if(!f.exists()) {
            f.mkdirs();
        }
    }

    public MediaPlayer getMediaPlayer() {
        if(mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }

}
