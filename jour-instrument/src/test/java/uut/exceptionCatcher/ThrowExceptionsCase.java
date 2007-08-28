package uut.exceptionCatcher;

import uut.Monitor;

public class ThrowExceptionsCase implements Runnable {

	public void throwMethod1() {
		
		Monitor.count += 1;
		
		if (Monitor.flag == 1) {
			throw new Error("thrown by Method1");
		} else {
			throw new IllegalArgumentException("Monitor disabled");
		}
		
	}

	public void run() {
		switch (Monitor.number) {
		default:
			throwMethod1();
		}
	}
}
