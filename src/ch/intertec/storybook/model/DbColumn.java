package ch.intertec.storybook.model;

import ch.intertec.storybook.toolkit.I18N;

public class DbColumn {
	final private String name;
	final private String resourceKey;

	public DbColumn(String name) {
		this(name, "");
	}

	public DbColumn(String name, String resourceKey) {
		this.name = name;
		this.resourceKey = resourceKey;
	}

	public String toString() {
		return name;
	}
	
	public String getI18Name() {
		return I18N.getMsg(resourceKey);
	}
}
