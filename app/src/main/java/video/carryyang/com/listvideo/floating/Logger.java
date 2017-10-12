
package video.carryyang.com.listvideo.floating;
import android.util.Log;

/**
 * 封装日志类
 *
 * @author Frank
 * @version 1.0
 *          Create by 2015.10.8
 */
public class Logger {
    private final static boolean logFlag = true;

    /**
     * info
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (logFlag) {
            Log.i(tag, msg);
        }

    }

    /**
     * debug
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (logFlag) {
            Log.d(tag, msg);
        }

    }

    /**
     * verbose
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (logFlag) {
            Log.v(tag, msg);
        }

    }

    /**
     * warm
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (logFlag) {
            Log.w(tag, msg);
        }

    }

    /**
     * error
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (logFlag) {
            Log.e(tag, msg);
        }

    }
}  
