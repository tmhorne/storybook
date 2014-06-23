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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

public class PgTestDialog extends JDialog implements PropertyChangeListener {
	private static final long serialVersionUID = -4215152485850891424L;

	private JLabel lbText;
	private JLabel lbAge;

	public PgTestDialog(JFrame frame) {
		super(frame, "test dialog");
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("wrap");
		setLayout(layout);

		lbText = new JLabel();
		lbText.setText("Text: init");

		lbAge = new JLabel();
		lbAge.setText("Age: init");

		add(lbText);
		add(lbAge);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof PgTestBean) {
			PgTestBean bean = (PgTestBean) evt.getSource();
			lbText.setText("Text: " + bean.getText());
			lbAge.setText("Age: " + bean.getAge());
		}
	}
}
