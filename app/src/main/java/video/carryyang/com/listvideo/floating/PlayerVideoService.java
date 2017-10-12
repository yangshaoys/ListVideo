package video.carryyang.com.listvideo.floating;

import java.io.IOException;
import java.util.LinkedList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import video.carryyang.com.listvideo.R;

/**
 * 视频播放服务
 * @author Frank
 * @version 1.0 Create by 2015.10.29
 */
public class PlayerVideoService extends Service {
	private final static String TAG = "PlayerVideoService";
	private final IBinder mBinder = new LocalBinder();
	private final static int UPDATE_WINDOWN_SHOW = 4;
	private final static int UPDATE_WINDOWN_HIDEN = 5;
	/* MediaPlayer对象 */
	private MediaPlayer mMediaPlayer = null;
	private int currentTime = 0;// 歌曲播放进度
	private static int currentListItme = 0;// 当前播放第几首歌
	private LinkedList<Video> videos = new LinkedList<Video>();// 要播放的歌曲集合
	private PowerManager pm;
	private WakeLock wakeLock;
	private Context context;
	public static boolean isRuning = false;
	public static boolean isPause = true;
	private MyTimerThread myTimerThread;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams wmParams;
	private LinearLayout windowLayout;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private boolean isShowSurface = false;
	private Object object = new Object();
	private Handler handler = new Handler();
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_WINDOWN_SHOW:
				wmParams.x = 50;
				wmParams.y = 400;
				wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
				wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
				mWindowManager.updateViewLayout(windowLayout, wmParams);
				break;
			case UPDATE_WINDOWN_HIDEN:
				wmParams.x = -1000;
				wmParams.y = -1000;
				wmParams.width = 0;
				wmParams.height = 0;
				mWindowManager.updateViewLayout(windowLayout, wmParams);
				break;

			default:
				break;
			}
		}

	};
	
	{
		videos.add(new Video("http://v7.leappmusic.cc/t720p/20160621/a3072b47a-376e-11e6-b9bd-525400475748.mp4"));
	}

	@Override
	public void onCreate() {
		super.onCreate();
		isRuning = true;
		isPause = true;
		context = this;
		if (mMediaPlayer == null) {
			mMediaPlayer = Util.getMediaPlayer(context);
		}
		if (myTimerThread == null) {
			myTimerThread = new MyTimerThread();
			myTimerThread.start();
		}

		initWakelock();

		initWindowManager();
	}

	/**
	 * 初始化窗口控制
	 */
	private void initWindowManager() {
		// 获取的是LocalWindowManager对象
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		// 获取LayoutParams对象
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = LayoutParams.TYPE_PHONE;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE|LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = 0;
		wmParams.height = 0;
		// wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		// wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		windowLayout = (LinearLayout) inflater.inflate(
				R.layout.player_audio_service_surfaceview_layout, null);
		windowLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				wmParams.x = (int) event.getRawX() - windowLayout.getWidth()
						/ 2;
				// 25为状态栏高度
				wmParams.y = (int) event.getRawY() - windowLayout.getHeight()
						/ 2 - 40;
				mWindowManager.updateViewLayout(windowLayout, wmParams);
				return false;
			}
		});
		surfaceView = (SurfaceView) windowLayout
				.findViewById(R.id.player_audio_service_surface);
		surfaceHolder = surfaceView.getHolder();// SurfaceHolder是SurfaceView的控制接口
		surfaceHolder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				mMediaPlayer.setDisplay(surfaceHolder);
				synchronized (object) {
					isShowSurface = true;
					object.notifyAll();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

			}
		});// 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// Surface类型
		mWindowManager.addView(windowLayout, wmParams);
	}

	/**
	 * 初始化高亮控制
	 */
	private void initWakelock() {
		if (pm == null) {
			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		}

		if (wakeLock == null) {

			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
					.getClass().getName());
			wakeLock.acquire();
		}
	}

	/**
	 * 得到当前播放进度
	 */
	public int getCurrent() {
		if (mMediaPlayer.isPlaying()) {
			return mMediaPlayer.getCurrentPosition();
		} else {
			return currentTime;
		}
	}

	/**
	 * 获得当前MediaPlayer
	 * 
	 * @return
	 */
	public MediaPlayer getMediaPlayer() {
		if (mMediaPlayer != null) {
			return mMediaPlayer;
		}
		return null;
	}

	/**
	 * 跳到输入的进度
	 */
	public void movePlay(int progress) {
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(progress);
			currentTime = progress;
		}
	}

	/**
	 * @return 得到正在播放视频的Music对象
	 */
	public Video getCurrentMusic() {
		return videos.get(currentListItme);
	}

	/**
	 * 设置当前位置
	 * 
	 * @param position
	 */
	public void setCurrentItem(int position) {
		currentListItme = position;
	}

	/**
	 * 获得当前位置
	 * 
	 * @param
	 */
	public static int getCurrentItem() {
		return currentListItme;
	}

	/**
	 * 播放当前视频
	 * 
	 * @param
	 */
	private void playMusic(final String path) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					/* 重置MediaPlayer */
					currentTime = 0;
					if (mMediaPlayer == null) {
						mMediaPlayer = Util.getMediaPlayer(context);
					}
					mMediaPlayer.reset();
					mMediaPlayer.setWakeMode(PlayerVideoService.this, 1);
					/* 设置要播放的文件的路径 */
					mMediaPlayer.setDataSource(path);

//					mMediaPlayer
//							.setDataSource("http://test1.baidu.bos.starunion.ping-qu.com/aini.m3u8");
					mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					
					Message msg = Message.obtain();
					msg.what = UPDATE_WINDOWN_SHOW;
					mHandler.sendMessage(msg);
					synchronized (object) {
						if (!isShowSurface) {
							object.wait();
						}
					}
					
					mMediaPlayer.setScreenOnWhilePlaying(true);					
					/* 准备播放 */
					mMediaPlayer.prepare();
					/* 开始播放 */
					mMediaPlayer.start();
					isPause = false;

					mMediaPlayer
							.setOnPreparedListener(new OnPreparedListener() {

								@Override
								public void onPrepared(MediaPlayer mp) {
									Logger.i(TAG, "OnPreparedListener");
								}
							});
					// 音乐播放完毕
					mMediaPlayer
							.setOnCompletionListener(new OnCompletionListener() {
								public void onCompletion(MediaPlayer arg0) {
									// 播放完成一首之后进行下一首
									Logger.i(TAG, "播放完成");
									currentTime = 0;
								}
							});
					mMediaPlayer
							.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

								@Override
								public void onBufferingUpdate(MediaPlayer mp,
										int percent) {
									Logger.i(TAG, "OnBufferingUpdateListener");
								}
							});
					mMediaPlayer.setOnInfoListener(new OnInfoListener() {

						@Override
						public boolean onInfo(MediaPlayer mp, int what,
								int extra) {
							Logger.i(TAG, "OnInfoListener");
							return false;
						}
					});
					// 播放音乐时发生错误的事件处理
					mMediaPlayer
							.setOnErrorListener(new MediaPlayer.OnErrorListener() {

								public boolean onError(MediaPlayer mp,
										int what, int extra) {
									Logger.i(TAG, "播放出现错误");
									currentTime = 0;
									return false;
								}
							});
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 停止播放并结束服务
	 */
	public void stopMusic() {
		if (mMediaPlayer != null) {
			Log.i("test", "mMediaPlayer!=null");
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			stopSelf();
		}
	}


	/**
	 * 歌曲是否真在播放
	 */
	public boolean isPlay() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.isPlaying();
		}
		return false;
	}


	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	/**
	 * 自定义绑定Service类，通过这里的getService得到Service，之后就可调用Service这里的方法了
	 */
	public class LocalBinder extends Binder {
		public PlayerVideoService getService() {
			Log.d("playerService", "getService");
			return PlayerVideoService.this;
		}
	}

	@Override
	public void onDestroy() {
		Logger.i(TAG, "销毁服务");
		isRuning = false;
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
		if (myTimerThread != null) {
			try {
				myTimerThread.stop();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isRuning = true;
		// 获得activity传过来的参数
		if (intent != null) {
			String type = intent.getAction();
			switch (type) {
			case "play":
				playMusic(videos.get(0).url);
				break;
			case "close":
				stopMusic();
				mWindowManager.removeView(windowLayout);
				break;
			case "removeWindow":
				wmParams.x = -1000;
				wmParams.y = -1000;
				wmParams.width = 0;
				wmParams.height = 0;
				mWindowManager.updateViewLayout(windowLayout, wmParams);
				break;
			case "adddWindow":
				wmParams.x = 50;
				wmParams.y = 400;
				wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
				wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
				mWindowManager.updateViewLayout(windowLayout, wmParams);
				break;

			default:
				break;
			}
		} 
		return START_STICKY;
	};

	/**
	 * 时间线程
	 * 
	 * @author Frank
	 */
	private class MyTimerThread extends Thread {
		@Override
		public void run() {
			while (true) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						// Logger.i(TAG, "回调进度");
						if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
							currentTime += 1000;
						}
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Logger.e(TAG, e.getLocalizedMessage());
				}

			}
		}
	}
}
