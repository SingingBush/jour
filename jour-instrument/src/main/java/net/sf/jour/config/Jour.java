package net.sf.jour.config;

import java.util.List;

public class Jour {

	List aspect;

	boolean isSetSerialVersionUID;
	
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
		return isSetSerialVersionUID;
	}

	public void setSetSerialVersionUID(boolean setSerialVersionUID) {
		this.isSetSerialVersionUID = setSerialVersionUID;
	}
}
