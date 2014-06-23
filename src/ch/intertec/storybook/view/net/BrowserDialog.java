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

package ch.intertec.storybook.view.net;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.net.NetTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class BrowserDialog extends JDialog implements HyperlinkListener {

	private JPanel panel;
	private AbstractAction closeAction;
	private Timer timer;
	private String url;
	private int width;
	private int height;
	private String title;
	private boolean updateDialog;

	public BrowserDialog(String title, String url) {
		this(title, url, 700, 550);
	}
	
	public BrowserDialog(String title, String url, int width, int height) {
		this(title, url, width, height, false);
	}
	
	public BrowserDialog(String title, String url,
			int width, int height, boolean isUpdateDialog) {
		super();
		this.url = url;
		this.width = width;
		this.height = height;
		this.title = title;
		this.updateDialog = isUpdateDialog;
		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadPage();
			}
		});
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout(
				"wrap,fill",
				"[]",
				"[grow,center][]");
		setLayout(layout);
		setPreferredSize(new Dimension(width, height));
		setTitle(title);
		SwingTools.setWaitCursor(this);

		panel = new JPanel(new MigLayout("fill"));

		// close button
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		SwingTools.addEnterAction(btClose, getCloseAction());
		SwingTools.addEscAction(btClose, getCloseAction());

		JButton btExit = new JButton();;
		if (updateDialog) {
			// exit button if this is a updater dialog			
			btExit.setAction(ActionRegistry.getInstance().getAction(
					SbAction.EXIT));
			btExit.setText(I18N.getMsg("msg.common.exit"));
		}
		
		// layout
		add(new JScrollPane(panel), "grow");
		if (isUpdateDialog()) {
			add(btExit, "split 2");
		}
		add(btClose, "gap push");

		// start timer
		timer.start();
	}

	private void loadPage(){
		try{
			timer.stop();
			JEditorPane pane = new JEditorPane(url);
			pane.setEditable(false);
			pane.addHyperlinkListener(this);
			panel.removeAll();
			panel.add(pane, "grow");
			panel.validate();
			panel.repaint();
			pack();
			SwingTools.setDefaultCursor(this);
		} catch (IOException e) {
			timer.stop();
			SwingTools.setDefaultCursor(this);
			JOptionPane.showMessageDialog(
					this,
					I18N.getMsg("msg.error.internet.connection.failed", url),
					I18N.getMsg("msg.common.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public boolean isUpdateDialog() {
		return updateDialog;
	}
	
	protected JDialog getThis() {
		return this;
	}

	private AbstractAction getCloseAction() {
		if (closeAction == null) {
			closeAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					getThis().dispose();
				}
			};
		}
		return closeAction;
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				NetTools.openBrowser(evt.getURL().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
