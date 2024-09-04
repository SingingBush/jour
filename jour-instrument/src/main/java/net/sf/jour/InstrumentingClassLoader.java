package net.sf.jour;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;

public class InstrumentingClassLoader extends Loader {

	Config config;

	InstrumentingTranslator translator;

	public InstrumentingClassLoader(String configFileName) {
		this(configFileName, null);
	}

	public InstrumentingClassLoader(String configFileName, String[] path) {
		super();
		init(configFileName, path);
	}

	public InstrumentingClassLoader(String configFileName, String[] path, ClassLoader parent) {
		super(parent, null);
		init(configFileName, path);
	}

	private void init(String configFileName, String[] path) {
		config = new Config(configFileName);
		translator = new InstrumentingTranslator(config);
		ClassPool pool = ClassPool.getDefault();
		try {
			for (int i = 0; (path != null) && (i < path.length); i++) {
				pool.appendClassPath(path[i]);
			}
			super.addTranslator(pool, translator);
		} catch (NotFoundException | CannotCompileException e) {
			throw new Error(e);
		}
    }

}
