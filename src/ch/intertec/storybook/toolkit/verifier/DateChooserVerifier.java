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

package ch.intertec.storybook.toolkit.verifier;

import javax.swing.JComponent;

import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.panel.DateChooser;

public class DateChooserVerifier extends AbstractInputVerifier {

	@Override
	public boolean verify(JComponent comp) {
		if (comp instanceof DateChooser) {
			DateChooser dateChooser = (DateChooser) comp;
			if (dateChooser.hasError()) {
				setErrorResource("msg.verifier.wrong.format");
				dateChooser.setBorder(SwingTools.getBorderRed());				
				return false;
			}
			if (dateChooser.getAllowEmptyValue() && dateChooser.getDate() == null) {
				clearError();
				return true;
			}
			if (dateChooser.getDate() != null) {
				dateChooser.setBorder(null);
				clearError();
				return true;
			}
			setErrorResource("msg.verifier.wrong.format");
			dateChooser.setBorder(SwingTools.getBorderRed());
			return false;
		}
		return false;
	}
}
