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

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextField;
import ch.intertec.storybook.toolkit.verifier.IntegerVerifier;
import ch.intertec.storybook.toolkit.verifier.NonEmptyVerifier;
import ch.intertec.storybook.view.AbstractModifyDialog;

/**
 * Dialog for creating a new part.
 * 
 * @author martin
 *
 */

@SuppressWarnings("serial")
public class PartDialog extends AbstractModifyDialog {
	
	private UndoableTextField tfNumber;
	private UndoableTextField tfName;

	public PartDialog(){
		super();
	}

	public PartDialog(JFrame frame) {
		super(frame);
	}
	
	public PartDialog(JFrame frame, Part part) {
		super(frame, part);
	}
	
	@Override
	public void init(){
		// nothing to do
	}
	
	@Override
	protected MigLayout getMigLayout(){
		return new MigLayout(
			"wrap 2,fill",
			"[]10[grow,fill]",
			"[][][grow]");
	}
	
	@Override
	protected void initGUI() {
		try {
			if (edit) {
				setTitle("msg.common.part.edit");
			} else {
				setTitle("msg.common.part.new");
			}

			panel.setPreferredSize(new Dimension(420, 100));

			NonEmptyVerifier neVerifier = new NonEmptyVerifier();
			neVerifier.setErrorLabel(getErrorLabel());
			
			IntegerVerifier intVerifier = new IntegerVerifier(true);
			intVerifier.setErrorLabel(getErrorLabel());

			// part number
			JLabel lbNumber = new JLabel(I18N.getMsg("msg.dlg.part.number"));
			tfNumber = new UndoableTextField();
			tfNumber.setInputVerifier(intVerifier);
			tfNumber.setBorder(
					BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			setFocusComponent(tfNumber);

			// part name
			JLabel lbName = new JLabel(I18N.getMsg("msg.dlg.part.name"));
			tfName = new UndoableTextField();
			tfName.setInputVerifier(neVerifier);
			tfName.setBorder(
					BorderFactory.createBevelBorder(BevelBorder.LOWERED));
									
			// fill in input list
			inputComponentList.add(tfNumber);
			inputComponentList.add(tfName);

			// layout
			panel.add(lbNumber);
			panel.add(tfNumber);			
			panel.add(lbName);
			panel.add(tfName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setValuesFromTable(){
		Part part = (Part)table;
		tfNumber.setText(part.getNumberStr());
		tfName.setText(part.getName());
		SwingTools.checkInputComponents(inputComponentList);
		tfNumber.getUndoManager().discardAllEdits();
		tfName.getUndoManager().discardAllEdits();
	}
	
	@Override
	protected void setValuesFromAction(Action action) {
		logger.fatal("not implemented");
	}

	public JTextField getNumberTextField() {
		return this.tfNumber;
	}
	
	public JTextField getNameTextField() {
		return this.tfName;
	}
	
	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {
		PartPeer.makeOrUpdatePart((PartDialog)getThis(), edit);
	}
		
	public Part getPart(){
		return (Part)table;
	}
}
