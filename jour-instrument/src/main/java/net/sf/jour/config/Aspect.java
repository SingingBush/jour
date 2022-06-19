package net.sf.jour.config;

import java.util.List;
import java.util.Vector;

public class Aspect {

	private String type;

    private String descr;

    private String typedef;

    private List<Pointcut> pointcut;

    private List<AspectProperty> properties;

    private boolean enabled;

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

	public List<Pointcut> getPointcut() {
		return pointcut;
	}

	public void setPointcut(List<Pointcut> pointcut) {
		this.pointcut = pointcut;
	}

	public void addPointcut(final Pointcut pointcut) {
		if (this.pointcut == null) {
			this.pointcut = new Vector<>();
		}
		this.pointcut.add(pointcut);
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

	public List<AspectProperty> getProperty() {
		return properties;
	}

	public void setProperty(List<AspectProperty> properties) {
		this.properties = properties;
	}


	public void addProperty(AspectProperty property) {
		if (this.properties == null) {
			this.properties = new Vector<>();
		}
		this.properties.add(property);
	}
}
