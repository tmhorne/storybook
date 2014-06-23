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

import javax.swing.JEditorPane;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.net.NetTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class BrowserPanel extends JEditorPane implements HyperlinkListener {

	private Timer timer;
	private String url;
	private int width;
	private int height;

	public BrowserPanel(String url) {
		this(url, 200, 200);
	}

	public BrowserPanel(String url, int width, int height) {
		super();
		this.url = url;
		this.width = width;
		this.height = height;
		setContentType("text/html");
		setEditable(false);
		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadPage();
			}
		});
		initGUI();
		addHyperlinkListener(this);
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("wrap,fill", "[]", "[]");
		setLayout(layout);
		setPreferredSize(new Dimension(width, height));
		timer.start();
	}

	private void loadPage() {
		try {
			setPage(url);
			timer.stop();
			SwingTools.setDefaultCursor(this);
		} catch (IOException e) {
			timer.stop();
			SwingTools.setDefaultCursor(this);
			setText(I18N.getMsg("msg.error.internet.connection.failed", url));
		}
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
