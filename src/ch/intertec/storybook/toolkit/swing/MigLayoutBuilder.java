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

package ch.intertec.storybook.toolkit.swing;

import javax.swing.JComponent;

import net.miginfocom.swing.MigLayout;

/**
 * @author jorgen
 */
public class MigLayoutBuilder {
	private JComponent component;

	/**
	 * Constructor, a parent component is needed.
	 * 
	 * @param component the parent component
	 */
	public MigLayoutBuilder(JComponent component) {
		this.component = component;
	}

	/**
	 * Replaces a component with another component using the original components
	 * constraints. (could have been static with a getParent() check if
	 * preferred.)
	 * 
	 * @param oldComponent
	 * @param newComponent
	 * @return true if it replaced it false if it couldn't find it
	 * @throws IllegalStateException
	 *             if the layout of the component isn't MigLayout
	 */
	public boolean replaceComponent(JComponent oldComponent,
			JComponent newComponent) {
		boolean res = false;
		if (component.getLayout() instanceof MigLayout) {
			Object constraint = ((MigLayout) component.getLayout())
					.getComponentConstraints(oldComponent);
			if (constraint != null) {
				int compCount = component.getComponentCount();
				for (int i = 0; i < compCount - 1; i++) {
					if (component.getComponent(i) == oldComponent) {
						component.add(newComponent, constraint, i);
						component.remove(oldComponent);
						component.revalidate();
						res = true;
						break;
					}
				}
			}
		} else {
			throw new IllegalStateException("Unsupported Layout "
					+ component.getLayout().getClass().getName());
		}
		return res;
	}
}
