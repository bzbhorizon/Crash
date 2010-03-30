/**
 * 
 */
package bzb.android.logger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author bzb
 *
 */
public class LoggingService extends Service {

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		super.onCreate();
		Log.i(getClass().getName(),"Started service");
	}

	public void onDestroy() {
		super.onDestroy();
		Log.i(getClass().getName(),"Destroyed service");
	}

}
