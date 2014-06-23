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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.AbstractTableAction;
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.GenderPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.CleverColorChooser;
import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.IconListRenderer;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.panel.DateChooser;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextField;
import ch.intertec.storybook.toolkit.verifier.DateChooserVerifier;
import ch.intertec.storybook.toolkit.verifier.NonEmptyVerifier;
import ch.intertec.storybook.view.AbstractModifyDialog;

/**
 * Dialog for creating a new person.
 * 
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class CharacterDialog extends AbstractModifyDialog implements KeyListener {

    public static final String COMP_NAME_TF_FIRSTNAME = "tf:firstname";
    public static final String COMP_NAME_TF_LASTNAME = "tf:lastname";
    public static final String COMP_NAME_TF_ABBR = "tf:abbr";
    private UndoableTextField tfFirstName;
    private UndoableTextField tfLastName;
    private UndoableTextField tfAbbreviation;
    private UndoableTextField tfOccupation;
    private UndoableTextArea taDescription;
    private UndoableTextArea taNotes;
    private java.sql.Date birthdayDate;
    private DateChooser birthdayDateChooser;
    private java.sql.Date dayOfDeathDate;
    private DateChooser dayOfDeathDateChooser;
    private CleverColorChooser colorChooser;
    private JRadioButton rbCenteralCharacter;
    private JRadioButton rbMinorCharacter;
    private JComboBox genderCombo;
    private boolean noAutoAbbr;

    public CharacterDialog() {
        super();
    }

    public CharacterDialog(JFrame frame) {
        super(frame);
    }

    public CharacterDialog(JFrame frame, SbCharacter character) {
        super(frame, character);
    }

    public CharacterDialog(Action action) {
        super(action);
    }

    /**
     * @see ch.intertec.storybook.view.AbstractModifyDialog#init()
     */
    @Override
    public void init() {
        noAutoAbbr = false;
    }

    /**
     * @see ch.intertec.storybook.view.AbstractModifyDialog#getMigLayout()
     */
    @Override
    protected MigLayout getMigLayout() {
        return new MigLayout(
                "wrap 2",
                "[]20[fill]",
                "[top]");
    }

    /**
     * @see ch.intertec.storybook.view.AbstractModifyDialog#initGUI()
     */
    @Override
    protected void initGUI() {
        try {
        	setPreferredSize(new Dimension(600, 600));
        	
            if (edit) {
                this.setTitle("msg.common.person.edit");
            } else {
                this.setTitle("msg.common.person.new");
            }

            // create a non-empty verifier
            NonEmptyVerifier neVerifier = new NonEmptyVerifier();
            neVerifier.setErrorLabel(getErrorLabel());

            // first name
            JLabel lbFirstName = new JLabel(I18N.getMsgColon("msg.dlg.person.firstname", true));
            this.tfFirstName = new UndoableTextField();
            this.tfFirstName.setName(COMP_NAME_TF_FIRSTNAME);
            this.tfFirstName.setInputVerifier(neVerifier);
            this.tfFirstName.setBorder(
                    BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            this.tfFirstName.addKeyListener(this);
            setFocusComponent(this.tfFirstName);

            // last name
            JLabel lbLastName = new JLabel(I18N.getMsgColon("msg.dlg.person.lastname"));
            this.tfLastName = new UndoableTextField();
            this.tfLastName.setName(COMP_NAME_TF_LASTNAME);
            this.tfLastName.setBorder(
                    BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            this.tfLastName.addKeyListener(this);

            // abbreviation
            JLabel lbAbbreviation = new JLabel(I18N.getMsgColon("msg.dlg.person.abbr", true));
            this.tfAbbreviation = new UndoableTextField();
            this.tfAbbreviation.setInputVerifier(neVerifier);
            this.tfAbbreviation.setBorder(
                    BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            tfAbbreviation.setName(COMP_NAME_TF_ABBR);

            // gender
            JLabel lbGender = new JLabel(I18N.getMsgColon("msg.dlg.person.gender", true));
            List<Gender> genders = GenderPeer.doSelectAll();
			genderCombo = new JComboBox(genders.toArray(new Gender[genders
					.size()]));
			Map<Object, Icon> icons = new HashMap<Object, Icon>();
			for (Gender gender : genders) {
				icons.put(gender, gender.getIcon());
			}
			genderCombo.setRenderer(new IconListRenderer(icons));
			
            // category
            JLabel lbCategory = new JLabel(I18N.getMsgColon("msg.common.category"));
            rbCenteralCharacter = new JRadioButton(I18N.getMsg("msg.category.central.character"));
            rbCenteralCharacter.setSelected(true);
            rbMinorCharacter = new JRadioButton(I18N.getMsg("msg.category.minor.character"));
            ButtonGroup categoryGroup = new ButtonGroup();
            categoryGroup.add(rbCenteralCharacter);
            categoryGroup.add(rbMinorCharacter);

            // birthday
            JLabel lbBirthday = new JLabel(I18N.getMsgColon("msg.dlg.person.birthday"));
            if (birthdayDate != null) {
                birthdayDateChooser = new DateChooser(this.birthdayDate);
            } else {
                birthdayDateChooser = new DateChooser();
            }
            birthdayDateChooser.setAllowEmptyValue(true);
            birthdayDateChooser.setInputVerifier(new DateChooserVerifier());

            // day of death
            JLabel lbDayOfDeath = new JLabel(I18N.getMsgColon("msg.dlg.person.death"));
            if (this.dayOfDeathDate != null) {
                this.dayOfDeathDateChooser = new DateChooser(this.dayOfDeathDate);
            } else {
                this.dayOfDeathDateChooser = new DateChooser();
            }
            this.dayOfDeathDateChooser.setAllowEmptyValue(true);
            this.dayOfDeathDateChooser.setInputVerifier(new DateChooserVerifier());

            // occupation
            JLabel lbOccupation = new JLabel(I18N.getMsgColon("msg.dlg.person.occupation"));
            this.tfOccupation = new UndoableTextField();
            this.tfOccupation.setBorder(
                    BorderFactory.createBevelBorder(BevelBorder.LOWERED));

            // color
            JLabel lbColor = new JLabel(I18N.getMsgColon("msg.dlg.strand.color"));
            colorChooser = new CleverColorChooser(
                    I18N.getMsg("msg.dlg.strand.choose.color"),
                    getCharacterTable() == null ? null : getCharacterTable().getColor(),
                    ColorUtil.getNiceColors(),
                    true);

            // fill in input list
            this.inputComponentList.add(this.tfFirstName);
            this.inputComponentList.add(this.tfAbbreviation);
            this.inputComponentList.add(this.birthdayDateChooser);
            this.inputComponentList.add(this.dayOfDeathDateChooser);

            // layout common panel
            this.panel.add(lbFirstName);
            this.panel.add(tfFirstName, "w 200");
            this.panel.add(lbLastName);
            this.panel.add(tfLastName, "w 200");
            this.panel.add(lbAbbreviation);
            this.panel.add(tfAbbreviation, "w 80,gapbottom 10");
            this.panel.add(lbGender);
            this.panel.add(this.genderCombo, "wrap");
            this.panel.add(lbCategory);
            this.panel.add(rbCenteralCharacter, "split");
            this.panel.add(rbMinorCharacter, "wrap,gapbottom 10");
            this.panel.add(lbBirthday);
            this.panel.add(birthdayDateChooser);
            this.panel.add(lbDayOfDeath);
            this.panel.add(dayOfDeathDateChooser, "gapbottom 10");
            this.panel.add(lbOccupation);
            this.panel.add(tfOccupation, "span,wrap,w 300,gapbottom 10");
            this.panel.add(lbColor);
            this.panel.add(colorChooser);
            
            // description
            taDescription = new UndoableTextArea();
            tabbedPane.addTab(I18N.getMsg("msg.dlg.person.descr"),
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
    protected void setValuesFromTable() {
        SbCharacter character = (SbCharacter) table;
        tfFirstName.setText(character.getFirstname());
        tfLastName.setText(character.getLastname());
        tfAbbreviation.setText(character.getAbbreviation());
        birthdayDateChooser.setDate(character.getBirthday());
        dayOfDeathDateChooser.setDate(character.getDayOfDeath());
        tfOccupation.setText(character.getOccupation());
        taDescription.setText(character.getDescription());
        taDescription.setCaretPosition(0);
        taNotes.setText(character.getNotes());
        taNotes.setCaretPosition(0);
        this.genderCombo.setSelectedItem(character.getGender());
        if (character.getCategory() == SbCharacter.CATEGORY_CENTRAL) {
            rbCenteralCharacter.setSelected(true);
        } else {
            rbMinorCharacter.setSelected(true);
        }
        SwingTools.checkInputComponents(this.inputComponentList);
        taDescription.getUndoManager().discardAllEdits();
        taNotes.getUndoManager().discardAllEdits();
        tfAbbreviation.getUndoManager().discardAllEdits();
        tfFirstName.getUndoManager().discardAllEdits();
        tfLastName.getUndoManager().discardAllEdits();
        tfOccupation.getUndoManager().discardAllEdits();
        noAutoAbbr = true;
    }

    @Override
    protected void setValuesFromAction(Action action) {
        Integer category = (Integer) action.getValue(AbstractTableAction.ActionKey.CATEGORY.toString());
        if (category != null) {
            if (category == SbCharacter.CATEGORY_CENTRAL) {
                rbCenteralCharacter.setSelected(true);
            } else if (category == SbCharacter.CATEGORY_MINOR) {
                rbMinorCharacter.setSelected(true);
            }
        }
    }

    /**
     * @see ch.intertec.storybook.view.AbstractModifyDialog#makeOrUpdate(ch.intertec.storybook.view.AbstractModifyDialog, boolean)
     */
    @Override
    protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
            throws Exception {
        SbCharacterPeer.makeOrUpdateCharacter((CharacterDialog) getThis(), edit);
    }

    /**
     * Returns the data object associated with the character.
     * @return the {@link SbCharacter} associated with the dialog.
     */
    public SbCharacter getCharacterTable() {
        return (SbCharacter) this.table;
    }

    /**
     * Returns the text field containing the character's first name.
     * @return the {@link JTextField} containing the character's first name.
     */
    public JTextField getFirstNameTextField() {
        return this.tfFirstName;
    }

    /**
     * Returns the text field containing the character's last name.
     * @return the {@link JTextField} containing the character's last name.
     */
    public JTextField getLastNameTextField() {
        return this.tfLastName;
    }

    /**
     * Returns the text field containing the character's abbreviation.
     * @return the {@link JTextField} containing the character's abbreviation.
     */
    public JTextField getAbbreviationTextField() {
        return this.tfAbbreviation;
    }

    public JRadioButton getCentralCharacterRadioButton() {
        return rbCenteralCharacter;
    }

    public JRadioButton getMinorCharacterRadioButton() {
        return rbMinorCharacter;
    }

    /**
     * Returns the chooser that selects the character's birthday.
     * @return the {@link DateChooser} that selects the character's birthday.
     */
    public DateChooser getBirthdayDateChooser() {
        return this.birthdayDateChooser;
    }

    /**
     * Returns the chooser that selects the character's day of death.
     * @return the {@link DateChooser} that selects the character's day of death.
     */
    public DateChooser getDayOfDeathDateChooser() {
        return this.dayOfDeathDateChooser;
    }

    /**
     * Returns the text field containing the character's occupation.
     * @return the {@link JTextField} containing the character's occupation.
     */
    public JTextField getOccupationTextField() {
        return this.tfOccupation;
    }

    /**
     * Returns the text area containing the character's description.
     * @return the {@link JTextArea} containing the character's description.
     */
    public JTextArea getDescriptionTextArea() {
        return this.taDescription;
    }

    public JTextArea getNotesTextArea() {
        return this.taNotes;
    }

    public Gender getSelectedGender() {
        return (Gender) this.genderCombo.getSelectedItem();
    }

    /**
     * Returns the label describing the color associated with the character.
     * @return the {@link JLabel} describing the color associated with the character.
     */
    public CleverColorChooser getShowColorLabel() {
        return this.colorChooser;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // nothing to do
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // nothing to do
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (this.noAutoAbbr) {
            return;
        }
        JComponent comp = (JComponent) e.getSource();
        if (comp instanceof JTextField) {
            String fn = "";
            String ln = "";
            if (this.tfFirstName.getText().length() > 1) {
                fn = this.tfFirstName.getText().substring(0, 2);
            }
            if (this.tfLastName.getText().length() > 1) {
                ln = this.tfLastName.getText().substring(0, 2);
            }
            this.tfAbbreviation.setText(fn + ln);
        }
    }
}
