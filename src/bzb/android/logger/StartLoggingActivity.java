package bzb.android.logger;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.ProgressBar;

/**
 * @author bzb
 *
 */
public class StartLoggingActivity extends Activity implements Callback {
	
    private ProgressBar mProgress;
    private int mProgressStatus = 0;
    private boolean stopProgress = false;
    private Handler mHandler = new Handler();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        startVideoCapture();

        Log.i(getClass().getName(),"Started activity");
        
        new Thread(new Runnable () {
        	public void run () {
		        Intent loggingServiceIntent = new Intent();
		        loggingServiceIntent.setAction("bzb.android.logger.STARTLOGGING");
		        startService(loggingServiceIntent);
		        Log.i(getClass().getName(),"Sent intent to start logging service");
        	}
        }).start();
       
        mProgress = (ProgressBar) findViewById(R.id.progress_bar);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
            	StatFs statFs;
                while (!stopProgress) {
                	statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
                	mProgressStatus = 100 - (int)((double)statFs.getAvailableBlocks() / (double)(statFs.getBlockCount() - (statFs.getFreeBlocks() - statFs.getAvailableBlocks())) * 100);
                	//Log.i(getClass().getName(), "Available space =" + statFs.getAvailableBlocks() * statFs.getBlockSize());
                	
                	if (mProgressStatus >= 99) {
                		Intent loggingServiceIntent = new Intent();
                        loggingServiceIntent.setAction("bzb.android.logger.STARTLOGGING");
                        stopService(loggingServiceIntent);
                        Log.i(getClass().getName(),"Memory full: sent intent to stop logging service");
                        
                        stopProgress = true;
                        break;
                	}
                	
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                        }
                    });
                    
                    try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }
            }
        }).start();

    }
    
    public void onDestroy () {
		if (videoRecorder != null) {
			try {
				videoRecorder.stop();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			videoRecorder.release();
		    Log.i(getClass().getName(), "Stopped video recorder");
		}
		
    	super.onDestroy();
    	
    	stopProgress = true;
    }

    /////////////////////////

	private MediaRecorder videoRecorder;
    
    private void startVideoCapture () {
    	SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        videoRecorder = new MediaRecorder();
		
	    videoRecorder.setPreviewDisplay(surfaceHolder.getSurface());
	    
		videoRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		videoRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		videoRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		videoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
		videoRecorder.setVideoFrameRate(5);
		videoRecorder.setVideoSize(320, 240);
		videoRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/vlog_" + System.currentTimeMillis() + ".mp4");
    }
    
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}
	
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {		
		try {
			videoRecorder.prepare();
			Log.i(getClass().getName(), "Prepared video recorder");	
			videoRecorder.start();
		    Log.i(getClass().getName(), "Started video recorder");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {}
}