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

import java.awt.Dimension;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;

import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.Tag.TagType;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.AutoComboBox;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextField;
import ch.intertec.storybook.toolkit.verifier.NonEmptyVerifier;
import ch.intertec.storybook.view.AbstractModifyDialog;

/**
 * Dialog for creating or editing an tag.
 * 
 * @author martin
 *
 */

@SuppressWarnings("serial")
public class TagDialog extends AbstractModifyDialog {
	
	private AutoComboBox acbCategory;
	private UndoableTextField tfName;
	private UndoableTextArea taDescription;
	private UndoableTextArea taNotes;

	public TagDialog() {
		super();
	}

	public TagDialog(JFrame frame) {
		super(frame);
	}
	
	public TagDialog(JFrame frame, Tag tag) {
		super(frame, tag);
	}

	@Override
	public void init(){
	}
	
	@Override
	protected MigLayout getMigLayout(){
		return new MigLayout(
			"wrap 2",
			"[]10[grow,fill]",
			"[][][grow,fill]");
	}
	
	@Override
	protected void initGUI() {
		try {
			String txtKey = "";
			
			if (edit) {
				if (getTagType() == TagType.ITEM) {
					setTitle("msg.item.edit");
				} else {
					setTitle("msg.tag.edit");
				}
			} else {
				if (getTagType() == TagType.ITEM) {
					setTitle("msg.item.new");
				} else {
					setTitle("msg.tag.new");
				}
			}

			panel.setPreferredSize(new Dimension(420, 100));

			NonEmptyVerifier neVerifier = new NonEmptyVerifier();
			neVerifier.setErrorLabel(getErrorLabel());
			
			// tag name
			if (getTagType() == TagType.ITEM) {
				txtKey = "msg.item.name";
			}else{				
				txtKey = "msg.tag.name";
			}
			JLabel lbName = new JLabel(I18N.getMsgColon(txtKey, true));
			
			tfName = new UndoableTextField();
			tfName.setInputVerifier(neVerifier);
			tfName.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			setFocusComponent(tfName);

			// tag category
			if (getTagType() == TagType.ITEM) {
				txtKey = "msg.item.category";
			} else {
				txtKey = "msg.tag.category";
			}
			JLabel lbCategory = new JLabel(I18N.getMsgColon(txtKey));
			
			List<String> list;
			if (getTagType() == TagType.ITEM) {
				list = ItemPeer.doSelectDistinctCategory();
			} else {
				list = TagPeer.doSelectDistinctCategory();
			}
			if (!list.contains("")) {
				list.add(0, "");
			}
			acbCategory = new AutoComboBox(list);
			acbCategory.setStrict(false);
			
			// fill in input list
			inputComponentList.add(tfName);

			// layout
			panel.add(lbName);
			panel.add(tfName);
			panel.add(lbCategory);
			panel.add(acbCategory);
			
			// description
			taDescription = new UndoableTextArea();
			tabbedPane.addTab(I18N.getMsg("msg.item.description"),
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
	
	@Override
	protected void setValuesFromTable(){
		Tag tag = (Tag)table;
		acbCategory.setText(tag.getCategory());
		tfName.setText(tag.getName());
		taDescription.setText(tag.getDescription());
		taDescription.setCaretPosition(0);
		taNotes.setText(tag.getNotes());
		taNotes.setCaretPosition(0);
		SwingTools.checkInputComponents(inputComponentList);
		tfName.getUndoManager().discardAllEdits();
		taDescription.getUndoManager().discardAllEdits();
		taNotes.getUndoManager().discardAllEdits();
	}
	
	@Override
	protected void setValuesFromAction(Action action) {
		logger.fatal("not implemented");
	}

	public AutoComboBox getCategoryTextField() {
		return this.acbCategory;
	}
	
	public JTextField getNameTextField() {
		return this.tfName;
	}

	public JTextArea getDescriptionTextArea() {
		return this.taDescription;
	}	

	public JTextArea getNotesTextArea() {
		return this.taNotes;
	}	

	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {
			TagPeer.makeOrUpdateTag((TagDialog)getThis(), edit);
	}
		
	public Tag getTag(){
		return (Tag)table;
	}

	public TagType getTagType(){
		return TagType.TAG;
	}
}
