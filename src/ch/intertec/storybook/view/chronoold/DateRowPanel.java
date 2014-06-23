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

package ch.intertec.storybook.view.chronoold;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.util.List;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.main.MainSplitPane.ContentPanelType;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.content.chrono.cell.ChronoScenePanel;
import ch.intertec.storybook.view.content.chrono.cell.SpacePanel;

@SuppressWarnings("serial")
public class DateRowPanel extends JPanel implements PropertyChangeListener {

	public static final String COMP_NAME ="date_row_panel";
	
	private Date date;
	private boolean footer;
	private int row;

	public DateRowPanel(Date date, int row) {
		this(date, row, false);
	}

	public DateRowPanel(Date date, int row, boolean footer) {
		this.date = date;
		this.footer = footer;
		this.row = row;
		initGUI();
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(
				PCSDispatcher.Property.SCENE, this);
	}
	
	private void initGUI() {
		ContentPanelType contentPanelType = MainFrame.getInstance().getContentPanelType();
		int width = contentPanelType.getCalculatedScale();	
		MigLayout layout = new MigLayout(
				"",
				"[" + width + ",fill]",
				"[fill,top]");
		setLayout(layout);
		// setBackground(ChronoContentPanel.getRowColor(row));
		setBackground(SwingTools.getBackgroundColor());
		setName(createComponentName(getDate()));
				
		try {
			for (Strand strand : StrandPeer.doSelectAll()) {
				List<Scene> sceneList =
					ScenePeer.doSelectByStrandIdAndDate(strand.getId(), date);
				if (sceneList.isEmpty()) {
					// show space panel
					SpacePanel spacePanel = new SpacePanel(strand.getId(), date);
					int size = contentPanelType.getCalculatedScale();
					spacePanel.setPreferredSize(new Dimension(size, size));
					// This wrapper panel is needed. Without it, the
					// space panel takes the whole space, not just one
					// single row.
					JPanel wrapper = new JPanel(new MigLayout("insets 0"));
					wrapper.setOpaque(false);
					wrapper.add(spacePanel);
					add(wrapper);
				} else {
					// show items
					MigLayout layout2 = new MigLayout(
							"insets 0,wrap,fillx",
							"[]",
							"[top]");
					JPanel colPanel = new JPanel(layout2);
					colPanel.setName("column_panel");
					colPanel.setOpaque(false);
					for (Scene scene : sceneList) {
						ChronoScenePanel csp = new ChronoScenePanel(
								scene, strand.getColor());
						colPanel.add(csp, "growx");
					}
					add(colPanel);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Date getDate() {
		return date;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (getDate() == null) {
			return;
		}
		
		if (PCSDispatcher.isPropertyFired(Property.SCENE, evt)) {
			pcScene(evt);
			return;
		}
	}

	private void pcScene(PropertyChangeEvent evt) {
		Scene oldScene = (Scene) evt.getOldValue();
		Scene newScene = (Scene) evt.getNewValue();
		if (PCSDispatcher.isPropertyEdited(evt)
				&& oldScene.getDate().compareTo(newScene.getDate()) != 0) {
			// date has changed, handled by parent container
			return;
		}
		if (ScenePeer.doCountByDate(getDate()) == 0) {
			// empty row, handled by parent container
			return;
		}
		if (PCSDispatcher.isPropertyNew(evt)) {
			int count = ScenePeer.doCountByDate(newScene.getDate());
			if (count == 1) {
				// new scene with new date, handled by parent container		
				return;
			}
		}
		refresh(oldScene);
		refresh(newScene);
	}
	
	private void refresh(Scene scene) {
		if (scene == null) {
			return;
		}
		if (getDate().compareTo(scene.getDate()) == 0) {
			removeAll();
			initGUI();
			revalidate();
			repaint();
		}
	}

	public boolean isFooter() {
		return footer;
	}

	public int getRow() {
		return row;
	}

	public static String createComponentName(Date date) {
		return COMP_NAME + "_" + date;
	}
}
