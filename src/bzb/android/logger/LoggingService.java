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
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * @author bzb
 *
 */
public class LoggingService extends Service implements SensorEventListener {

	private PrintWriter captureFile;

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
		
		startLogging();
	}

	public void onDestroy() {
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
    		
    		int i = 0;
    		for (Sensor sensor : sensors) {
    			sensorManager.registerListener( 
                        this, 
                        sensor,
                        SensorManager.SENSOR_DELAY_UI );
    			Log.i(getClass().getName(),"Registered listener for sensor " + sensor.getName());
    			captureFile.println(sensor.getType() + "," + sensor.getName() + "," + sensor.getResolution() + "," + sensor.getMaximumRange() + "," + sensor.getPower());
    		}
    		
        } catch( IOException ex ) {
            Log.e( getClass().getName(), ex.getMessage(), ex );
        }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		/*StringBuilder b = new StringBuilder();
        for( int i = 0 ; i < event.values.length ; ++i ) {
            if( i > 0 )
                b.append( " , " );
            b.append( Float.toString( event.values[i] ) );
        }*/
        captureFile.print(event.sensor.getType() + ",");
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

}
