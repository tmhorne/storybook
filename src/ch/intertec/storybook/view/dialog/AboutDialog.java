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

package ch.intertec.storybook.view.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Properties;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.Internal;
import ch.intertec.storybook.model.InternalPeer;
import ch.intertec.storybook.model.Preference;
import ch.intertec.storybook.model.PreferencePeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.DateTools;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.ReadOnlyTable;
import ch.intertec.storybook.toolkit.swing.SwingTools;

/**
 * The about dialog shows the copyright, credits and some internal information
 * that may help to support clients.
 * 
 * @author martin
 * 
 */
@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	// actions
	private AbstractAction closeAction;

	private final String gpl = "<html>" + Constants.Application.NAME
			+ " is a free, open source novel-writing tool "
			+ "for creative writers, novelists and authors which "
			+ "will help you to keep an overview of multiple plot-lines "
			+ "while writing books, novels or other written works. "
			+ Constants.Application.NAME
			+ " assists you in structuring your book"
	
			+ "<p>Copyright (C) " + Constants.Application.COPYRIGHT_YEAR + " "
			+ Constants.Application.COPYRIGHT_AUTHOR
			+ "<br>Product Homepage: www.novelist.ch"

			+ "<p>" + Constants.Application.NAME
			+ " is free software: you can redistribute "
			+ "it and/or modify it under the terms of the "
			+ "GNU General Public License as published by the "
			+ "Free Software Foundation, either version 3 of the "
			+ "License, or (at your option) any later version."

			+ "<p>" + Constants.Application.NAME
			+ " is distributed in the hope that it will be "
			+ "useful, but WITHOUT ANY WARRANTY; without even the "
			+ "implied warranty of MERCHANTABILITY or FITNESS FOR "
			+ "A PARTICULAR PURPOSE.  See the GNU General Public "
			+ "License for more details."
			+ "<p>You should have received a copy of the GNU "
			+ "General Public License along with " + Constants.Application.NAME
			+ ". " + "If not, see http://www.gnu.org/licenses/";

	private final String credits = "<html>"
			+ "<h2>Developers</h2>"
			+ "Martin Mustun"
			+ "<h2>Logo Designer</h2>"
			+ "Jose Campoy"
			+ "<h2>Translators</h2>"
			+ "Brazilian Portuguese: <b>vacant</b>"
			+ "<br>Czech: Jan Drbohlav"
			+ "<br>Dutch: Frans van Dijk"	
			+ "<br>Danish: Claus Jensby Madsen"
			+ "<br>English: Martin Mustun"
			+ "<br>English Proof-Reading: Greg Scowen"
			+ "<br>Finnish: Christine Hammar"
			+ "<br>French: David Zysman"
			+ "<br>German: Martin Mustun"
			+ "<br>Greek: Elias Kalapanidas"
			+ "<br>Hebrew: Abraham"
			+ "<br>Italian: Alessandro Ranaldi"
			+ "<br>Japanese: Asakura Sumako"
			+ "<br>Polish: Mark"
			+ "<br>Russian: Aleĉjo fon Zaroviĉ"
			+ "<br>Simplified Chinese: June"
			+ "<br>Spanish: Nacho Blanco"
			+ "<br>Swedish: Hindrik"
			+ "<br>Traditional Chinese: WC Yan"
			+ "<p>&nbsp;";

	public AboutDialog() {
		super();
		initGUI();
	}

	public AboutDialog(JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout(
				"flowy",
				"[center]",
				"[]10[]10[]10[]");
		setLayout(layout);
		Container cp = getContentPane();
		cp.setBackground(Color.white);
		setPreferredSize(new Dimension(680, 650));

		// logo
		JLabel lbLogo = new JLabel((ImageIcon) I18N.getIcon("icon.logo.500"));
		lbLogo.setOpaque(true);
		lbLogo.setBackground(Color.WHITE);

		// application info
		JLabel lbInfo = new JLabel();
		JLabel lbReview = new JLabel("");
		StringBuffer buf = new StringBuffer();
		if (Constants.Application.IS_PRO_VERSION.toBoolean()) {
			buf.append(Constants.Application.NAME_PRO);
			buf.append(" - Version " + Constants.Application.VERSION_PRO);
		} else {
			buf.append(Constants.Application.NAME);
			buf.append(" - Version " + Constants.Application.VERSION);
		}
		buf.append(" - Released on " + Constants.Application.RELEASE_DATE);
		lbInfo.setText(buf.toString());

		if (Constants.Application.IS_FOR_REVIEW.toBoolean()) {
			lbReview.setText("This version is for journalistic review only. "
					+ "It will expire on "
					+ DateTools.calendarToString(Constants.expireDate));
		}

		JTabbedPane pane = new JTabbedPane();

		// licenses
		JTextPane taGpl = new JTextPane();
		taGpl.setContentType("text/html");
		taGpl.setEditable(false);
		taGpl.setText(gpl);
		JScrollPane scroller1 = new JScrollPane(taGpl);
		scroller1.setBorder(SwingTools.getEtchedBorder());
		pane.addTab("Copyright (GPL)", scroller1);

		// credits
		JTextPane taCredits = new JTextPane();
		taCredits.setContentType("text/html");
		taCredits.setEditable(false);
		taCredits.setText(credits);
		JScrollPane scroller2 = new JScrollPane(taCredits);
		scroller2.setBorder(SwingTools.getEtchedBorder());
		pane.addTab("Credits", scroller2);

		// preference table
		pane.addTab("Preferences", createPreferenceInfoTable());

		// internal table
		if (ProjectTools.isProjectOpen()) {
			pane.addTab("Internal Project Values", createInternalInfoTable());
		}

		// system properties
		pane.addTab("System Properties", createPropertiesScrollPane());
		
		// close button
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		SwingTools.addEnterAction(btClose, getCloseAction());
		SwingTools.addEscAction(btClose, getCloseAction());

		// layout
		add(lbLogo);
		add(lbInfo);
		add(lbReview);
		add(pane, "grow");
		add(btClose, "right");

		taCredits.setCaretPosition(0);
		taGpl.setCaretPosition(0);
	}

	private JScrollPane createInternalInfoTable() {
		String[] columnNames = { "Key", "String", "Integer", "Boolean" };
		int size = InternalPeer.doCount();
		Object[][] data = new Object[size][4];
		int c = 0;
		for (Internal internal : InternalPeer.doSelectAll()) {
			data[c][0] = internal.getKey();
			data[c][1] = internal.getStringValue();
			data[c][2] = internal.getIntegerValue();
			data[c][3] = internal.getBooleanValue();
			++c;
		}
		JTable table = new ReadOnlyTable(data, columnNames);
		JScrollPane scroller = new JScrollPane(table);
		scroller.setBorder(SwingTools.getEtchedBorder());
		return scroller;
	}

	private JScrollPane createPreferenceInfoTable() {
		String[] columnNames = { "Key", "String", "Integer", "Boolean" };
		int size = PreferencePeer.doCount();
		Object[][] data = new Object[size][4];
		int c = 0;
		for (Preference pref : PreferencePeer.doSelectAll()) {
			data[c][0] = pref.getKey();
			data[c][1] = pref.getStringValue();
			data[c][2] = pref.getIntegerValue();
			data[c][3] = pref.getBooleanValue();
			++c;
		}
		JTable table = new ReadOnlyTable(data, columnNames);
		JScrollPane scroller = new JScrollPane(table);
		scroller.setBorder(SwingTools.getEtchedBorder());
		return scroller;
	}

	private JScrollPane createPropertiesScrollPane() {
		JTextArea ta = new JTextArea();
		ta.setEditable(false);
		ta.setLineWrap(true);
		Properties props = System.getProperties();
		Set<Object> keys = props.keySet();
		for (Object key : keys) {
			ta.append(key.toString());
			ta.append(": ");
			ta.append(props.getProperty(key.toString()));
			ta.append("\n");
		}
		ta.setCaretPosition(0);
		return new JScrollPane(ta);
	}
	
	private JDialog getThis() {
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
}
