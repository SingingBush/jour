package uut.makecalls;

import java.util.List;
import java.util.Vector;

public class EachMethodCallCase implements EachMethodCall<String> {

	private final List<String> methodsCalled;

	public EachMethodCallCase() {
		methodsCalled = new Vector<>();
	}

    @Override
	public void callEachMethod() {
	}

    @Override
	public List<String> getMethodsCalled() {
		return methodsCalled;
	}

	public void testMethod1() {
		methodsCalled.add("testMethod1");
	}

	public void testMethod2() {
		methodsCalled.add("testMethod2");
	}

	private void testMethod3() {
		methodsCalled.add("testMethod3");
	}

	public void doNotCallMe() {
		methodsCalled.add("doNotCallMe");
	}
}
