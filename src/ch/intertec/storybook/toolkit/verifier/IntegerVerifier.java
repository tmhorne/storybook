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
import javax.swing.JTextField;

import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

public class IntegerVerifier extends AbstractInputVerifier {

	private boolean onlyPositiveNumbers;

	public IntegerVerifier(){
		this(false);
	}
	
	public IntegerVerifier(boolean onlyPositiveNumbers){
		super(false);
		this.onlyPositiveNumbers = onlyPositiveNumbers;
	}

	public IntegerVerifier(boolean onlyPositiveNumbers, boolean acceptEmty){
		super(acceptEmty);
		this.onlyPositiveNumbers = onlyPositiveNumbers;
	}
	
	@Override
	public boolean verify(JComponent comp) {
		if(super.verify(comp)){
			return true;
		}
		if (comp instanceof JTextField) {
			JTextField tf = (JTextField) comp;
			try {
				int i = Integer.parseInt(tf.getText());
				if(onlyPositiveNumbers){
					if (i < 0) {
						throw new NumberFormatException(I18N.getMsg("msg.verifier.integer.positive"));
					}
				}
				comp.setBackground(SwingTools.getWarningGreen());
				clearError();
				return true;
			} catch (NumberFormatException e) {
				comp.setBackground(SwingTools.getWarningRed());
				setError(I18N.getMsg("msg.verifier.wrong.format") + " " + e.getLocalizedMessage());
			}
		}
		return false;
	}
}
