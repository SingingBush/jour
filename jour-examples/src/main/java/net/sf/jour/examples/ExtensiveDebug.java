package net.sf.jour.examples;

/*
* The generated class file will have code changes based on the configuration in
* the process-classes.jour.xml file in the root of the jour-examples module.
*/
public class ExtensiveDebug {

	private boolean debugMethodBodyExists = false;

    public ExtensiveDebug() {}

	public ExtensiveDebug(String param) {
		debug("ExtensiveDebug created", param);
	}

    // will have method body removed by MakeEmptyMethodInstrumentor
    public void debug(String message, String v) {
        debugMethodBodyExists = true;
		System.out.println(message + " " + v);
	}

    public boolean debugMethodBodyRemoved() {
        return !debugMethodBodyExists;
    }

    // will have body replaced by ReplaceMethodInstrumentor
    public boolean replaceMe() {
        return false;
    }

    // will have body replaced by ReplaceMethodInstrumentor
    public String replaceThis() {
        return "original text";
    }

    // will output execution time due to MethodExecutionTimeInstrumentor
    public void timed() {
        System.out.println("This should be timed");
    }
}
