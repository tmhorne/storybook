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

package ch.intertec.storybook.view.content.manage;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Popup;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IconButton;

@SuppressWarnings("serial")
public class TextLengthChooser extends JPanel implements FocusListener {

	private Popup popup;
	private ITextLengthChooserService service;

	public TextLengthChooser(ITextLengthChooserService service) {
		super();
		this.service = service;
		setEnabled(true);
		setFocusable(true);
		initGUI();
		addFocusListener(this);
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("flowy");
		setLayout(layout);
		setBorder(SwingTools.getBorderDefault());

		IconButton btSmall = new IconButton("icon.small.size.small",
				getSmallAction());
		IconButton btMedium = new IconButton("icon.small.size.medium",
				getMediumAction());
		IconButton btLarge = new IconButton("icon.small.size.large",
				getLargeAction());

		add(btSmall);
		add(btMedium);
		add(btLarge);
		add(new JButton(getCancelAction()), "al center");
		SwingTools.addEscAction(this, getCancelAction());
		requestFocusInWindow();
	}
	
	private AbstractAction getSmallAction(){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTextLength(30);
				popup.hide();
			}
		};
	}

	private AbstractAction getMediumAction(){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTextLength(128);
				popup.hide();
			}
		};
	}

	private AbstractAction getLargeAction(){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTextLength(256);
				popup.hide();
			}
		};
	}

	private void setTextLength(int textLength) {
		service.setTextLength(textLength);
	}
	
	public Popup getPopup() {
		return popup;
	}

	public void setPopup(Popup popup) {
		this.popup = popup;
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		popup.hide();
	}
	
	private TextLengthChooser getThis() {
		return this;
	}

	private AbstractAction getCancelAction() {
		return new AbstractAction(I18N.getMsg("msg.common.cancel")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				getThis().getPopup().hide();
			}
		};
	}
}
