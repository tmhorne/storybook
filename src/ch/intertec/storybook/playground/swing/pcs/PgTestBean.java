/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008-2009 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.intertec.storybook.playground.swing.pcs;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class PgTestBean implements Serializable {
	private static final long serialVersionUID = 4372536553610053222L;

	public static final String PROPERTY_TEXT = "text";
	public static final String PROPERTY_AGE = "age";
	private String text;
	private int age;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		String old = this.text;
		this.text = text;
		this.pcs.firePropertyChange(PROPERTY_TEXT, old, text);
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		int old = this.age;
		this.age = age;
		this.pcs.firePropertyChange(PROPERTY_AGE, old, age);
		PropertyChangeListener[] pcls = pcs.getPropertyChangeListeners();
		System.out.println();
		System.out.println("PropertyChangeListeners");
		System.out.println(pcls);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(propertyName, listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
}
