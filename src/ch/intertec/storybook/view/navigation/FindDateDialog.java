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

package ch.intertec.storybook.view.navigation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.lang3.time.DateUtils;

import net.miginfocom.swing.MigLayout;

import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.panel.DateChooser;
import ch.intertec.storybook.view.IconButton;
import ch.intertec.storybook.view.ViewTools;

@SuppressWarnings("serial")
public class FindDateDialog extends javax.swing.JDialog implements
		PropertyChangeListener, ActionListener {

	private AbstractAction closeAction;
	
	private static Date lastDate;
	
	private JComboBox strandCombo;
	private DateChooser dateChooser;
	private JLabel lbWarning;
	
	public FindDateDialog(JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout(
				"wrap 2",
				"[]10[grow]",
				"[][]20[]10[]");
		setLayout(layout);
		setTitle(I18N.getMsg("msg.menu.navigate.goto.date"));
		
		// strand label
		JLabel lbStrand = new JLabel();
		lbStrand.setText(I18N.getMsgColon("msg.common.strand"));
		
		// strand combo
		strandCombo = SwingTools.createStrandComboBox(-1);
		strandCombo.addActionListener(this);
		
		// date label
		JLabel lbDate = new JLabel();
		lbDate.setText(I18N.getMsgColon("msg.common.date"));
		
		// date chooser
		if (lastDate == null) {
			lastDate = ScenePeer.getFirstDate();
			dateChooser = new DateChooser();
		}
		dateChooser = new DateChooser(lastDate, false);
		dateChooser.addPropertyChangeListener(this);

		// previous day
		IconButton btPrevious = new IconButton(
				"icon.small.arrow.left",
				null,
				getPreviousDayAction());

		// next day
		IconButton btNext = new IconButton(
				"icon.small.arrow.right",
				null,
				getNextDayAction());
		
		lbWarning = new JLabel(" ");

		// find button
		JButton btFind = new JButton();
		btFind.setAction(getFindAction());
		btFind.setText(I18N.getMsg("msg.common.find"));
		btFind.setIcon(I18N.getIcon("icon.small.search"));
		SwingTools.addEnterAction(btFind, getFindAction());

		// close button
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		btClose.setIcon(I18N.getIcon("icon.small.close"));
		SwingTools.addEscAction(btClose, getCloseAction());
		
		// layout
		add(lbStrand, "sg 3");
		add(strandCombo, "sg 4");
		add(lbDate, "sg 3");
		add(dateChooser, "sg 4");
		add(btPrevious, "span,split 2,sg 2,al center");
		add(btNext, "sg 2");
		add(lbWarning, "span,wrap");
		add(btFind, "span,split 2, sg");
		add(btClose, "sg,gap push");
	}

	public void reset(){
		lastDate = null;
	}

	/**
	 * Returns the {@link DateChooser}.
	 * 
	 * @return the {@link DateChooser}
	 */
	public DateChooser getDateChooser() {
		return dateChooser;
	}
	
	private JDialog getThis() {
		return this;
	}
	
	private void scrollToStrandAndDate() {
		Date date = dateChooser.getDate();
		Strand strand = (Strand) strandCombo.getSelectedItem();
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		boolean found = ViewTools.scrollToStrandAndDate(strand, sqlDate);
		if (!found) {
			lbWarning.setText(I18N.getMsg("msg.dlg.navigation.date.not.found"));
		} else {
			lbWarning.setText(" ");
		}
	}

	private AbstractAction getNextDayAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					Date date = dateChooser.getDate();
					date = DateUtils.addDays(date, 1);
					dateChooser.setDate(date);
					scrollToStrandAndDate();
				} catch (IllegalArgumentException e) {
					lbWarning.setText(I18N.getMsg("msg.common.error"));
				}
			}
		};
	}

	private AbstractAction getPreviousDayAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					Date date = dateChooser.getDate();
					date = DateUtils.addDays(date, -1);
					dateChooser.setDate(date);
					scrollToStrandAndDate();
				} catch (IllegalArgumentException e) {
					lbWarning.setText(I18N.getMsg("msg.common.error"));
				}
			}
		};
	}

	private AbstractAction getFindAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				scrollToStrandAndDate();
			}
		};
	}

	private AbstractAction getCloseAction() {
		if (closeAction == null) {
			closeAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					getThis().dispose();
				}
			};
		}
		return closeAction;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getOldValue() != null && evt.getNewValue() != null) {
			lastDate = (Date) evt.getNewValue();
			scrollToStrandAndDate();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		scrollToStrandAndDate();
	}
}
