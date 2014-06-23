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

package ch.intertec.storybook.view.modify.scene;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.time.DateUtils;

import ch.intertec.storybook.action.AbstractTableAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Scene.Status;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.toolkit.ExportTools;
import ch.intertec.storybook.toolkit.FileTools;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.SpellCheckerTools;
import ch.intertec.storybook.toolkit.swing.IconListRenderer;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.panel.DateChooser;
import ch.intertec.storybook.toolkit.swing.panel.PastelPanel;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextField;
import ch.intertec.storybook.toolkit.verifier.DateChooserVerifier;
import ch.intertec.storybook.toolkit.verifier.IntegerVerifier;
import ch.intertec.storybook.view.AbstractModifyDialog;
import ch.intertec.storybook.view.modify.StrandLinksFactory;

import com.inet.jortho.PopupListener;
import com.inet.jortho.SpellChecker;
import com.inet.jortho.SpellCheckerOptions;
import com.toedter.calendar.JDateChooser;

/**
 * Builds the add/edit scene dialog box. It also responds to the dialog's
 * events.
 * 
 * @author Martin.
 */
@SuppressWarnings("serial")
public class SceneDialog extends AbstractModifyDialog implements
		ActionListener {

	public final static String COMP_NAME = "scene_dialog";
	public final static String COMP_NAME_TA_TITLE = "ta:title";
	public final static String COMP_NAME_TA_SUMMARY = "ta:summary";
	public final static String COMP_NAME_TAB_CHARACTERS_LOCATIONS = "tab:characters_locations";

	private UndoableTextArea taTitle;
	private UndoableTextArea taSummary;
	private UndoableTextField tfSceneNo;
	private DateChooser dateChooser;
	private JComboBox cobChapter;
	private JComboBox cobStrand;
	private JComboBox cobStatus;
	private CharactersLocationsPanel charactersLocationsPanel;
	private JTextField tfPart;
	private UndoableTextArea taNotes;
	private PastelPanel strandPanel;
	private JPanel datePanel;
	private JComboBox cobRelativeSceneSelector;
	private JSpinner relativeSceneDaysDifferenceSpinner;
	private JRadioButton rbRelativeDate;
	private JRadioButton rbFixedDate;

	private int strandId;
	private int chapterId;

	private Date date;

	private List<Object> strandLinksList;
	private StrandLinksFactory strandFactory;

	// actions
	private AbstractAction spellCheckAction;

	/**
	 * Builds a scene dialog box.
	 */
	public SceneDialog() {
		super();
	}

	/**
	 * Builds a scene dialog with the passed frame as the parent.
	 * 
	 * @param frame
	 *            the parent of the dialog box.
	 */
	public SceneDialog(JFrame frame) {
		super(frame);
	}

	/**
	 * Builds a scene dialog with the passed frame as parent and the table
	 * assigned.
	 * 
	 * @param frame
	 *            the {@link JFrame} instance to be the parent of the dialog.
	 * @param scene
	 *            the {@link Scene} data object to associate.
	 */
	public SceneDialog(JFrame frame, Scene scene) {
		super(frame, scene);
	}

	/**
	 * Builds a scene dialog with data from the passed {@link Action}.
	 * 
	 * @param action
	 *            the {@link Action} instance with data to populate with.
	 */
	public SceneDialog(Action action) {
		edit = false;
		init();
		initPanels();
		setValuesFromAction(action);
		initInternalGUI();
		if (this.tfSceneNo != null && this.tfSceneNo.getText().isEmpty()) {
			if (this.chapterId > 0) {
				Chapter chapter = ChapterPeer.doSelectById(this.chapterId);
				Integer next = ScenePeer.getNextSceneNo(chapter);
				this.tfSceneNo.setText(next.toString());
			}
		}
	}

	@Override
	public void init() {
		strandLinksList = new ArrayList<Object>();
		strandId = -1;
		chapterId = -1;
		strandFactory = new StrandLinksFactory(strandLinksList,
				(DbTable) getScene());
		if (date == null) {
			date = ScenePeer.getFirstDate();
		}
	}

	@Override
	protected MigLayout getMigLayout() {
		return new MigLayout(
				"wrap 2",
				"[]",
				"[]10[][][]10[40!][fill]"
				);
	}

	/**
	 * Builds the basic GUI for the Create/Edit scene dialog.
	 * 
	 * @see ch.intertec.storybook.view.AbstractModifyDialog#initGUI()
	 */
	@Override
	protected void initGUI() {
		try {
			setPreferredSize(SwingTools.getPreferredDimension());
			setName(COMP_NAME);

			if (edit) {
				setTitle("msg.common.scene.edit");
			} else {
				setTitle("msg.common.scene.add");
			}

			// chapter
			JLabel lbChapter = new JLabel(I18N.getMsgColon(
					"msg.dlg.scene.chapter", true));
			cobChapter = createChapterComboBox();
			cobChapter.setMaximumRowCount(20);
			cobChapter.addActionListener(this);

			// scene number
			JLabel lbSceneNo = new JLabel(
					I18N.getMsgColon("msg.dlg.scene.scene.no"));
			tfSceneNo = new UndoableTextField();

			IntegerVerifier intVerifier = new IntegerVerifier(true, true);
			intVerifier.setErrorLabel(getErrorLabel());

			tfSceneNo.setInputVerifier(intVerifier);
			tfSceneNo.setBorder(BorderFactory.createLoweredBevelBorder());
			tfSceneNo.setPreferredSize(new Dimension(30, 20));

			// part number (from chapter, read only)
			JLabel lbPart = new JLabel(I18N.getMsgColon("msg.common.part"));
			tfPart = new JTextField();
			tfPart.setEditable(false);
			tfPart.setBorder(null);
			tfPart.setPreferredSize(new Dimension(30, 20));

			// strand panel
			strandPanel = createStrandPanel();

			// date panel
			datePanel = this.createDatePanel();

			// status
			JLabel lbStatus = new JLabel(I18N.getMsgColon("msg.status", true));
			cobStatus = createStatusComboBox();

			// title
			JLabel lbTitle = new JLabel(
					I18N.getMsgColon("msg.dlg.chapter.title"));
			taTitle = new UndoableTextArea();
			taTitle.setRows(3);
			taTitle.setLineWrap(true);
			taTitle.setWrapStyleWord(true);
			taTitle.setDragEnabled(true);
			taTitle.setName(COMP_NAME_TA_TITLE);
			SwingTools.addCtrlEnterAction(taTitle, getOkAction());
			JScrollPane spTitle = new JScrollPane(taTitle);
			spTitle.setPreferredSize(new Dimension(Integer.MAX_VALUE,
					Integer.MAX_VALUE));

			// summary
			JLabel lbSummary = new JLabel(
					I18N.getMsgColon("msg.dlg.scene.summary"));
			lbSummary.setVerticalAlignment(JLabel.TOP);
			taSummary = new UndoableTextArea();
			taSummary.setLineWrap(true);
			taSummary.setWrapStyleWord(true);
			taSummary.setDragEnabled(true);
			taSummary.setName(COMP_NAME_TA_SUMMARY);
			SwingTools.addCtrlEnterAction(taSummary, getOkAction());
			JScrollPane spSummary = new JScrollPane(taSummary);
			spSummary.setPreferredSize(new Dimension(Integer.MAX_VALUE,
					Integer.MAX_VALUE));

			if (taTitle.getText().isEmpty()) {
				setFocusComponent(taTitle);
			} else {
				setFocusComponent(taSummary);
			}
			
			// pop up menu
			JPopupMenu menu = new JPopupMenu();
			if (SpellCheckerTools.isSpellCheckActive()) {
				// register fields for spell check
				SpellChecker.register(taTitle);
				SpellChecker.register(taSummary);
				SpellChecker.enablePopup(taTitle, true);
				SpellChecker.enablePopup(taSummary, true);
				menu.add(SpellChecker.createCheckerMenu());
				menu.add(SpellChecker.createLanguagesMenu());
				menu.add(new JSeparator());
			}
			SwingTools.addCopyPasteToPopupMenu(menu, taSummary);
			taSummary.addMouseListener(new PopupListener(menu));
			taTitle.addMouseListener(new PopupListener(menu));
			
			// spell check button
			JButton btSpellCheck = new JButton();
			btSpellCheck.setAction(getSpellCheckAction());
			btSpellCheck.setText(I18N.getMsg("msg.pref.spelling"));

			// import button
			JButton btImport = new JButton();
			btImport.setAction(getImportAction());
			btImport.setText(I18N.getMsg("msg.common.import"));

			// export button
			JButton btExport = new JButton();
			btExport.setAction(getExportAction());
			btExport.setText(I18N.getMsg("msg.common.export"));

			// components to check
			inputComponentList.add(tfSceneNo);
			inputComponentList.add(dateChooser);

			// layout
			panel.add(strandPanel, "growx,span");
			panel.add(this.datePanel, "growx, span");
			panel.add(lbChapter);
			panel.add(cobChapter, "sg,split 5");
			panel.add(lbSceneNo, "gapleft 10");
			panel.add(tfSceneNo);
			panel.add(lbPart, "gapleft 10");
			panel.add(tfPart, "growx");
			panel.add(lbStatus);
			panel.add(cobStatus, "sg");

			panel.add(lbTitle, "top");
			panel.add(spTitle, "growx");
			panel.add(lbSummary, "top");
			panel.add(spSummary, "grow,split 2");

			// panel for command buttons
			JPanel commandPanel = new JPanel(new MigLayout("wrap"));
			commandPanel.setName("command_panel");
			if (SpellCheckerTools.isSpellCheckActive()) {
				commandPanel.add(btSpellCheck, "gapbottom 10");
			}
			commandPanel.add(btImport);
			commandPanel.add(btExport);
			panel.add(commandPanel, "aligny top");

			// panel for character and location links
			JPanel charAndLocPanel = refreshCharactersLocations();
			tabbedPane.addTab(I18N.getMsg("msg.character.and.locations"),
					charAndLocPanel);
			tabbedPane.setIconAt(1,
					I18N.getIcon("icon.medium.character.location"));
			
			// notes
			taNotes = createNotesTextArea();
			tabbedPane.addTab(I18N.getMsg("msg.common.notes"),
					SwingTools.createNotesPanel(taNotes));
			tabbedPane.setIconAt(2, I18N.getIcon("icon.small.note"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JComboBox createSceneTimeDifferenceComboBox() {
		List<Scene> scenes = ScenePeer.doSelectAllByChapterNumberExcept(this
				.getScene());
		JComboBox retour = new JComboBox(
				scenes.toArray(new Scene[scenes.size()]));
		return retour;
	}

	private void updateRelativeDateValue() {
		this.rbRelativeDate.setSelected(true);
		int nbDays = (Integer) this.relativeSceneDaysDifferenceSpinner.getValue();
		Scene selectedScene = (Scene) this.cobRelativeSceneSelector
				.getSelectedItem();
		if (selectedScene != null) {
			Date relativeDate = (Date) selectedScene.getDate().clone();
			relativeDate = new Date(DateUtils.addDays(relativeDate, nbDays)
					.getTime());
			this.dateChooser.setDate(relativeDate);
		}
	}

	private JPanel createDatePanel() {
		MigLayout layout = new MigLayout(
				"wrap 5,insets 2 10 2 10",
				"[][][][][]",
				"[]");
		JPanel panel = new JPanel(layout);
		TitledBorder tb = new TitledBorder(I18N.getMsg(
				"msg.scenedialog.relativedate.title", true));
		// tb.setTitlePosition(TitledBorder.LEFT);
		panel.setBorder(tb);
		tb.setTitleColor(Color.BLACK);

		this.rbFixedDate = new JRadioButton(
				I18N.getMsg("msg.scenedialog.fixeddate"));
		this.rbFixedDate.addActionListener(this);

		// date chooser
		dateChooser = new DateChooser(date);
		DateChooserVerifier dateVerifier = new DateChooserVerifier();
		dateVerifier.setErrorLabel(getErrorLabel());
		dateChooser.setInputVerifier(dateVerifier);

		this.rbRelativeDate = new JRadioButton(
				I18N.getMsg("msg.scenedialog.relativedate"));
		this.rbRelativeDate.addActionListener(this);
		// scene time difference chooser
		JLabel lbDateDiff = new JLabel(
				I18N.getMsg("msg.scenedialog.relativedate.occurs"));
		JLabel lbDateDiffAfter = new JLabel(
				I18N.getMsg("msg.scenedialog.relativedate.after"));
		this.relativeSceneDaysDifferenceSpinner = new JSpinner();
		this.relativeSceneDaysDifferenceSpinner
				.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent arg0) {
						updateRelativeDateValue();
					}
				});

		this.relativeSceneDaysDifferenceSpinner.setPreferredSize(new Dimension(50,
				20));
		this.cobRelativeSceneSelector = this.createSceneTimeDifferenceComboBox();
		this.cobRelativeSceneSelector.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				updateRelativeDateValue();
			}
		});
		ButtonGroup group = new ButtonGroup();
		group.add(this.rbFixedDate);
		group.add(this.rbRelativeDate);

		Scene scene = this.getScene();
		if (scene == null) {
			this.rbFixedDate.setSelected(true);
		} else {
			if (scene.getRelativeSceneId() == -1) {
				this.rbFixedDate.setSelected(true);
				this.relativeSceneDaysDifferenceSpinner.setEnabled(false);
				this.cobRelativeSceneSelector.setEnabled(false);
			} else {
				this.rbRelativeDate.setSelected(true);
				this.cobRelativeSceneSelector.setSelectedItem(ScenePeer
						.doSelectById(scene.getRelativeSceneId()));
				this.relativeSceneDaysDifferenceSpinner.setValue(new Integer(scene
						.getRelativeDateDifference()));
			}
		}

		panel.add(this.rbFixedDate);
		panel.add(dateChooser, "span 4");
		panel.add(this.rbRelativeDate);
		panel.add(lbDateDiff);
		panel.add(this.relativeSceneDaysDifferenceSpinner);
		panel.add(lbDateDiffAfter);
		panel.add(this.cobRelativeSceneSelector);
		return panel;
	}

	private PastelPanel createStrandPanel() {
		Strand strand = null;
		// got a strand? set the background color
		if (table != null) {
			strand = StrandPeer.doSelectById(((Scene) table).getStrandId());
		} else if (strandId != -1) {
			strand = StrandPeer.doSelectById(strandId);
		}

		PastelPanel strandPanel;
		MigLayout layout = new MigLayout("insets 5","[]","[top]");
		if (strand != null) {
			strandPanel = new PastelPanel(layout, strand.getColor());
		} else {
			strandPanel = new PastelPanel(layout, null);
		}
		strandPanel.setBorder(SwingTools.getBorderLightGray());

		// strand combo
		JLabel lbStrand = new JLabel(I18N.getMsgColon("msg.dlg.scene.strand",
				true));
		cobStrand = SwingTools.createStrandComboBox(this.strandId);
		cobStrand.addActionListener(this);

		// strand links
		JLabel lbStrandLink = new JLabel(
				I18N.getMsgColon("msg.dlg.scene.strand.links"));
		JPanel strandLinksPanel = strandFactory.createPanel();

		strandPanel.add(lbStrand);
		strandPanel.add(cobStrand, "gapafter 20");
		strandPanel.add(lbStrandLink);
		strandPanel.add(strandLinksPanel);
				
		return strandPanel;
	}

	/**
	 * Creates and returns a combination box of chapters.
	 * 
	 * @return a {@link JComboBox} of {@link Chapter}s.
	 */
	private JComboBox createChapterComboBox() {
		try {
			JComboBox cb;
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			Chapter chapter = new Chapter();
			chapter.setChapterNo(0);
			chapter.setTitle("---");
			model.addElement(chapter);
			DbTable selected = null;
			for (DbTable t : ChapterPeer.doSelectAll()) {
				if (t.getId() == this.chapterId) {
					selected = t;
				}
				if (getScene() != null
						&& t.getId() == this.getScene().getChapterId()) {
					selected = t;
				}
				model.addElement(t);
			}
			cb = new JComboBox();
			cb.setModel(model);
			if (selected != null) {
				cb.setSelectedItem(selected);
			}
			return cb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates a combination box that reflects the status of a scene.
	 * 
	 * @return a {@link JComboBox} of {@link Status} objects.
	 */
	private JComboBox createStatusComboBox() {
		try {
			JComboBox cb;
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			Status selected = null;
			for (Status status : Status.values()) {
				if (status == Status.NONE) {
					continue;
				}
				model.addElement(status);
				if (getScene() != null
						&& getScene().getStatus() == status.ordinal()) {
					selected = status;
				}
			}
			cb = new JComboBox();
			cb.setModel(model);
			if (selected != null) {
				cb.setSelectedItem(selected);
			}
			
			Map<Object, Icon> icons = new HashMap<Object, Icon>();
			icons.put(Status.OUTLINE, I18N.getIcon("icon.small.status.outline"));
			icons.put(Status.DRAFT, I18N.getIcon("icon.small.status.draft"));
			icons.put(Status.FIRST_EDIT,
					I18N.getIcon("icon.small.status.edit1"));
			icons.put(Status.SECOND_EDIT,
					I18N.getIcon("icon.small.status.edit2"));
			icons.put(Status.DONE, I18N.getIcon("icon.small.status.done"));
			cb.setRenderer(new IconListRenderer(icons));
			
			return cb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Populates the generated dialog with information from a {@link Scene}
	 * 
	 * @param action
	 *            an {@link Action} that may be cast as a {@link Scene}.
	 */
	@Override
	protected void setValuesFromAction(Action action) {
		Scene actionScene = (Scene) action
				.getValue(AbstractTableAction.ActionKey.SCENE.toString());
		if (actionScene != null) {
			strandId = actionScene.getStrandId();
			chapterId = actionScene.getChapterId();
		} else {
			Chapter chapter = (Chapter) action
					.getValue(AbstractTableAction.ActionKey.CHAPTER.toString());
			if (chapter != null) {
				chapterId = chapter.getId();
			}
		}
		Date actionDate = (Date) action
				.getValue(AbstractTableAction.ActionKey.DATE.toString());
		if (actionDate != null) {
			date = actionDate;
		}
	}

	/**
	 * @see ch.intertec.storybook.view.AbstractModifyDialog#setValuesFromTable()
	 */
	@Override
	protected void setValuesFromTable() {
		Scene scene = (Scene) table;

		// scene no.
		if (scene.getSceneNo() > 0) {
			tfSceneNo.setText(scene.getSceneNoStr());
			tfSceneNo.getUndoManager().discardAllEdits();
		}

		// date
		dateChooser.setDate(scene.getDate());

		// chapter combo box
		DefaultComboBoxModel model = (DefaultComboBoxModel) cobChapter
				.getModel();
		for (int i = 0; i < model.getSize(); ++i) {
			Chapter chapter = (Chapter) model.getElementAt(i);
			if (chapter.getId() == scene.getChapterId()) {
				cobChapter.setSelectedItem(chapter);
				if (chapter.getPart() != null) {
					tfPart.setText(chapter.getPart().toString());
				} else {
					tfPart.setText("");
				}
				break;
			}
		}

		// status combo box
		model = (DefaultComboBoxModel) cobStatus.getModel();
		for (int i = 0; i < model.getSize(); ++i) {
			Status status = (Status) model.getElementAt(i);
			if (status.ordinal() == scene.getStatus()) {
				cobStatus.setSelectedItem(status);
				break;
			}
		}

		// strand combo box
		model = (DefaultComboBoxModel) cobStrand.getModel();
		for (int i = 0; i < model.getSize(); ++i) {
			Strand strand = (Strand) model.getElementAt(i);
			if (strand.getId() == scene.getStrandId()) {
				cobStrand.setSelectedItem(strand);
				break;
			}
		}

		// title
		taTitle.setText(scene.getTitle());
		taTitle.setCaretPosition(0);
		taTitle.getUndoManager().discardAllEdits();

		// summary
		taSummary.setText(scene.getText());
		taSummary.setCaretPosition(0);
		taSummary.getUndoManager().discardAllEdits();

		// notes
		taNotes.setText(scene.getNotes());
		taNotes.setCaretPosition(0);
		taNotes.getUndoManager().discardAllEdits();

		SwingTools.checkInputComponents(inputComponentList);
		
		if (taTitle.getText().isEmpty()) {
			setFocusComponent(taTitle);
		} else {
			setFocusComponent(taSummary);
		}
	}

	private AbstractAction getSpellCheckAction() {
		if (spellCheckAction == null) {
			spellCheckAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					try {
						SpellChecker.showSpellCheckerDialog(taTitle,
								new SpellCheckerOptions());
						SpellChecker.showSpellCheckerDialog(taSummary,
								new SpellCheckerOptions());
					} catch (IllegalArgumentException e) {
						// ignore
						// sometimes this crashes with a
						// java.lang.IllegalArgumentException: offset out of
						// bounds
						// seems to be a bug in the spell checker
					}
				}
			};
		}
		return spellCheckAction;
	}

	/**
	 * Makes or updates the scene that was edited within the dialog.
	 * 
	 * @see ch.intertec.storybook.view.AbstractModifyDialog#makeOrUpdate(ch.intertec.storybook.view.AbstractModifyDialog,
	 *      boolean)
	 */
	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {

		// make or update scene
		table = ScenePeer.makeOrUpdateScene((SceneDialog) getThis(), edit);
	}

	/**
	 * Returns the relative date radio button.
	 * 
	 * @return the relative date radio button.
	 */
	public JRadioButton getRelativeDateButton() {
		return this.rbRelativeDate;
	}

	/**
	 * Returns the relative days spinner.
	 * 
	 * @return the relative days spinner.
	 */
	public JSpinner getRelativeDateSpinner() {
		return this.relativeSceneDaysDifferenceSpinner;
	}

	/**
	 * Returns the relative scene combo box.
	 * 
	 * @return the relative scene combo box.
	 */
	public JComboBox getRelativeSceneComboBox() {
		return this.cobRelativeSceneSelector;
	}

	/**
	 * Returns the date chooser widget.
	 * 
	 * @return the date chooser widget.
	 */
	public JDateChooser getDateChooser() {
		return dateChooser.getJDateChooser();
	}

	/**
	 * Returns the scene number text field.
	 * 
	 * @return the {@link JTextField} containing the scene number.
	 */
	public JTextField getSceneTextField() {
		return tfSceneNo;
	}

	/**
	 * Returns the text area containing the scene summary.
	 * 
	 * @return the {@link JTextArea} containing the scene summary.
	 */
	public JTextArea getSummaryTextArea() {
		return taSummary;
	}

	public JTextArea getNotesTextArea() {
		return taNotes;
	}

	/**
	 * Returns the text area containing the scene title.
	 * 
	 * @return the {@link JTextArea} containing the scene title.
	 */
	public JTextArea getTitleTextArea() {
		return taTitle;
	}

	/**
	 * Returns the combination box with the scene status.
	 * 
	 * @return the {@link JComboBox} with the scene status.
	 */
	public JComboBox getStatusComboBox() {
		return cobStatus;
	}

	/**
	 * Returns the {@link Scene} data object associated with the scene.
	 * 
	 * @return the {@link Scene} data object associated with the scene.
	 */
	public Scene getScene() {
		return (Scene) table;
	}

	/**
	 * Returns the combination box with the scene strands.
	 * 
	 * @return the {@link JComboBox} with the scene strands.
	 */
	public JComboBox getStrandComboBox() {
		return cobStrand;
	}

	/**
	 * Returns the combination box with the scene chapter.
	 * 
	 * @return the {@link JComboBox} with the scene chapter.
	 */
	public JComboBox getChapterComboBox() {
		return cobChapter;
	}

	/**
	 * Returns a list of strands linked to in this scene.
	 * 
	 * @return a {@link List} of strands linked to in this scene.
	 */
	public List<Object> getStrandLinksList() {
		return strandLinksList;
	}

	/**
	 * Returns a list of characters linked to in this scene.
	 * 
	 * @return a {@link JList} of characters linked to in this scene.
	 */
	public JList getCharacterList() {
		return charactersLocationsPanel.getChoosenCharacterList();
	}

	/**
	 * Returns a list of locations linked to in this scene.
	 * 
	 * @return a {@link JList} of locations linked to in this scene.
	 */
	public JList getLocationList() {
		return charactersLocationsPanel.getChoosenLocationList();
	}
	
	@Override
	public void windowClosed(WindowEvent evt) {
		super.windowClosed(evt);
		SpellChecker.unregister(taSummary);
	}

	/**
	 * This method overrides the default to set the chapter text after a chapter
	 * has been selected.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == cobChapter) {
			// chapter combo box
			Chapter chapter = (Chapter) cobChapter.getSelectedItem();
			if (chapter.getPart() != null) {
				tfPart.setText(chapter.getPart().toString());
			} else {
				tfPart.setText("");
			}
		} else if (evt.getSource() == cobStrand) {
			// strand combo box
			Strand strand = (Strand) cobStrand.getSelectedItem();
			strandPanel.setColor(strand.getColor());
		} else if (evt.getSource() == this.rbRelativeDate) {
			if (this.cobRelativeSceneSelector.getItemCount() > 0) {
				this.relativeSceneDaysDifferenceSpinner.setEnabled(true);
				this.cobRelativeSceneSelector.setEnabled(true);
				this.updateRelativeDateValue();
			} else {
				JOptionPane.showMessageDialog(this, I18N.getMsg("msg.scenedialog.relativedate.nosceneavailable"), "", JOptionPane.INFORMATION_MESSAGE);
				this.rbFixedDate.setSelected(true);
			}
		} else if (evt.getSource() == this.rbFixedDate) {
			this.relativeSceneDaysDifferenceSpinner.setEnabled(false);
			this.cobRelativeSceneSelector.setEnabled(false);
			if (this.getScene() != null) {
				this.dateChooser.setDate(this.getScene().getDate());
			} else {
				this.dateChooser.setDate(date);
			}
		}
	}

	/**
	 * Gets the import action.
	 * 
	 * @return the import action
	 */
	public AbstractAction getImportAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				final JFileChooser chooser = SwingTools.getTextFileChooser();
				int returnVal = chooser.showOpenDialog(getThis());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						File file = chooser.getSelectedFile();
						String text = FileTools.importTextFromFile(file);
						// set the summary text
						getThis().getSummaryTextArea().setText(text);
					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(MainFrame.getInstance(),
								e.getLocalizedMessage(), "Warning",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		};
	}

	/**
	 * Gets the export action.
	 * 
	 * @return the export action
	 */
	public AbstractAction getExportAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				final JFileChooser chooser = SwingTools.getTextFileChooser();
				int returnVal = chooser.showOpenDialog(getThis());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					file = FileTools.ensureFileHasExtension(file, "txt");
					// write both the scene title and
					// the scene summary to a text file
					StringBuffer buf = new StringBuffer();
					buf.append(getThis().getTitleTextArea().getText());
					buf.append("\n\n");
					buf.append(getThis().getSummaryTextArea().getText());
					ExportTools.exportTextToFile(file, buf.toString());
					JOptionPane.showMessageDialog(getThis(),
							I18N.getMsg("msg.common.export.success") + "\n"
									+ file);
				}
			}

		};
	}

	/**
	 * Returns its self but overrides parent to return specific subclass.
	 * 
	 * @see ch.intertec.storybook.view.AbstractModifyDialog#getThis()
	 */
	public SceneDialog getThis() {
		return this;
	}

	/**
	 * Builds the character and location links panel to associate with a scene.
	 * 
	 * @return the built {@link JPanel}
	 */
	public JPanel refreshCharactersLocations() {
		if (charactersLocationsPanel == null) {
			charactersLocationsPanel = new CharactersLocationsPanel(getScene());
		}
		return charactersLocationsPanel;
	}

	// TODO
//	@Override
//	public void mouseClicked(MouseEvent e) {
//		JComponent comp = (JComponent) e.getSource();
//		if (SwingUtilities.isRightMouseButton(e)) {
//			Point p = SwingUtilities.convertPoint(comp, e.getPoint(), this);
//			JPopupMenu menu = createPopupMenu(comp);
//			popup.show(this, p.x, p.y);
//		}
//	}
//
//	@Override
//	public void mousePressed(MouseEvent e) {
//	}
//
//	@Override
//	public void mouseReleased(MouseEvent e) {
//	}
//
//	@Override
//	public void mouseEntered(MouseEvent e) {
//	}
//
//	@Override
//	public void mouseExited(MouseEvent e) {
//	}
}
