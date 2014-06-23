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

package ch.intertec.storybook.model;

import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class PCSDispatcher implements Serializable {

	private static PCSDispatcher theInstance = new PCSDispatcher();

	public enum Property {		
		STRAND("strand"),
		PART("part"),
		SCENE("scene"),
		SCENE_TITLE_SUMMARY("scene_ts"),
		CHAPTER("chapter"),
		LOCATION("location"),
		CHARACTER("character"),
		PROJECT("project"),
		ACTIVE_PART("active_part"),
		SCALE("scale"),
		VIEW("view"),
		STRAND_ORDER("strand_order"),
		REFRESH_ALL("refresh_all"),
		IDEAS("global_notes"),
		GENDER("gender"),
		TAG("tag"),
		ITEM("item");
		final private String text;
		private Property(String text) { this.text = text; }
		public String toString() { return text; }
	}

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private PCSDispatcher() {
	}
	
	public static PCSDispatcher getInstance() {
		return theInstance;
	}
		
	public void addPropertyChangeListener(Property property,
			PropertyChangeListener listener) {
		addPropertyChangeListener(property.toString(), listener);
	}
	
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		PropertyChangeListener[] listeners = pcs.getPropertyChangeListeners(propertyName);
		List<PropertyChangeListener> list = Arrays.asList(listeners);
		if (!list.contains(listener)) {
			this.pcs.addPropertyChangeListener(propertyName, listener);
		}
	}

	public void removePropertyChangeListener(Property property,
			PropertyChangeListener listener) {
		removePropertyChangeListener(property.toString(), listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(propertyName, listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		PropertyChangeListener[] listeners = pcs.getPropertyChangeListeners();
		List<PropertyChangeListener> list = Arrays.asList(listeners);
		if (!list.contains(listener)) {
			this.pcs.addPropertyChangeListener(listener);
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
	
	public void removeAllPropertyChangeListener(PropertyChangeListener listener) {
		removePropertyChangeListener(listener);
		for (Property prop : Property.values()) {
			removePropertyChangeListener(prop.toString(), listener);
		}
	}
	
	public void reset() {
		pcs = new PropertyChangeSupport(this);
	}
	
	public void removeListenersByContainer(Container container){
		if (container == null) {
			return;
		}
		PropertyChangeListener[] listeners = pcs.getPropertyChangeListeners();
		for (PropertyChangeListener listener : listeners) {
			if (listener instanceof PropertyChangeListenerProxy) {
				PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
				Component comp = (Component) proxy.getListener();
				if (container.isAncestorOf(comp)) {
					PropertyChangeListener listener2 = (PropertyChangeListener) proxy
							.getListener();
					removePropertyChangeListener(listener2);
					for (Property prop : Property.values()) {
						removePropertyChangeListener(prop.toString(), listener2);
					}
					listener2 = null;
				}
			} else {
				Component comp = (Component) listener;
				if (container.isAncestorOf(comp)) {
					removePropertyChangeListener(listener);
					for (Property prop : Property.values()) {
						removePropertyChangeListener(prop.toString(), listener);
					}
				}
			}
		}
	}
	
	public void removeListenersByClass(Class<?> c){
		if (c == null) {
			return;
		}
		PropertyChangeListener[] listeners = pcs.getPropertyChangeListeners();
		for (PropertyChangeListener listener : listeners) {
			if (listener instanceof PropertyChangeListenerProxy) {
				PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
				Object o = proxy.getListener();
				if (o.getClass() == c) {
					PropertyChangeListener listener2 = (PropertyChangeListener) proxy
							.getListener();
					removePropertyChangeListener(listener2);
					for (Property prop : Property.values()) {
						removePropertyChangeListener(prop.toString(), listener2);
					}
				}
			} else {
				Object o = listener;
				if (o.getClass() == c) {
					removePropertyChangeListener(listener);
					for (Property prop : Property.values()) {
						removePropertyChangeListener(prop.toString(), listener);
					}
				}				
			}
		}
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			boolean oldValue, boolean newValue) {
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			int oldValue, int newValue) {
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			Object oldValue, Object newValue) {
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	public void firePropertyChange(PropertyChangeEvent evt) {
		pcs.firePropertyChange(evt);
	}

	public void firePropertyChange(Property property, boolean oldValue,
			boolean newValue) {
		firePropertyChange(property.toString(), oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(Property property, int oldValue,
			int newValue) {
		firePropertyChange(property.toString(), oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(Property property, Object oldValue,
			Object newValue) {
		firePropertyChange(property.toString(), oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return pcs.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		return pcs.getPropertyChangeListeners(propertyName);
	}

	public static boolean isSource(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof PCSDispatcher) {
			return true;
		}
		return false;
	}
	
	public static boolean isPropertyFired(Property property,
			PropertyChangeEvent evt) {
		if (property.toString().equals(evt.getPropertyName())) {
			return true;
		}
		return false;
	}

	public static boolean isPropertyNew(Object oldValue, Object newValue) {
		if (oldValue == null) {
			return true;
		}
		return false;
	}

	public static boolean isPropertyNew(PropertyChangeEvent evt) {
		if (evt.getOldValue() == null) {
			return true;
		}
		return false;
	}

	public static boolean isPropertyRemoved(Object oldValue, Object newValue) {
		if (newValue == null) {
			return true;
		}
		return false;
	}
	
	public static boolean isPropertyRemoved(PropertyChangeEvent evt) {
		if (evt.getNewValue() == null) {
			return true;
		}
		return false;
	}

	public static boolean isPropertyEdited(Object oldValue, Object newValue) {
		if (oldValue != null && newValue != null) {
			return true;
		}
		return false;
	}
	
	public static boolean isPropertyEdited(PropertyChangeEvent evt) {
		if (evt.getOldValue() != null && evt.getNewValue() != null) {
			return true;
		}
		return false;
	}
	
	public void printInfos(){
		System.out.println(StringUtils.repeat("-", 20));
		for (Property prop : Property.values()) {
			if (prop != Property.PART) {
				continue;
			}
			PropertyChangeListener[] listeners = pcs
					.getPropertyChangeListeners(prop.toString());
			List<PropertyChangeListener> list = Arrays.asList(listeners);
			System.out.print("property: " + prop.toString());
			System.out.println(", size: " + list.size());
			Iterator<PropertyChangeListener> i = list.iterator();
			while (i.hasNext()) {
				System.out.println(i.next());
			}
		}
	}
}
