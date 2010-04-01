/**
 * 
 */
package bzb.android.logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author bzb
 *
 */
public class StopLoggingActivity extends Activity {
	   /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i(getClass().getName(),"Started activity");
        
    	Intent loggingServiceIntent = new Intent();
        loggingServiceIntent.setAction("bzb.android.logger.STARTLOGGING");
        stopService(loggingServiceIntent);
        Log.i(getClass().getName(),"Sent intent to stop logging service");
        
        finish();
    }
    
    public void onDestroy () {
    	super.onDestroy();
    }
}
