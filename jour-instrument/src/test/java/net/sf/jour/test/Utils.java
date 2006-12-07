package net.sf.jour.test;

import java.net.URL;

import javassist.ClassPool;
import javassist.NotFoundException;

public class Utils {

	public static String getClassResourceName(String testClassName) {
		return "/" + testClassName.replace('.', '/') + ".class";
	}
	
	public static String getClassResourcePath(String testClassName) {
		return getResourcePath(getClassResourceName(testClassName));
	}

	public static String getResourcePath(String resource) {
	    URL url = Utils.class.getResource(resource); 
	    if (url == null) {
	    	throw new Error("Resource not found " + resource);
	    }
	    String path = url.toExternalForm();
	    return path.substring("file:".length(), path.length() - resource.length());
	}
	
	public static String getResourceAbsolutePath(String resource) {
	    URL url = Utils.class.getResource(resource); 
	    if (url == null) {
	    	throw new Error("Resource not found " + resource);
	    }
	    String path = url.toExternalForm();
	    return path.substring("file:".length(), path.length());
	}
	
	public static ClassPool getClassPool(String testClassName) {
		ClassPool pool = ClassPool.getDefault();
	    try {
			pool.appendClassPath(getClassResourcePath(testClassName));
		} catch (NotFoundException e) {
			throw new Error(e);
		}
	    return pool;
	}
}
