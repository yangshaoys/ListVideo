package video.carryyang.com.listvideo.videoview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by yangshao on 2017/9/26.
 */

public class MyTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = "MyTextureView";

    private String playingUrl = "";

    private MediaPlayer mMediaPlayer;

    private Surface surface;
    private MediaPlayer.OnPreparedListener preparedListener;
    private MediaPlayer.OnErrorListener errorListener;
    private MediaPlayer.OnCompletionListener completionListener;
    private MediaPlayer.OnInfoListener infoListener;
    private MediaPlayer.OnSeekCompleteListener seekCompleteListener;

    public MyTextureView(Context context) {
        super(context);
    }

    public MyTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            Log.e(TAG, "startPlay");
        } else {
            Log.e(TAG, "start Error media is null");
        }
    }

    public void pausePlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            Log.e(TAG, "stopPlay");
        } else {
            Log.e(TAG, "pause Error media is null");
        }
    }

    public void resetPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            Log.e(TAG, "resetPlay");
        } else {
            Log.e(TAG, "reset Error media is null");
        }
    }


    public void destory() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    public void setUrl(String path) {
        if (!playingUrl.equals(path)) {
            mMediaPlayer.reset();
            try {
                playingUrl = path;
                if (path.contains("http")) {
                    mMediaPlayer.setDataSource(path);
                } else {
                    FileInputStream fis = null;
                    fis = new FileInputStream(new File(path));
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    mMediaPlayer.setDataSource(fis.getFD());
                }
                mMediaPlayer.prepareAsync();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            mMediaPlayer.start();
        }
        if(preparedListener != null) {
            mMediaPlayer.setOnPreparedListener(preparedListener);
        }
    }

    private void initView() {
        setSurfaceTextureListener(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        initMediaPlayer(surfaceTexture);
    }

    /**
     * @param surfaceTexture
     * @return -1 初始化失败
     */
    private int initMediaPlayer(SurfaceTexture surfaceTexture) {
        if (surfaceTexture == null)
            return 1;

        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                Log.e(TAG, " initMediaPlayer new media");
            }
            surface = new Surface(surfaceTexture);
            mMediaPlayer.setSurface(surface);
            Log.e(TAG, " initMediaPlayer Success");
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " initMediaPlayer-" + e.getMessage());
            return -1;
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }

    public void setMediaPlayer(MediaPlayer mMediaPlayer) {
        this.mMediaPlayer = mMediaPlayer;
        mMediaPlayer.setSurface(new Surface(this.getSurfaceTexture()));
    }

    public String getPlayingUrl() {
        return playingUrl;
    }

    public void setPreparedListener(MediaPlayer.OnPreparedListener preparedListener) {
        this.preparedListener = preparedListener;
    }

    public void setErrorListener(MediaPlayer.OnErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setCompletionListener(MediaPlayer.OnCompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public void setInfoListener(MediaPlayer.OnInfoListener infoListener) {
        this.infoListener = infoListener;
    }

    public void setSeekCompleteListener(MediaPlayer.OnSeekCompleteListener seekCompleteListener) {
        this.seekCompleteListener = seekCompleteListener;
    }
}

