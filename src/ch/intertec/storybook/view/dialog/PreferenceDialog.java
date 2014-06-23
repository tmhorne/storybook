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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.InternalPeer;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.SpellCheckerTools;
import ch.intertec.storybook.toolkit.Constants.Language;
import ch.intertec.storybook.toolkit.Constants.LookAndFeel;
import ch.intertec.storybook.toolkit.Constants.Spelling;
import ch.intertec.storybook.toolkit.Constants.StartOption;
import ch.intertec.storybook.toolkit.net.NetTools;
import ch.intertec.storybook.toolkit.swing.FontChooser;
import ch.intertec.storybook.toolkit.swing.SwingTools;

/**
 * Shows the preference dialog where things like language,
 * spell checking language, font and others can be set. 
 * 
 * @author Martin Mustun
 *
 */
@SuppressWarnings("serial")
public class PreferenceDialog extends JDialog {

	private AbstractAction okAction;
	private AbstractAction cancelAction;
	private AbstractAction applyAction;
	private AbstractAction fontChooserAction;
		
	private JPanel panel;
	private JComboBox cobLanguage;
	private JComboBox cobSpelling;
	private JComboBox cobLaf;
	private JComboBox cobStart;
	private JCheckBox chbConfirmExit;
	private JCheckBox chbUpdate;
	private JLabel lbShowFont;
	private Font font;
	private JTabbedPane tabbedPane;
	private JTextField tfGoogleMapsUrl;
	private JCheckBox chbTranslatorMode;
	private JCheckBox chbGradient;
	private JCheckBox chbDateDiff;
	private JTextField tfSfChrono;
	private JTextField tfSfBook;
	private JTextField tfSfManage;
	
	public PreferenceDialog() {
		super();
		font = MainFrame.getInstance().getDefaultFont();
		initPanel();
		initGUI();
	}
	
	public PreferenceDialog(JFrame frame) {
		super(frame);
		font = MainFrame.getInstance().getDefaultFont();
		initGUI();
	}

	private void initPanel(){
		if(panel == null){
			MigLayout layout = new MigLayout(
					"wrap,fill",
					"[]",
					"[grow][]");
			panel = new JPanel(layout);
		}
	}
	
	private void initGUI() {
		setTitle(I18N.getMsg("msg.dlg.preference.title"));
		setIconImage(I18N.getImageIcon("icon.small.preferences").getImage());

		setPreferredSize(new Dimension(700, 400));
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(
				I18N.getMsg("msg.dlg.preference.global"),
				createCommonPanel());
		tabbedPane.addTab(
				I18N.getMsg("msg.dlg.preference.appearance"),
				createAppearancePanel());
		tabbedPane.addTab(
				"Internet",
				createInternetPanel());
		tabbedPane.addTab(
				"Translators",
				createTranslatorsPanel());

		JButton btApply = new JButton();
		btApply.setAction(getApplyAction());
		btApply.setText(I18N.getMsg("msg.common.apply"));

		JButton btOk = new JButton();
		btOk.setAction(getOkAction());
		btOk.setText(I18N.getMsg("msg.common.ok"));
		SwingTools.addEnterAction(btOk, getOkAction());
		
		JButton btCancel = new JButton();
		btCancel.setAction(getCancelAction());
		btCancel.setText(I18N.getMsg("msg.common.cancel"));
		btCancel.setIcon(I18N.getIcon("icon.small.close"));
		SwingTools.addEscAction(btCancel, getCancelAction());
		
		// layout
		panel.add(tabbedPane, "grow");
		panel.add(btApply, "span,split 3,sg");
		panel.add(btOk, "sg,gap push");
		panel.add(btCancel, "sg");
		
		add(panel);
	}

	private JPanel createCommonPanel(){
		MigLayout layout = new MigLayout(
				"wrap 2",
				"",
				"[]10");
		JPanel panel = new JPanel(layout);
		
		// language
		JLabel lbLanguage = new JLabel(
				I18N.getMsgColon("msg.common.language"));
		DefaultComboBoxModel cbmLanguage = new DefaultComboBoxModel();
		for (Language lang : Language.values()) {
			cbmLanguage.addElement(lang.getI18N());
		}
		cobLanguage = new JComboBox(cbmLanguage);
		String currentLangStr = I18N.getCountryLanguage(Locale.getDefault());
		Language lang = Language.valueOf(currentLangStr);
		cobLanguage.setSelectedIndex(lang.ordinal());
		
		// spelling
		JLabel lbSpelling = new JLabel(
				I18N.getMsgColon("msg.pref.spelling"));
		DefaultComboBoxModel cbmSpelling = new DefaultComboBoxModel();
		for (Spelling spelling : Spelling.values()) {
			cbmSpelling.addElement(spelling.getI18N());
		}
		cobSpelling = new JComboBox(cbmSpelling);
		String spellingStr = PrefManager.getInstance().getStringValue(
				Constants.Preference.SPELLING);
		Spelling spelling = Spelling.valueOf(spellingStr);
		cobSpelling.setSelectedIndex(spelling.ordinal());
		
		// start options
		JLabel lbStart = new JLabel(I18N.getMsg("msg.pref.start"));
		DefaultComboBoxModel cbmStart = new DefaultComboBoxModel();
		for (StartOption so : StartOption.values()) {
			cbmStart.addElement(so.getI18N());
		}
		StartOption so = StartOption.donothing;
		try {
			cobStart = new JComboBox(cbmStart);
			String currentStartup = PrefManager.getInstance().getStringValue(
					Constants.Preference.START);
			so = StartOption.valueOf(currentStartup);
		} catch (IllegalArgumentException e) {
			// ignore
		}
		cobStart.setSelectedIndex(so.ordinal());
		
		// confirm exit
		JLabel lbConfirmExit = new JLabel(I18N.getMsg("msg.pref.exit"));
		chbConfirmExit = new JCheckBox(I18N.getMsg("msg.pref.exit.chb"));
		Boolean confirm = PrefManager.getInstance().getBooleanValue(
				Constants.Preference.CONFIRM_EXIT);		
		chbConfirmExit.getModel().setSelected(confirm);

		// check for updates
		JLabel lbUpdate = new JLabel(I18N.getMsg("msg.pref.update"));
		chbUpdate = new JCheckBox(I18N.getMsg("msg.pref.update.chb"));
		Boolean updates = PrefManager.getInstance().getBooleanValue(
				Constants.Preference.CHECK_UPDATES);
		chbUpdate.getModel().setSelected(updates);
		
		// layout
		panel.add(lbLanguage);
		panel.add(cobLanguage);
		panel.add(lbSpelling);
		panel.add(cobSpelling);		
		panel.add(lbStart);
		panel.add(cobStart);
		panel.add(lbConfirmExit);
		panel.add(chbConfirmExit);
		if(!Constants.Application.IS_PRO_VERSION.toBoolean()){			
			panel.add(lbUpdate);
			panel.add(chbUpdate);
		}
		
		return panel;
	}
	
	private JPanel createAppearancePanel(){
		MigLayout layout = new MigLayout(
				"wrap 2",
				"",
				"[]20[][]");
		JPanel panel = new JPanel(layout);
		
		// look and feel
		JLabel lbLaf = new JLabel(I18N.getMsg("msg.pref.laf") + ": ");
		DefaultComboBoxModel lafModel = new DefaultComboBoxModel();
		for (Constants.LookAndFeel laf : Constants.LookAndFeel.values()) {
			lafModel.addElement(laf.getI18N());
		}
		String currentLaf = PrefManager.getInstance().getStringValue(
				Constants.Preference.LAF);
		Constants.LookAndFeel laf = Constants.LookAndFeel.valueOf(currentLaf);
		cobLaf = new JComboBox(lafModel);
		cobLaf.setSelectedIndex(laf.ordinal());

		// standard font
		JLabel lbFont = new JLabel(I18N.getMsgColon("msg.pref.font.standard"));
		JButton btFont = new JButton();
		btFont.setAction(getFontChooserAction());
		btFont.setText(I18N.getMsg("msg.pref.font.standard.bt"));
		lbShowFont = new JLabel();
		lbShowFont.setText(SwingTools.getNiceFontName(font));
		JLabel lbCurrentFont = new JLabel(I18N
				.getMsgColon("msg.pref.font.standard.current"));
		
		// scale factor chrono view
		JLabel lbSfChrono = new JLabel(
				I18N.getMsgColon("msg.pref.scale.factor.chrono"));
		tfSfChrono = new JTextField(10);
		tfSfChrono.setText(new Integer(InternalPeer.getScaleFactorChrono())
				.toString());
		JLabel lbSfBook = new JLabel(
				I18N.getMsgColon("msg.pref.scale.factor.book"));
		tfSfBook = new JTextField(10);
		tfSfBook.setText(new Integer(InternalPeer.getScaleFactorBook())
				.toString());
		JLabel lbSfManage = new JLabel(
				I18N.getMsgColon("msg.pref.scale.factor.manage"));
		tfSfManage = new JTextField(10);
		tfSfManage.setText(new Integer(InternalPeer.getScaleFactorManage())
				.toString());
		
		// show gradient
		JLabel lbGradient = new JLabel(I18N
				.getMsgColon("msg.pref.gradient.background"));
		chbGradient = new JCheckBox(I18N
				.getMsg("msg.pref.gradient.background.show"));
		chbGradient.setSelected(MainFrame.getInstance().showBgGradient());

		// show date difference
		JLabel lbDateDiff = new JLabel(I18N
				.getMsgColon("msg.pref.datediff"));
		chbDateDiff = new JCheckBox(I18N
				.getMsg("msg.pref.datediff.show"));
		Boolean showdatediff = PrefManager.getInstance().getBooleanValue(
				Constants.Preference.SHOW_DATE_DIFFERENCE);
		if (showdatediff == null) {
			showdatediff = false;
		}
		chbDateDiff.setSelected(showdatediff);

		// layout
		panel.add(lbLaf);
		panel.add(cobLaf, "gap bottom 8");
		
		panel.add(lbCurrentFont);
		panel.add(lbShowFont);
		panel.add(lbFont);
		panel.add(btFont, "gap bottom 16");
		
		panel.add(lbSfChrono);
		panel.add(tfSfChrono);
		panel.add(lbSfBook);
		panel.add(tfSfBook);
		panel.add(lbSfManage);
		panel.add(tfSfManage, "gap bottom 10");
		
		panel.add(lbGradient);
		panel.add(chbGradient);
		panel.add(lbDateDiff);
		panel.add(chbDateDiff);
		
		return panel;
	}

	private JPanel createInternetPanel(){
		MigLayout layout = new MigLayout(
				"wrap 2",
				"[][fill,grow]",
				"");
		JPanel panel = new JPanel(layout);
		
		// Google Maps URL
		JLabel lbGoogleMapsUrl = new JLabel("Google Maps URL:");
		tfGoogleMapsUrl = new JTextField();
		tfGoogleMapsUrl.setText(NetTools.getGoogleMapsUrl());		
		
		// layout
		panel.add(lbGoogleMapsUrl);
		panel.add(tfGoogleMapsUrl);
		
		return panel;
	}

	private JPanel createTranslatorsPanel(){
		MigLayout layout = new MigLayout(
				"wrap 2",
				"[][fill,grow]",
				"");
		JPanel panel = new JPanel(layout);
		
		// translator mode
		JLabel lbEnableTranslatorMode = new JLabel("Translator Mode:");
		chbTranslatorMode = new JCheckBox("Enable Translator Mode");
		chbTranslatorMode.setSelected(
				MainFrame.getInstance().isInTranslatorMode());

		// layout
		panel.add(lbEnableTranslatorMode);
		panel.add(chbTranslatorMode);
		
		return panel;
	}
	
	/**
	 * Gets the font chooser action and instances it when
	 * calling first.
	 * 
	 * @return the action
	 */
	public AbstractAction getFontChooserAction() {
		if (fontChooserAction == null) {
			fontChooserAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {					
					Font newFont = FontChooser.showDialog(null, null, font);
					if (newFont == null) {
						return;
					}
					lbShowFont.setFont(newFont);
					lbShowFont.setText(SwingTools.getNiceFontName(newFont));
					font = newFont;
				}
			};
		}
		return fontChooserAction;
	}
	private JDialog getThis() {
		return this;
	}
	
	private void refresh(){
		int index = tabbedPane.getSelectedIndex();
		panel.removeAll();
		initGUI();
		panel.validate();
		panel.repaint();
		pack();
		tabbedPane.setSelectedIndex(index);
	}

	private AbstractAction getCancelAction() {
		if (cancelAction == null) {
			cancelAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					getThis().dispose();
				}
			};
		}
		return cancelAction;
	}

	private AbstractAction getOkAction() {
		if (okAction == null) {
			okAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					applyNewSettings();
					getThis().dispose();
				}
			};
		}
		return okAction;
	}

	private AbstractAction getApplyAction() {
		if (applyAction == null) {
			applyAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					applyNewSettings();
					refresh();
				}
			};
		}
		return applyAction;
	}

	/**
	 * Applies and saves the new settings. 
	 */
	private void applyNewSettings() {
		try {
			SwingTools.setWaitCursor(this);
			PrefManager prefManager = PrefManager.getInstance();
			MainFrame mainFrame = MainFrame.getInstance();
			
			// language
			int i = cobLanguage.getSelectedIndex();
			Language lang = Language.values()[i];
			Locale locale = lang.getLocale();
			prefManager.setValue(Constants.Preference.LANG,
					I18N.getCountryLanguage(locale));

			// spelling
			i = cobSpelling.getSelectedIndex();
			Spelling spelling = Spelling.values()[i];			
			prefManager.setValue(
					Constants.Preference.SPELLING, spelling.name());
			SpellCheckerTools.registerDictionaries();
			
			// look and feel
			i = cobLaf.getSelectedIndex();
			LookAndFeel laf = LookAndFeel.values()[i];
			SwingTools.setLookAndFeel(laf);			
			
			// default font
			mainFrame.setDefaultFont(font);
			try {
				prefManager.setValue(
						Constants.Preference.FONT_DEFAULT_NAME, font.getName());
				prefManager.setValue(
						Constants.Preference.FONT_DEFAULT_STYLE, font.getStyle());
				prefManager.setValue(
						Constants.Preference.FONT_DEFAULT_SIZE, font.getSize());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// scale factors
			try {
				int sfChrono = new Integer(tfSfChrono.getText());
				if (sfChrono < 10) {
					sfChrono = 10;
				}
				InternalPeer.setScaleFactorChrono(sfChrono);
				
				int sfBook = new Integer(tfSfBook.getText());
				if (sfBook < 10) {
					sfBook = 10;
				}
				InternalPeer.setScaleFactorBook(sfBook);
				
				int sfManage = new Integer(tfSfManage.getText());
				if (sfManage < 1) {
					sfManage = 1;
				}
				InternalPeer.setScaleFactorManage(sfManage);
			} catch (NumberFormatException e) {
				InternalPeer.setScaleFactorDefaults();
			}
			
			// background gradient
			boolean showBgGradient = chbGradient.isSelected();
			prefManager.setValue(Constants.Preference.SHOW_BG_GRADIENT,
					showBgGradient);
			mainFrame.setShowBgGrandient(showBgGradient);

			// show date difference
			boolean showDateDiff = chbDateDiff.isSelected();
			prefManager.setValue(Constants.Preference.SHOW_DATE_DIFFERENCE,
					showDateDiff);
			
			// start options
			i = cobStart.getSelectedIndex();
			StartOption so = StartOption.values()[i];
			prefManager.setValue(
					Constants.Preference.START, so.name());

			// confirm exit
			prefManager.setValue(
					Constants.Preference.CONFIRM_EXIT,
					chbConfirmExit.getModel().isSelected());

			// check for updates
			prefManager.setValue(
					Constants.Preference.CHECK_UPDATES,
					chbUpdate.getModel().isSelected());

			// google map URL
			NetTools.setGoogleMapUrl(tfGoogleMapsUrl.getText());
			prefManager.setValue(
					Constants.Preference.GOOGLE_MAP_URL,
					NetTools.getGoogleMapsUrl());			
			
			// translator mode
			if (chbTranslatorMode.isSelected()) {
				mainFrame.setTranslatorMode(true);
			} else {
				mainFrame.setTranslatorMode(false);
			}
			
			// re-initialize and refresh GUI
			I18N.initResourceBundles(locale);
			PCSDispatcher.getInstance().firePropertyChange(
					Property.REFRESH_ALL, null, null);
			SwingTools.setDefaultCursor(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
