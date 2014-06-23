/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

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

package ch.intertec.storybook.view.chronoold;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.view.IconButton;

@SuppressWarnings("serial")
public class LockContentPanel extends JPanel {

	public final String PROPERTY_LOCKED = "locked";
	
	private boolean locked = true;
	
	public LockContentPanel() {
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout(
				"insets 0,fill",
				"[center]",
				"[]"
				);
		setLayout(layout);
		
		AbstractAction action;
		String icon;
		if (locked) {
			action = getUnlockAction();
			icon = "icon.small.lock";
		} else {
			action = getLockAction();
			icon = "icon.small.unlock";
		}
		IconButton btLock = new IconButton(icon, action);
		btLock.setSize20x20();
		add(btLock, "right");
	}

	public void lock() {
		locked = true;
		firePropertyChange(PROPERTY_LOCKED, false, true);
		refresh();
	}

	public void unlock() {
		locked = false;
		firePropertyChange(PROPERTY_LOCKED, true, false);
		refresh();
	}
	
	private AbstractAction getLockAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().lock();
			}
		};
	}

	private AbstractAction getUnlockAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().unlock();
			}
		};
	}
	
	private LockContentPanel getThis() {
		return this;
	}

	private void refresh() {
		removeAll();
		initGUI();
		validate();
		repaint();
	}
}
