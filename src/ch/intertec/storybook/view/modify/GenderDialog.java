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
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.GenderPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.verifier.IntegerVerifier;
import ch.intertec.storybook.toolkit.verifier.NonEmptyVerifier;
import ch.intertec.storybook.view.AbstractModifyDialog;

/**
 * Dialog for creating or editing a new gender.
 * 
 * @author martin
 *
 */

@SuppressWarnings("serial")
public class GenderDialog extends AbstractModifyDialog {
	
	private JTextField tfName;
	private JTextField tfChildhood;
	private JTextField tfAdolescence;
	private JTextField tfAdulthood;
	private JTextField tfRetirement;

	public GenderDialog(){
		super();
	}

	public GenderDialog(JFrame frame) {
		super(frame);
	}
	
	public GenderDialog(JFrame frame, Gender gender) {
		super(frame, gender);
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
			"[]");
	}
	
	@Override
	protected void initGUI() {
		try {
			if (edit) {
				setTitle("msg.dlg.mng.persons.gender.edit");
			} else {
				setTitle("msg.dlg.mng.persons.gender.new");
			}

			panel.setPreferredSize(new Dimension(550, 150));

			NonEmptyVerifier neVerifier = new NonEmptyVerifier();
			neVerifier.setErrorLabel(getErrorLabel());
			
			IntegerVerifier intVerifier = new IntegerVerifier(true, true);
			intVerifier.setErrorLabel(getErrorLabel());

			// gender name
			JLabel lbName = new JLabel(
					I18N.getMsgColon("msg.dlg.mng.persons.gender"));
			tfName = new JTextField();
			tfName.setInputVerifier(neVerifier);
			tfName.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));

			// childhood years
			JLabel lbChildhood = new JLabel(
					I18N.getMsgColon("msg.chart.gantt.childhood"));
			tfChildhood = new JTextField();
			tfChildhood.setInputVerifier(intVerifier);
			tfChildhood.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));

			// adolescence years
			JLabel lbAdolescence = new JLabel(
					I18N.getMsgColon("msg.chart.gantt.adolescence"));
			tfAdolescence = new JTextField();
			tfAdolescence.setInputVerifier(intVerifier);
			tfAdolescence.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));

			// adolescence years
			JLabel lbAdulthood = new JLabel(
					I18N.getMsgColon("msg.chart.gantt.adulthood"));
			tfAdulthood = new JTextField();
			tfAdulthood.setInputVerifier(intVerifier);
			tfAdulthood.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));

			// retirement years
			JLabel lbRetirement = new JLabel(
					I18N.getMsgColon("msg.chart.gantt.retirement"));
			tfRetirement = new JTextField();
			tfRetirement.setInputVerifier(intVerifier);
			tfRetirement.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));

			// fill in input list
			inputComponentList.add(tfName);
			inputComponentList.add(tfChildhood);
			inputComponentList.add(tfAdolescence);
			inputComponentList.add(tfAdulthood);
			inputComponentList.add(tfRetirement);

			// layout
			panel.add(lbName);
			panel.add(tfName);
			panel.add(lbChildhood);
			panel.add(tfChildhood);
			panel.add(lbAdolescence);
			panel.add(tfAdolescence);
			panel.add(lbAdulthood);
			panel.add(tfAdulthood);
			panel.add(lbRetirement);
			panel.add(tfRetirement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setValuesFromTable() {
		Gender gender = (Gender) table;
		tfName.setText(gender.getName());
		tfChildhood.setText(Integer.toString(gender.getChildhood()));
		tfAdolescence.setText(Integer.toString(gender.getAdolescence()));
		tfAdulthood.setText(Integer.toString(gender.getAdulthood()));
		tfRetirement.setText(Integer.toString(gender.getRetirement()));
		SwingTools.checkInputComponents(inputComponentList);
	}
	
	@Override
	protected void setValuesFromAction(Action action) {
		logger.fatal("not implemented");
	}

	public JTextField getNameTextField() {
		return this.tfName;
	}

	public JTextField getChildhoodTextField() {
		return this.tfChildhood;
	}

	public JTextField getAdolescenceTextField() {
		return this.tfAdolescence;
	}

	public JTextField getAdulthoodTextField() {
		return this.tfAdulthood;
	}

	public JTextField getRetirementTextField() {
		return this.tfRetirement;
	}

	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {
		GenderPeer.makeOrUpdateGender((GenderDialog)getThis(), edit);
	}
		
	public Gender getGender(){
		return (Gender)table;
	}
}
