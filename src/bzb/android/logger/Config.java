/**
 * 
 */
package bzb.android.logger;

/**
 * @author bzb
 *
 */
public abstract class Config {

	public static final int MEDIA_LOG_BOTH = 0;
	public static final int MEDIA_LOG_AUDIO = 1;
	public static final int MEDIA_LOG_NONE = 2;
	
	public static int logMedia = MEDIA_LOG_BOTH;
	public static boolean logSensors = true;
	public static boolean logGps = true;
	
}
