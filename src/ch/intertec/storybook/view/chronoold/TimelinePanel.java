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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.util.Set;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.PersistenceManager;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.DateTools;
import ch.intertec.storybook.toolkit.DbTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.content.chrono.cell.TimelineCellPanel;

@SuppressWarnings("serial")
public class TimelinePanel extends JPanel implements IRefreshable,
		PropertyChangeListener {

	public TimelinePanel() {
		super();
		initGUI();
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(PCSDispatcher.Property.SCENE.toString(),
				this);
	}

	private void initGUI() {
		setLayout(new MigLayout("wrap,insets 0"));
		setBorder(SwingTools.getEtchedBorder());

		if (!PersistenceManager.getInstance().isConnectionOpen()) {
			return;
		}
		int row = 0;
		Set<Date> dates = ScenePeer.doSelectDistinctDate();
		DateTools.expandDatesToFuture(dates);
		if (dates.isEmpty()) {
			dates.add(DbTools.getNowAsSqlDate());
		}
		Date previousDate = null;
		for (Date date : dates) {
			if (previousDate == null) {
				// first date of this part
				int previousPartId = PartPeer.getIdOfPreviousPart(MainFrame
						.getInstance().getActivePartId());
				if (previousPartId != -1) {
					// if it is not the first part
					// it means that there is a "last date in the previous part"
					previousDate = PartPeer.getMaxDateForPart(previousPartId);
				}
			}
			TimelineCellPanel tlcPanel = new TimelineCellPanel(date, row,
					(row == (dates.size() - 2)), previousDate);
			// -2 => there is another date put without any scene after the last
			// date with scenes
			add(tlcPanel, "");
			previousDate = date;
			++row;
		}
	}

	@Override
	public void refresh() {
		removeAll();
		initGUI();
		validate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}
}
