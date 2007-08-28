package net.sf.jour.config;

import java.util.List;

public class Aspect {

	String type;

	String descr;

	String typedef;

	List pointcut;
	
	List properties;

	boolean enabled;
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public List getPointcut() {
		return pointcut;
	}

	public void setPointcut(List pointcut) {
		this.pointcut = pointcut;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypedef() {
		return typedef;
	}

	public void setTypedef(String typedef) {
		this.typedef = typedef;
	}

	public List getProperty() {
		return properties;
	}

	public void setProperty(List properties) {
		this.properties = properties;
	}
}
