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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.toolkit.HtmlTools;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;

/**
 * @author martin
 * 
 */
@SuppressWarnings("serial")
public class FileInfoDialog extends JDialog {

	private JButton btOk;
	private JTextPane tpInfo;

	public FileInfoDialog() {
		super();
		initGUI();
	}

	public FileInfoDialog(JFrame frame) {
		super(frame);
		initGUI();
	}

	@SuppressWarnings("deprecation")
	private void initGUI() {
		MigLayout layout = new MigLayout("wrap,fill", "[]", "[]");
		setLayout(layout);
		setTitle(I18N.getMsg("msg.file.info"));

		int textLength = 0;
		int words = 0;
		List<Scene> scenes = ScenePeer.doSelectAll();
		for (Scene scene : scenes) {
			String summary = scene.getText();
			textLength += summary.length();
			words += HtmlTools.countWords(summary);
		}

		File file = ProjectTools.getCurrentFile();
		
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append(HtmlTools.getHeadWithCSS());
		buf.append("<body><table>");
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.file.info.filename"),
				file.toString()));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.file.info.last.mod"),
				new Date(file.lastModified()).toLocaleString()));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.file.info.text.length"),
				Integer.toString(textLength)));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.file.info.words"),
				Integer.toString(words)));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.common.parts"),
				Integer.toString(PartPeer.doCount())));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.common.chapters"),
				Integer.toString(ChapterPeer.doCount())));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.common.scenes"),
				Integer.toString(ScenePeer.doCount())));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.common.persons"),
				Integer.toString(SbCharacterPeer.doCount())));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.menu.locations"),
				Integer.toString(LocationPeer.doCount())));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.tags"),
				Integer.toString(TagPeer.doCount())));
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.items"),
				Integer.toString(ItemPeer.doCount())));
		buf.append("</table></body></html>");

		tpInfo = new JTextPane();
		tpInfo.setContentType("text/html");
		tpInfo.setEditable(false);
		tpInfo.setMinimumSize(new Dimension(400, 300));
		tpInfo.setText(buf.toString());
		tpInfo.setBorder(SwingTools.getEtchedBorder());
		JPopupMenu popup = new JPopupMenu();
		SwingTools.addCopyToPopupMenu(popup, tpInfo);
		tpInfo.setComponentPopupMenu(popup);

		// copy text
		JButton btCopyText = new JButton();
		btCopyText.setAction(getCopyTextAction());
		btCopyText.setText(I18N.getMsg("msg.file.info.copy.text"));
		btCopyText.setIcon(I18N.getIcon("icon.small.copy"));

		// OK button
		btOk = new JButton();
		btOk.setAction(getOkAction());
		SwingTools.addEnterAction(btOk, getOkAction());
		SwingTools.addEscAction(btOk, getOkAction());
		btOk.setText(I18N.getMsg("msg.common.ok"));

		// layout
		add(tpInfo, "grow");
		add(btCopyText, "sg,left,split 2,span");
		add(btOk, "sg,gap push,gapy 10");
	}

	private FileInfoDialog getThis() {
		return this;
	}

	private AbstractAction getOkAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().dispose();
			}
		};
	}

	private AbstractAction getCopyTextAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				String selection = tpInfo.getText();
				StringSelection data = new StringSelection(selection);
				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clipboard.setContents(data, data);
			}
		};
	}
}
