package ch.intertec.storybook.view.model.dbtable;

public class DbValue {
	public static enum Permission {
		EDITABLE, NON_EDITABLE
	}

	private Permission permission;
	private String value;

	public DbValue(String value) {
		this.value = value;
		this.permission = Permission.EDITABLE;
	}

	public DbValue(String value, Permission permission) {
		this.value = value;
		this.permission = permission;
	}

	public Permission getPermission() {
		return this.permission;
	}

	public boolean isEditable() {
		return this.permission == Permission.EDITABLE;
	}

	public String toString() {
		return value;
	}
}
