package bzb.android.logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * @author bzb
 *
 */
public class StartLoggingActivity extends Activity {
	
    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
                while (mProgressStatus < 99) {
                	statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
                	mProgressStatus = 100 - (int)((double)statFs.getAvailableBlocks() / (double)(statFs.getBlockCount() - (statFs.getFreeBlocks() - statFs.getAvailableBlocks())) * 100);
                	//Log.i(getClass().getName(), "Available space =" + statFs.getAvailableBlocks() * statFs.getBlockSize());
                	
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
                
                Intent loggingServiceIntent = new Intent();
                loggingServiceIntent.setAction("bzb.android.logger.STARTLOGGING");
                stopService(loggingServiceIntent);
                Log.i(getClass().getName(),"Sent intent to stop logging service");
            }
        }).start();

    }
    
    public void onDestroy () {
    	super.onDestroy();
    	
    	mProgressStatus = 100;
    }
}