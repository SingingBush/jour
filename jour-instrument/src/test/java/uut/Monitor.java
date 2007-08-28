package uut;

/**
 * A non instrumented class
 * 
 * @author vlads
 *
 */
public class Monitor {

	public static volatile int count;
	
	public static volatile int number;
	
	public static volatile int flag;
	
	public static volatile Throwable caught;
	
	public static void caught(Throwable caught) {
		Monitor.caught = caught;
	}
	
}
