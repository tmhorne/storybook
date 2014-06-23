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

package ch.intertec.storybook.view.modify;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.AbstractTableAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextField;
import ch.intertec.storybook.toolkit.verifier.IntegerVerifier;
import ch.intertec.storybook.toolkit.verifier.NonEmptyVerifier;
import ch.intertec.storybook.view.AbstractModifyDialog;

/**
 * Dialog for creating a new chapter.
 * 
 * @author martin
 *
 */

@SuppressWarnings("serial")
public class ChapterDialog extends AbstractModifyDialog {
	
	private UndoableTextField tfChapterNo;
	private UndoableTextField tfTitle;
	private JComboBox cobPart;
	private UndoableTextArea taDescription;
	private UndoableTextArea taNotes;

	public ChapterDialog(){
		super();
	}

	public ChapterDialog(JFrame frame) {
		super(frame);
	}

	public ChapterDialog(JFrame frame, Chapter chapter) {
		super(frame, chapter);
	}
	
	public ChapterDialog(Action action) {
		super(action);
	}
	
	@Override
	public void init(){
		// nothing to do
	}
	
	@Override
	protected MigLayout getMigLayout(){
		return new MigLayout(
			"wrap 2",
			"[]10[grow,fill]",
			"[]20[][][top]");
	}
	
	@Override
	protected void initGUI() {
		try {
			if (edit) {
				setTitle("msg.common.chapter.edit");
			} else {
				setTitle("msg.common.chapter.add");
			}

			NonEmptyVerifier neVerifier = new NonEmptyVerifier();
			neVerifier.setErrorLabel(getErrorLabel());
			
			IntegerVerifier intVerifier = new IntegerVerifier(true);
			intVerifier.setErrorLabel(getErrorLabel());

			// part
			JLabel lbPart = new JLabel(
					I18N.getMsgColon("msg.dlg.scene.part", true));
			cobPart = createPartComboBox();

			// next chapter number
			int nextChapterNo = ChapterPeer.getNextChapterNo();
			
			// chapter number
			JLabel lbChapterNo = new JLabel(I18N.getMsgColon("msg.dlg.chapter.number", true));
			tfChapterNo = new UndoableTextField();
			tfChapterNo.setText(String.valueOf(nextChapterNo));
			tfChapterNo.setInputVerifier(intVerifier);
			tfChapterNo.setBorder(
					BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			setFocusComponent(tfChapterNo);
			
			// title
			JLabel lbTitle = new JLabel(I18N.getMsgColon("msg.dlg.chapter.title", true));
			tfTitle = new UndoableTextField();
			tfTitle.setText(I18N.getMsg("msg.common.chapter") + " " + nextChapterNo);
			tfTitle.setInputVerifier(neVerifier);
			tfTitle.setBorder(
					BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			
			// fill in input list
			inputComponentList.add(tfChapterNo);
			inputComponentList.add(tfTitle);

			// layout
			panel.add(lbPart);
			panel.add(cobPart, "grow 0");
			panel.add(lbChapterNo);
			panel.add(tfChapterNo);
			panel.add(lbTitle);
			panel.add(tfTitle);
			
			// description
			taDescription = new UndoableTextArea();
			tabbedPane.addTab(I18N.getMsg("msg.dlg.location.description"),
					createDescrPanel(taDescription));
			tabbedPane.setIconAt(1, I18N.getIcon("icon.small.descr"));

			// notes
			taNotes = createNotesTextArea();
			tabbedPane.addTab(I18N.getMsg("msg.common.notes"),
					SwingTools.createNotesPanel(taNotes));
			tabbedPane.setIconAt(2, I18N.getIcon("icon.small.note"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JComboBox createPartComboBox() {
		try {
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			DbTable selected = null;
			for (Part t : PartPeer.doSelectAll()) {
				if (t.getId() == MainFrame.getInstance().getActivePartId()) {
					selected = t;
				}
				model.addElement(t);
			}
			JComboBox cob = new JComboBox();
			cob.setModel(model);
			if (selected != null) {
				cob.setSelectedItem(selected);
			}
			return cob;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void setValuesFromTable() {
		Chapter chapter = (Chapter) table;
		tfChapterNo.setText(Integer.toString(chapter.getChapterNo()));
		tfTitle.setText(chapter.getTitle());
		taDescription.setText(chapter.getDescription());
		taNotes.setText(chapter.getNotes());

		// part combo box
		DefaultComboBoxModel model = (DefaultComboBoxModel) cobPart
				.getModel();
		for (int i = 0; i < model.getSize(); ++i) {
			Part part = (Part) model.getElementAt(i);
			if (part.getId() == chapter.getPartId()) {
				cobPart.setSelectedItem(part);
				break;
			}
		}

		SwingTools.checkInputComponents(inputComponentList);
		taNotes.setCaretPosition(0);
		taDescription.setCaretPosition(0);
		taNotes.getUndoManager().discardAllEdits();
		taDescription.getUndoManager().discardAllEdits();
		tfTitle.getUndoManager().discardAllEdits();
		tfChapterNo.getUndoManager().discardAllEdits();
	}
	
	@Override
	protected void setValuesFromAction(Action action) {
		Part part = (Part) action.getValue(
				AbstractTableAction.ActionKey.PART.toString());
		if (part != null) {
			cobPart.setSelectedItem(part);
		}
	}

	public JComboBox getPartComboBox() {
		return cobPart;
	}

	public JTextField getChapterNoTF() {
		return tfChapterNo;
	}
	
	public JTextField getTitleTF() {
		return tfTitle;
	}

	public JTextArea getDescriptionTA() {
		return taDescription;
	}

	public JTextArea getNotesTA() {
		return taNotes;
	}

	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {
		ChapterPeer.makeOrUpdateChapter((ChapterDialog)getThis(), edit);
	}
		
	public Chapter getChapter(){
		return (Chapter)table;
	}
}
