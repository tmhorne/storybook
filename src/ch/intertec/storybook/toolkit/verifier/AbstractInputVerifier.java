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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

public abstract class AbstractInputVerifier extends InputVerifier {

	private boolean acceptEmpty;
	private JLabel errorLb;
	
	public AbstractInputVerifier(){
		this(false);
	}
	
	public AbstractInputVerifier(boolean acceptEmpty){
		this.acceptEmpty = acceptEmpty;
	}

	public void setErrorLabel(JLabel errorLabel) {
		this.errorLb = errorLabel;
	}
	
	public JLabel getErrorLabel(){
		return errorLb;
	}
	
	public void clearError() {
		if (errorLb != null) {
			errorLb.setText("");
		}
	}
	
	public void setErrorResource(String resourceKey){
		if (errorLb != null) {
			errorLb.setText(I18N.getMsg(resourceKey));
		}
	}
	
	public void setError(String text) {
		if (errorLb != null) {
			errorLb.setText(text);
		}
	}
	
	@Override
	public boolean verify(JComponent comp) {
		if(comp instanceof JTextField){
			if(((JTextField)comp).getText().trim().isEmpty() && acceptEmpty){
				comp.setBackground(SwingTools.getWarningGreen());
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
}
