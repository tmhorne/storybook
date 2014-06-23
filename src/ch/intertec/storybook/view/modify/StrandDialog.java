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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.Constants.ComponentName;
import ch.intertec.storybook.toolkit.swing.CleverColorChooser;
import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextField;
import ch.intertec.storybook.toolkit.verifier.NonEmptyVerifier;
import ch.intertec.storybook.view.AbstractModifyDialog;

/**
 * Dialog for creating a new strands.
 * 
 * @author martin
 *
 */

@SuppressWarnings("serial")
public class StrandDialog extends AbstractModifyDialog {

	public static final String COMP_NAME_TF_STRAND = "tf:name";
	public static final String COMP_NAME_TF_ABBR = "tf:abbr";
	
	private CleverColorChooser btColor;
	private UndoableTextField tfName;
	private UndoableTextField tfAbbreviation;
	private UndoableTextArea taNotes;

	public StrandDialog(){
		super();
	}

	public StrandDialog(JFrame frame) {
		super(frame);
	}
	
	public StrandDialog(JFrame frame, Strand strand) {
		super(frame, strand);
	}
	
	@Override
	public void init(){
		// nothing to do
	}
	
	@Override
	protected MigLayout getMigLayout(){
		return new MigLayout(
			"wrap 2",
			"[]10[grow,fill,200!]",
			"[][][]20[]");
	}
	
	@Override
	protected void initGUI() {
		try {
			if (edit) {
				setTitle("msg.common.strand.edit");
			} else {
				setTitle("msg.common.strand.new");
			}			
			panel.setPreferredSize(new Dimension(380, 90));

			NonEmptyVerifier neVerifier = new NonEmptyVerifier();
			neVerifier.setErrorLabel(getErrorLabel());
						
			// name
			JLabel lbName = new JLabel(I18N.getMsgColon("msg.dlg.strand.name", true));
			tfName = new UndoableTextField();
			tfName.setInputVerifier(neVerifier);
			tfName.setBorder(
					BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			tfName.setName(COMP_NAME_TF_STRAND);
			setFocusComponent(tfName);

			// abbreviation
			JLabel lbAbbreviation = new JLabel(I18N.getMsgColon("msg.dlg.strand.abbr", true));
			tfAbbreviation = new UndoableTextField();			
			tfAbbreviation.setInputVerifier(neVerifier);
			tfAbbreviation.setBorder(
					BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			tfAbbreviation.setName(COMP_NAME_TF_ABBR);

			// color
			JLabel lbColor = new JLabel(I18N.getMsgColon("msg.dlg.strand.color", true));
			btColor = new CleverColorChooser(
					I18N.getMsg("msg.dlg.strand.choose.color"),
					getStrand() == null ? Color.lightGray : getStrand().getColor(),
					ColorUtil.getNiceColors(),
					false);
			btColor.setName(ComponentName.COLOR_CHOOSER.toString());
			
			// fill in input list
			inputComponentList.add(tfAbbreviation);
			inputComponentList.add(tfName);

			// layout
			panel.add(lbName);
			panel.add(tfName);
			panel.add(lbAbbreviation);
			panel.add(tfAbbreviation);
			panel.add(lbColor);
			panel.add(btColor);
			
			// notes
			taNotes = createNotesTextArea();
			tabbedPane.addTab(I18N.getMsg("msg.common.notes"),
					SwingTools.createNotesPanel(taNotes));
			tabbedPane.setIconAt(1, I18N.getIcon("icon.small.note"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setValuesFromTable() {
		Strand strand = (Strand) table;
		tfAbbreviation.setText(strand.getAbbreviation());
		tfName.setText(strand.getName());
		btColor.setColor(strand.getColor());
		taNotes.setText(strand.getNotes());
		taNotes.setCaretPosition(0);
		SwingTools.checkInputComponents(inputComponentList);
		taNotes.getUndoManager().discardAllEdits();
		tfAbbreviation.getUndoManager().discardAllEdits();
		tfName.getUndoManager().discardAllEdits();
	}
	
	@Override
	protected void setValuesFromAction(Action action) {
		logger.fatal("not implemented");
	}

	public CleverColorChooser getShowColorLabel() {
		return this.btColor;
	}

	public JTextField getNameTextField() {
		return this.tfName;
	}

	public JTextArea getNotesTextArea() {
		return this.taNotes;
	}

	public JTextField getAbbreviationTextField(){
		return this.tfAbbreviation;
	}
	
	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {
		StrandPeer.makeOrUpdateStrand((StrandDialog)getThis(), edit);
	}
		
	public Strand getStrand(){
		return (Strand)table;
	}
}
