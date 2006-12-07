package net.sf.jour;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.Translator;
import net.sf.jour.instrumentor.Instrumentor;

public class InstrumentingTranslator implements Translator {

	private Config config;
	
	public InstrumentingTranslator(Config config) {
		this.config = config;
	}
	
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
		
	}
	
	public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
		Instrumentor[] instrumentors = config.getInstrumentors(classname);
        if (instrumentors.length > 0) {
            Interceptor interceptor = new Interceptor(this.config , pool, classname, instrumentors);
            interceptor.instrument();
        }
	}


}
