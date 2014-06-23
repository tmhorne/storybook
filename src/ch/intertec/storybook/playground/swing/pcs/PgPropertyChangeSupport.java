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

package ch.intertec.storybook.playground.swing.pcs;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ch.intertec.storybook.toolkit.swing.SwingTools;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PgPropertyChangeSupport extends JFrame implements PropertyChangeListener {

	private JTextField tfText;
	private JTextField tfAge;
	private PgTestBean bean;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new PgPropertyChangeSupport();
			}
		});
	}

	public PgPropertyChangeSupport() {
		super();
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initGUI();
		setVisible(true);
	}

	private void initGUI() {
		setLayout(new MigLayout("wrap 2"));

		JLabel lbText = new JLabel("Text: ");
		tfText = new JTextField(20);
		
		JLabel lbAge = new JLabel("Age: ");
		tfAge = new JTextField(20);
		
		bean = new PgTestBean();
		bean.addPropertyChangeListener(PgTestBean.PROPERTY_TEXT,
				this);
		bean.addPropertyChangeListener(PgTestBean.PROPERTY_AGE,
				this);
		
		JButton btChangeText = new JButton(getChangeTextAction());
		JButton btChangeAge = new JButton(getChangeAgeAction());
		JButton btSave = new JButton(getSaveAction());
		JButton btDiscard = new JButton(getDiscardAction());
		JButton btOpenNewDialog = new JButton(getOpenNewDialogAction());

		add(lbText);
		add(tfText);
		add(lbAge);
		add(tfAge);
		add(btChangeText, "span,split 2,sg");
		add(btChangeAge, "sg,gap bottom 10");
		add(btSave, "span,split 2,sg 2");
		add(btDiscard, "sg 2,gap bottom 10");
		add(btOpenNewDialog, "span");
	}

	private AbstractAction getOpenNewDialogAction() {
		return new AbstractAction("new dialog") {
			public void actionPerformed(ActionEvent evt) {
				PgTestDialog dlg = new PgTestDialog(getThis());
				bean.addPropertyChangeListener(dlg);
				SwingTools.showDialog(dlg, getThis());
			}
		};
	}
	
	private AbstractAction getSaveAction() {
		return new AbstractAction("save") {
			public void actionPerformed(ActionEvent evt) {
				bean.setText(tfText.getText());
				bean.setAge(Integer.parseInt(tfAge.getText()));
			}
		};
	}
	
	private AbstractAction getDiscardAction() {
		return new AbstractAction("discard") {
			public void actionPerformed(ActionEvent evt) {
				tfText.setText(bean.getText());
				tfAge.setText("" + bean.getAge());
			}
		};
	}

	private AbstractAction getChangeTextAction() {
		return new AbstractAction("change text") {
			public void actionPerformed(ActionEvent evt) {
				String text = bean.getText();
				bean.setText(text + " x");
			}
		};
	}
	
	private AbstractAction getChangeAgeAction() {
		return new AbstractAction("change age") {
			public void actionPerformed(ActionEvent evt) {
				int age = bean.getAge();
				bean.setAge(++age);
			}
		};
	}
	
	private JFrame getThis() {
		return this;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("source: " + evt.getSource());
		if(evt.getSource() instanceof PgTestBean){
			PgTestBean bean = (PgTestBean)evt.getSource();
			if(PgTestBean.PROPERTY_TEXT.equals(evt.getPropertyName())){
				tfText.setText(bean.getText());
			} else if(PgTestBean.PROPERTY_AGE.equals(evt.getPropertyName())){
				tfAge.setText("" + bean.getAge());
			}			
		}
	}
}
