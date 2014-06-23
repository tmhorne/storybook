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

package ch.intertec.storybook.main.toolbar;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JToggleButton;

import ch.intertec.storybook.toolkit.I18N;

public class ViewItem {
	private String text;
	private Icon icon;
	private AbstractAction viewAction;

	public ViewItem(String textKey, String iconKey, AbstractAction viewAction) {
		this.text = I18N.getMsg(textKey);
		this.icon = I18N.getIcon(iconKey);
		this.viewAction = viewAction;
	}

	public String toString() {
		return getText();
	}

	public String getText() {
		return text;
	}

	public Icon getIcon() {
		return icon;
	}

	public AbstractAction getViewAction() {
		return viewAction;
	}

	public JToggleButton getJButton() {
		return getToggleButton(true);
	}

	public JToggleButton getToggleButton(boolean withText) {
		JToggleButton bt = new JToggleButton(getViewAction());
		if (withText) {
			bt.setText(getText());
		}
		bt.setToolTipText(getText());
		bt.setIcon(getIcon());
		return bt;
	}
}
