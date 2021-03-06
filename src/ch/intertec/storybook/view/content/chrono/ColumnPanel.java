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

package ch.intertec.storybook.view.content.chrono;

import java.sql.Date;
import java.util.List;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.toolkit.swing.label.StrandDateLabel;
import ch.intertec.storybook.view.content.chrono.cell.ChronoScenePanel;
import ch.intertec.storybook.view.content.chrono.cell.SpacePanel;

@SuppressWarnings("serial")
public class ColumnPanel extends AbstractStrandDatePanel {

	public static final String CN_DATE_COLUMN = "date_column";
	public static final String CN_DATE_PANEL = "date_panel";
	
	public ColumnPanel(Strand strand, Date date) {
		super(strand, date);
	}

	protected void initGUI() {
		try {
			MigLayout layout = new MigLayout(
					"insets 1",
					"[fill,grow]",
					"[top][fill,grow]"
			);
			setLayout(layout);
			setOpaque(false);
			setName(CN_DATE_COLUMN);

			// date
			StrandDateLabel lbDate = new StrandDateLabel(strand, date);
			add(lbDate, "wrap");

			// scenes by strand and date
			List<Scene> sceneList = ScenePeer.doSelectByStrandIdAndDate(
					strand.getId(), date);

			if (sceneList.isEmpty()) {
				SpacePanel spacePanel = new SpacePanel(strand.getId(), date);
				add(spacePanel, "grow");
			} else {
				MigLayout layout2 = new MigLayout(
						"insets 0",
						"[]",
						"[top]"
				);
				JPanel colPanel = new JPanel(layout2);
				colPanel.setName(CN_DATE_PANEL);
				colPanel.setOpaque(false);
				for (Scene scene : sceneList) {
					ChronoScenePanel csp = new ChronoScenePanel(scene,
							strand.getColor());
					colPanel.add(csp, "grow");
				}
				add(colPanel, "grow");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
