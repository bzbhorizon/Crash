package bzb.android.logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author bzb
 *
 */
public class LoggingActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i(getClass().getName(),"Started activity");
        
        Intent loggingServiceIntent = new Intent();
        loggingServiceIntent.setAction("bzb.android.logger.STARTLOGGING");
        startService(loggingServiceIntent);
        Log.i(getClass().getName(),"Sent intent to start logging service");
    }
    
    public void onDestroy () {
    	super.onDestroy();
    	
    	/*
    	 *  Remember to comment this out later! Obviously don't want
    	 *  to stop logging if you leave the activity UI
    	 */
    	
    	Intent loggingServiceIntent = new Intent();
        loggingServiceIntent.setAction("bzb.android.logger.STARTLOGGING");
        stopService(loggingServiceIntent);
        Log.i(getClass().getName(),"Sent intent to stop logging service");
    }
}