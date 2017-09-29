package video.carryyang.com.listvideo.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by yangshao on 2017/9/29.
 */

public class AnimationUtils {
    private static int animationDuration = 500;

    public AnimationUtils() {

    }

    public static void fadeOut(final View view) {
        if (view != null && view.getVisibility() != View.INVISIBLE && view.getVisibility() != View.GONE) {
            AlphaAnimation animation = new AlphaAnimation(1.0F, 0.0F);
            animation.setDuration((long) animationDuration);
            animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

                public void onAnimationStart(Animation animation) {

                }

                public void onAnimationEnd(Animation animation) {
                    if (view != null) {
                        view.clearAnimation();
                        view.setVisibility(View.GONE);
                    }
                }

                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation);
        }
    }
}
