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

package ch.intertec.storybook.view.navigation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IconButton;
import ch.intertec.storybook.view.ViewTools;

@SuppressWarnings("serial")
public class FindChapterDialog extends javax.swing.JDialog {

	private AbstractAction closeAction;
	
	private static Integer lastChapterNo = 1;
	
	private JTextField tfChapterNo;
	private JLabel lbWarning;
	
	public FindChapterDialog(JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout(
				"wrap 2,fill",
				"[]10[grow]",
				"[]10[]10[]");
		setLayout(layout);
		setTitle(I18N.getMsg("msg.menu.navigate.goto.chapter"));
		setPreferredSize(new Dimension(250, 170));
		
		// date label
		JLabel lbDate = new JLabel();
		lbDate.setText(I18N.getMsgColon("msg.common.chapter"));
		
		// chapter
		tfChapterNo = new JTextField();
		tfChapterNo.setText(lastChapterNo.toString());
		SwingTools.selectAllText(tfChapterNo);

		// previous chapter
		IconButton btPrevious = new IconButton(
				"icon.small.arrow.left",
				null,
				getPreviousChapterAction());

		// next chapter
		IconButton btNext = new IconButton(
				"icon.small.arrow.right",
				null,
				getNextChapterAction());		

		// warning label
		lbWarning = new JLabel(" ");

		// find button
		JButton btFind = new JButton();
		btFind.setAction(getFindAction());
		btFind.setText(I18N.getMsg("msg.common.find"));
		btFind.setIcon(I18N.getIcon("icon.small.search"));
		SwingTools.addEnterAction(btFind, getFindAction());

		// close button
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		btClose.setIcon(I18N.getIcon("icon.small.close"));
		SwingTools.addEscAction(btClose, getCloseAction());
		
		// layout
		add(lbDate);
		add(tfChapterNo, "growx");
		add(btPrevious, "span,split 2,sg 2,al center");
		add(btNext, "sg 2");
		add(lbWarning, "span,wrap");
		add(btFind, "span,split 2,sg");
		add(btClose, "sg,gap push");
	}
	
	public void reset(){
		lastChapterNo = 1;
	}

	public JTextField getChapterNoTF() {
		return tfChapterNo;
	}
		
	private JDialog getThis() {
		return this;
	}
	
	private void scrollToChapter() {
		int chapterNo = 0;
		try {
			chapterNo = Integer.parseInt(tfChapterNo.getText());
		} catch (NumberFormatException e) {
			// ignore
		}
		boolean found = ViewTools.scrollToChapter(chapterNo);
		if (!found) {
			lbWarning.setText(I18N.getMsg("msg.dlg.navigation.chapter.not.found"));
		} else {
			lbWarning.setText(" ");
		}
	}

	private AbstractAction getNextChapterAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					lastChapterNo = Integer.parseInt(tfChapterNo.getText());
					++lastChapterNo;
					tfChapterNo.setText(lastChapterNo.toString());
					scrollToChapter();
				} catch (NumberFormatException e) {
					tfChapterNo.setText(lastChapterNo.toString());
					lbWarning.setText(I18N.getMsg("msg.common.error"));
				}
			}
		};
	}

	private AbstractAction getPreviousChapterAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					lastChapterNo = Integer.parseInt(tfChapterNo.getText());
					--lastChapterNo;
					tfChapterNo.setText(lastChapterNo.toString());
					scrollToChapter();
				} catch (NumberFormatException e) {
					tfChapterNo.setText(lastChapterNo.toString());
					lbWarning.setText(I18N.getMsg("msg.common.error"));
				}
			}
		};
	}

	private AbstractAction getFindAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				scrollToChapter();
			}
		};
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
