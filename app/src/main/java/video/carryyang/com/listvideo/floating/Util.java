package video.carryyang.com.listvideo.floating;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Handler;

/**
 * 工具类
 * @author Frank
 * @version 1.0
 * Create by 2015.10.8
 */
public class Util {
	public static class ResolutionComparator implements Comparator<Camera.Size> {
		@Override
		public int compare(Camera.Size size1, Camera.Size size2) {
			if(size1.height != size2.height)
				return size1.height -size2.height;
			else
				return size1.width - size2.width;
		}
	}
	
	public static List<Camera.Size> getResolutionList(Camera camera)
	{ 
		Parameters parameters = camera.getParameters();
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();


		return previewSizes;
	}
	
	/**
	 * 重写获得mediaplayer
	 * @param context
	 * @return
	 */
	public static MediaPlayer getMediaPlayer(Context context){

	    MediaPlayer mediaplayer = new MediaPlayer();

	    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
	        return mediaplayer;
	    }

	    try {
	        Class<?> cMediaTimeProvider = Class.forName( "android.media.MediaTimeProvider" );
	        Class<?> cSubtitleController = Class.forName( "android.media.SubtitleController" );
	        Class<?> iSubtitleControllerAnchor = Class.forName( "android.media.SubtitleController$Anchor" );
	        Class<?> iSubtitleControllerListener = Class.forName( "android.media.SubtitleController$Listener" );

	        Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});

	        Object subtitleInstance = constructor.newInstance(context, null, null);

	        Field f = cSubtitleController.getDeclaredField("mHandler");

	        f.setAccessible(true);
	        try {
	            f.set(subtitleInstance, new Handler());
	        }
	        catch (IllegalAccessException e) {return mediaplayer;}
	        finally {
	            f.setAccessible(false);
	        }

	        Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);

	        setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
	        //Log.e("", "subtitle is setted :p");
	    } catch (Exception e) {}

	    return mediaplayer;
	}
}
