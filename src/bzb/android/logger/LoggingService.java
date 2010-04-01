/**
 * 
 */
package bzb.android.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/**
 * @author bzb
 *
 */
public class LoggingService extends Service implements SensorEventListener, LocationListener, Listener {

	private PrintWriter captureFile;
	private PowerManager.WakeLock wl;
	private LocationManager lm;

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
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getName());
        wl.acquire();
        Log.i(getClass().getName(),"Acquired wakelock");
        
		super.onCreate();
		Log.i(getClass().getName(),"Started service");
		
		startLogging();
	}
	
	public void onPause() {
		
	}

	public void onDestroy() {
		if (wl != null) {
			wl.release();
			Log.i(getClass().getName(),"Released wakelock");
		}
		
		super.onDestroy();
		
		if (captureFile != null) {
			captureFile.close();
			Log.i(getClass().getName(),"Closed file");
		}
		
		Log.i(getClass().getName(),"Destroyed service");
	}
	
	private void startLogging () {
		File captureFileName = new File( Environment.getExternalStorageDirectory(), "log.csv" );
		Log.i(getClass().getName(),"Linked to file");
		
        try {       	
            captureFile = new PrintWriter( new FileWriter( captureFileName, true ) );
            Log.i(getClass().getName(),"Opened file " + captureFileName.getName());
            
            SensorManager sensorManager = 
                (SensorManager)getSystemService( SENSOR_SERVICE  );
    		List<Sensor> sensors = sensorManager.getSensorList( Sensor.TYPE_ALL );
    		Log.i(getClass().getName(),"Listed sensors");
    		
    		for (Sensor sensor : sensors) {
    			sensorManager.registerListener( 
                        this, 
                        sensor,
                        SensorManager.SENSOR_DELAY_UI );
    			Log.i(getClass().getName(),"Registered listener for sensor " + sensor.getName());
    			captureFile.println(sensor.getType() + "," + sensor.getName() + "," + sensor.getResolution() + "," + sensor.getMaximumRange() + "," + sensor.getPower());
    		}
    		
    		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    		List<String> providers = lm.getProviders(true);
    		Log.i(getClass().getName(),"Listed providers");
    		
    		for (String provider : providers) {
    			Log.i(getClass().getName(),"Enabled provider " + provider);
    		}
    		
    		lm.addGpsStatusListener(this);
    		
    		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    		Log.i(getClass().getName(),"Started GPS tracking");
        } catch( IOException ex ) {
            Log.e( getClass().getName(), ex.getMessage(), ex );
        }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
        captureFile.print(System.currentTimeMillis() + "," + event.sensor.getType() + ",");
        //Log.i( getClass().getName(), event.sensor.getName() + " onSensorChanged" );
        if( captureFile != null ) {
                for( int i = 0 ; i < event.values.length ; ++i ) {
                    if( i > 0 )
                        captureFile.print( "," );
                    captureFile.print( Float.toString( event.values[i] ) );
                }
                captureFile.println();
        }
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		captureFile.println(System.currentTimeMillis() + ",G," + arg0.getLatitude() + "," + arg0.getLongitude() + "," + arg0.getAccuracy() + "," + arg0.getAltitude() + "," + arg0.getBearing() + "," + arg0.getSpeed());
		Log.i(getClass().getName(), "GPS location changed: lat="+arg0.getLatitude()+", lon="+arg0.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}

	int satellitesSeen = 0;
	
	@Override
	public void onGpsStatusChanged(int arg0) {
		switch (arg0) {
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			GpsStatus status = lm.getGpsStatus(null);
			int i = 0;
			for (GpsSatellite gps : status.getSatellites()) {
				i++;
			}
			if (i != satellitesSeen) {
				satellitesSeen = i;
				Log.i(getClass().getName(), satellitesSeen + " satellites seen");
			}
			break;
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			Log.i(getClass().getName(), "GPS fix acquired");
			break;
		case GpsStatus.GPS_EVENT_STARTED:
			Log.i(getClass().getName(), "GPS fired up");
			break;
		case GpsStatus.GPS_EVENT_STOPPED:
			Log.i(getClass().getName(), "GPS powered down");
			break;
		}	
	}

}
