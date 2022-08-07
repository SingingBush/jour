package uut.makecalls;

import java.util.List;

public interface EachMethodCall<T> {

	void callEachMethod();

	List<T> getMethodsCalled();
}
