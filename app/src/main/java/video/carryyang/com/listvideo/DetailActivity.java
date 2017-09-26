package video.carryyang.com.listvideo;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.TextureView;

import butterknife.BindView;
import butterknife.ButterKnife;
import video.carryyang.com.listvideo.videoview.MyTextureView;

/**
 * Created by yangshao on 2017/9/26.
 */

public class DetailActivity extends Activity {
    @BindView(R.id.texture)
    MyTextureView textureView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                textureView.setMediaPlayer(MainApplication.getInstance().getMediaPlayer());
                textureView.startPlay();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
    }
}
