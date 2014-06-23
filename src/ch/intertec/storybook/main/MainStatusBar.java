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

package ch.intertec.storybook.main;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public class MainStatusBar extends JPanel implements PropertyChangeListener,
		IRefreshable {
	
	private JLabel lbInfo;
	private JLabel lbWarning;
//	private JSlider scaleSlider;

	public MainStatusBar() {
		initGUI();
		
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.removeListenersByClass(this.getClass());
		pcs.addPropertyChangeListener(Property.PROJECT, this);
		pcs.addPropertyChangeListener(Property.ACTIVE_PART, this);
		pcs.addPropertyChangeListener(Property.VIEW, this);
	}

	private void initGUI() {
		if (!ProjectTools.isProjectOpen()) {
			return;
		}
		
//		MigLayout layout = new MigLayout(
//				"insets 0 n 0 n",
//				"[grow,fill][grow,fill][]0[]",
//				"[center]");
		MigLayout layout = new MigLayout(
				"insets 0",
				"[grow,fill]0[grow,fill]",
				"[center]"
		);
		setLayout(layout);
		
		lbInfo = new JLabel(" ");
		lbInfo.setBorder(SwingTools.getEtchedBorder());
		lbWarning = new JLabel(" ");
		lbWarning.setBorder(SwingTools.getEtchedBorder());
		setWarningLabel();
		setInfoLabel();
		
		// switch alignment button
//		IconButton btAlignment = new IconButton("icon.small.switch",
//				getSwitchLayoutAction());
//		btAlignment.setSize20x20();
//		btAlignment.setToolTipText(I18N.getMsg("msg.statusbar.switch.layout"));
//		btAlignment.setNoBorder();
//		MainFrame mainFrame = MainFrame.getInstance();
//		if (mainFrame.getContentPanelType() != ContentPanelType.CHRONO) {
//			btAlignment.setEnabled(false);
//		}
		
//		// view size
//		int min = 1;
//		int max = 16;
//		MainSplitPane splitPane = MainFrame.getInstance().getSplitPane();
//		if (splitPane.getContentPanelType() == ContentPanelType.CHRONO) {
//			min = 2;
//		}
//		int tick = splitPane.getContentPanelType().getScale();
//		scaleSlider = SwingTools.createSafeSlider(JSlider.HORIZONTAL, min, max,
//				tick);
//		scaleSlider.setMinorTickSpacing(1);
//		scaleSlider.setMajorTickSpacing(2);
//		scaleSlider.setPaintTicks(false);
//		scaleSlider.setPaintLabels(false);
//		scaleSlider.setSnapToTicks(false);
//		scaleSlider.setPreferredSize(new Dimension(200, 10));
//		scaleSlider.addChangeListener(this);

		add(lbInfo);
		add(lbWarning);
//		add(scaleSlider);
//		add(btAlignment);
	}
		
	@Override
	public void refresh() {
		removeAll();
		initGUI();
		revalidate();
	}
		
	private void setInfoLabel() {
		try {
			StringBuffer buf = new StringBuffer();
			Part part = PartPeer.doSelectById(
					MainFrame.getInstance().getActivePartId());
			buf.append(part);
			buf.append(" / ");

			List<Integer> chapterList = ChapterPeer.doSelectIds();
			if (!chapterList.isEmpty()) {
				int min = Collections.min(chapterList);
				int max = Collections.max(chapterList);
				buf.append(I18N.getMsgColon("msg.common.chapters"));
				buf.append(" " + min + " - " + max);
			}
			lbInfo.setText(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setWarningLabel() {
		try {			
			if (!ProjectTools.isProjectOpen()) {
				lbWarning.setText(" ");
				return;
			}

			// check whether some chapter are missing or found twice
			java.util.List<Integer> notFoundList = new ArrayList<Integer>();
			java.util.List<Integer> foundTwiceList = new ArrayList<Integer>();
			ChapterPeer.checkChapterNumbers(notFoundList, foundTwiceList);
			StringBuffer buf = new StringBuffer(" ");
			if (!notFoundList.isEmpty()) {
				buf.append(I18N.getMsg("msg.warning.missing.chapters",
						notFoundList.toString()));
			}
			if (!notFoundList.isEmpty() && !foundTwiceList.isEmpty()) {
				buf.append(" - ");
			}
			if (!foundTwiceList.isEmpty()) {
				buf.append(I18N.getMsg("msg.warning.chapters.twice",
						foundTwiceList.toString()));
			}
			buf.append(" ");
			lbWarning.setText(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	private Action getSwitchLayoutAction() {
//		return new AbstractAction() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				MainFrame mainFrame = MainFrame.getInstance();
//				if (mainFrame.getContentPanelType() == ContentPanelType.CHRONO) {
//					NewChronoContentPanel cp = (NewChronoContentPanel) mainFrame
//							.getContentPanel();
//					cp.switchLayoutAlignment();
//				}
//			}
//		};
//	}
	
//	@Override
//	public void stateChanged(ChangeEvent evt) {
//		JSlider source = (JSlider) evt.getSource();
//		if (!source.getValueIsAdjusting()) {
//			adjustViewSize(source.getValue());
//		}
//	}
//
//	private void adjustViewSize(int pos) {
//		SwingTools.setWaitCursor(this);
//		ContentPanelType contentPanelType = MainFrame.getInstance()
//				.getSplitPane().getContentPanelType();
//		Integer old = contentPanelType.getScale();
//		contentPanelType.setScale(pos);
//		PCSDispatcher.getInstance().firePropertyChange(
//				PCSDispatcher.Property.SCALE.toString(), old, new Integer(pos));
//		SwingTools.setDefaultCursor(this);
//	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.PROJECT, evt)) {
			refresh();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.ACTIVE_PART, evt)) {
			refresh();
			return;
		}
		
		if (PCSDispatcher.isPropertyFired(Property.VIEW, evt)) {
			refresh();
			return;
		}		
	}
}
