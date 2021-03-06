package net.sf.jour.config;

import java.util.List;
import java.util.Vector;

public class Jour {

	List<Aspect> aspect;

	boolean isSetSerialVersionUID;

	boolean debug;

	public List<Aspect> getAspect() {
		return aspect;
	}

	public void setAspect(List<Aspect> aspect) {
		this.aspect = aspect;
	}

	public void addAspect(Aspect aspect) {
		if (this.aspect == null) {
			this.aspect = new Vector<>();
		}
		this.aspect.add(aspect);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isSetSerialVersionUID() {
		return isSetSerialVersionUID;
	}

	public void setSetSerialVersionUID(boolean setSerialVersionUID) {
		this.isSetSerialVersionUID = setSerialVersionUID;
	}
}
