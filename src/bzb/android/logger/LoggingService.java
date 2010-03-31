/**
 * 
 */
package bzb.android.logger;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

/**
 * @author bzb
 *
 */
public class LoggingService extends Service {

	public LoggingService () {
		
	}
	
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
		
		SensorManager sensorManager = 
            (SensorManager)getSystemService( SENSOR_SERVICE  );
		List<Sensor> sensors = sensorManager.getSensorList( Sensor.TYPE_ALL );
	}

	public void onDestroy() {
		super.onDestroy();
		Log.i(getClass().getName(),"Destroyed service");
	}

}
