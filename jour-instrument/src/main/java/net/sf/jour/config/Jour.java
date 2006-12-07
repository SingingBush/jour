package net.sf.jour.config;

import java.util.List;

public class Jour {

	List aspect;

	boolean setSerialVersionUID;
	
	boolean debug;

	public List getAspect() {
		return aspect;
	}

	public void setAspect(List aspect) {
		this.aspect = aspect;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isSetSerialVersionUID() {
		return setSerialVersionUID;
	}

	public void setSetSerialVersionUID(boolean setSerialVersionUID) {
		this.setSerialVersionUID = setSerialVersionUID;
	}
}
