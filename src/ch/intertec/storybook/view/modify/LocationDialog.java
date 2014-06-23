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
import ch.intertec.storybook.action.AbstractTableAction;
import ch.intertec.storybook.main.sidebar.City;
import ch.intertec.storybook.main.sidebar.Country;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.AutoComboBox;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextField;
import ch.intertec.storybook.toolkit.verifier.NonEmptyVerifier;
import ch.intertec.storybook.view.AbstractModifyDialog;

/**
 * Dialog for creating a new locations.
 * 
 * @author Martin
 */
@SuppressWarnings("serial")
public class LocationDialog extends AbstractModifyDialog {

	public final static String COMP_NAME_TF_NAME = "tf:name";
	public final static String COMP_NAME_TF_CITY = "tf:city";
	public final static String COMP_NAME_TF_COUTNRY = "tf:country";
	
	private UndoableTextField tfName;
	private AutoComboBox acbCity;
	private AutoComboBox acbCountry;
	private UndoableTextArea taDescription;
	private UndoableTextField tfAddress;
	private UndoableTextArea taNotes;

	public LocationDialog(){
		super();
	}

	public LocationDialog(JFrame frame) {
		super(frame);
	}
	
	public LocationDialog(JFrame frame, Location location) {
		super(frame, location);
	}
	
	public LocationDialog(Action action) {
		super(action);
	}
	
	/**
	 * @see ch.intertec.storybook.view.AbstractModifyDialog#init()
	 */
	@Override
	public void init(){
		// nothing to do
	}
	
	/**
	 * @see ch.intertec.storybook.view.AbstractModifyDialog#getMigLayout()
	 */
	@Override
	protected MigLayout getMigLayout(){
		return new MigLayout(
			"wrap 2,fill",
			"[]10[grow,fill]",
			"[][][][]20[grow,fill]"
		);
	}
	
	/**
	 * @see ch.intertec.storybook.view.AbstractModifyDialog#initGUI()
	 */
	@Override
	protected void initGUI() {
		try {
			setPreferredSize(new Dimension(600, 600));
			
			if (this.edit) {
				setTitle("msg.common.location.edit");
			} else {
				setTitle("msg.common.location.new");
			}
			
			// location name
			JLabel lbName = new JLabel(
					I18N.getMsgColon("msg.dlg.location.name", true));
			tfName = new UndoableTextField();
			NonEmptyVerifier neVerifier = new NonEmptyVerifier();
			neVerifier.setErrorLabel(getErrorLabel());
			tfName.setInputVerifier(neVerifier);
			tfName.setBorder(
					BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			tfName.setName(COMP_NAME_TF_NAME);
			setFocusComponent(this.tfName);

			// address
			JLabel lbAddress = new JLabel(I18N.getMsgColon("msg.dlg.location.address"));
			tfAddress = new UndoableTextField();
			tfAddress.setBorder(
					BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			
			// city
			JLabel lbCity = new JLabel(
					I18N.getMsgColon("msg.dlg.location.city"));
			List<String> cityList = LocationPeer.doSelectDistinctCity();
			if (!cityList.contains("")) {
				cityList.add(0, "");
			}
			acbCity = new AutoComboBox(cityList);
			acbCity.setStrict(false);
			acbCity.setName(COMP_NAME_TF_CITY);

			// country
			JLabel lbCountry = new JLabel(
					I18N.getMsgColon("msg.dlg.location.country"));
			List<String> countryList = LocationPeer.doSelectDistinctCountry();
			if (!countryList.contains("")) {
				countryList.add(0, "");
			}
			acbCountry = new AutoComboBox(countryList);
			acbCountry.setStrict(false);
			acbCountry.setName(COMP_NAME_TF_COUTNRY);

			// fill in input list
			inputComponentList.add(this.tfName);

			// layout
			panel.add(lbName);
			panel.add(this.tfName);
			panel.add(lbAddress);
			panel.add(this.tfAddress);			
			panel.add(lbCity);
			panel.add(this.acbCity);
			panel.add(lbCountry);
			panel.add(this.acbCountry);			
			
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
	
	/**
	 * @see ch.intertec.storybook.view.AbstractModifyDialog#setValuesFromTable()
	 */
	@Override
	protected void setValuesFromTable(){
		Location location = (Location) this.table;
		tfName.setText(location.getName());
		acbCity.setText(location.getCity());
		acbCountry.setText(location.getCountry());
		taDescription.setText(location.getDescription());
		tfAddress.setText(location.getAddress());
		taNotes.setText(location.getNotes());
		SwingTools.checkInputComponents(inputComponentList);
		taDescription.setCaretPosition(0);
		taNotes.setCaretPosition(0);
		taDescription.getUndoManager().discardAllEdits();
		taNotes.getUndoManager().discardAllEdits();
		tfAddress.getUndoManager().discardAllEdits();
		tfName.getUndoManager().discardAllEdits();
	}
	
	@Override
	protected void setValuesFromAction(Action action) {
		Country country = (Country) action
				.getValue(AbstractTableAction.ActionKey.COUNTRY.toString());
		if (country != null) {
			acbCountry.setText(country.toString());
		}
		City city = (City) action.getValue(AbstractTableAction.ActionKey.CITY
				.toString());
		if (city != null) {
			acbCity.setText(city.toString());
		}
	}

	/**
	 * @see ch.intertec.storybook.view.AbstractModifyDialog#makeOrUpdate(ch.intertec.storybook.view.AbstractModifyDialog, boolean)
	 */
	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {
		LocationPeer.makeOrUpdateLocation((LocationDialog) this.getThis(), edit);
	}
		
	/**
	 * Returns the {@link Location} database object associated.
	 * @return the {@link Location} database object associated.
	 */
	public Location getLocationTable(){
		return (Location) this.table;
	}
	
	/**
	 * Returns the text field associated with the location name.
	 * @return the {@link JTextField} containing the location name.
	 */
	public JTextField getNameTextField() {
		return this.tfName;
	}
	
	/**
	 * Returns the text field associated with the location city.
	 * @return the {@link JTextField} containing the location city.
	 */
	public AutoComboBox getCityAutoCombo() {
		return this.acbCity;
	}

	/**
	 * Returns the text field associated with the location country.
	 * @return the {@link JTextField} containing the location country.
	 */
	public AutoComboBox getCountryTextField() {
		return this.acbCountry;
	}

	/**
	 * Returns the text field associated with the location description.
	 * @return the {@link JTextField} containing the location description.
	 */
	public JTextArea getDescriptionTextArea() {
		return this.taDescription;
	}
	
	/**
	 * Returns the text field associated with the location address.
	 * @return the {@link JTextField} containing the location address.
	 */
	public JTextField getAddressTextField() {
		return this.tfAddress;
	}
	
	public JTextArea getNotesTextArea() {
		return this.taNotes;
	}	
}
