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

package ch.intertec.storybook.view.content.chrono.cell;

import java.awt.Dimension;
import java.sql.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.time.FastDateFormat;

import net.miginfocom.swing.MigLayout;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.DateTools;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.chronoold.ChronoContentPanelOld;

import com.lowagie.text.Font;

@SuppressWarnings("serial")
public class TimelineCellPanel extends JPanel {
	private Date date;
	private Date previousDate;
	private int row;
	
	public TimelineCellPanel(){
		this(null, 0, true, null);
	}
	
	public TimelineCellPanel(Date date, int row, boolean isLastRow, Date previousDate) {
		super();
		this.date = date;
		this.row = row;
		this.previousDate = previousDate;
		initGUI(isLastRow);
	}

	private void initGUI(boolean isLastRow) {
		MigLayout layout = new MigLayout(
				"wrap",
				"[grow]",
				"[][]");
		setLayout(layout);
		setOpaque(true);
		setBackground(ChronoContentPanelOld.getRowColor(row, true));

		int count = ScenePeer.getMaxScenesByDate(date);
		if (count <= 0) {
			count = 1;
		}
		
		for (int i = 0; i < count; ++i) {
			JPanel panel = new JPanel(new MigLayout("wrap,insets 0"));
			int height = MainFrame.getInstance().getContentPanelType()
					.getCalculatedScale();

			Boolean showDateDiff = PrefManager.getInstance().getBooleanValue(
					Constants.Preference.SHOW_DATE_DIFFERENCE);
			if (showDateDiff == null) {
				showDateDiff = false;
			}

			if (!showDateDiff) {
				if (count == 1) {
					height -= 4;
				}
				if (row == 0) {
					// shrink height for the first panel to
					// level out the border
					height -= 2;
				}
			} else {
				if (i == 0) {
					if (row == 0) {
						// shrink height for the first panel to
						// level out the border
						// but add those 30 pixels from the date difference
						// panel
						// ==> adding 30 - 2
						// except for the first part
						if (PartPeer.getIdOfPreviousPart(MainFrame
								.getInstance().getActivePartId()) != -1) {
							height += 28;
						} else {
							height -= 2;
						}
					} else {
						// we add 30 to take into account the height of the date
						// difference panel
						// but only one time because there is only one date
						// separator
						height += 30;
					}
				}
			}
			
			panel.setOpaque(false);
			panel.setPreferredSize(new Dimension(75, height));
			
			JLabel dateDifferenceLabel = null;
			if (showDateDiff) {
				if (previousDate != null) {
					dateDifferenceLabel = new JLabel();
					// let's show the difference between those two dates
					long difference = date.getTime() - previousDate.getTime();
					dateDifferenceLabel.setText(DateTools
							.convertDifferenceToString(difference));
					dateDifferenceLabel.setPreferredSize(new Dimension(75, 30));
					dateDifferenceLabel.setFont(dateDifferenceLabel.getFont()
							.deriveFont(Font.ITALIC));
				}
			}

			JLabel lbDate;
			if (this.date == null) {
				lbDate = new JLabel("");
			} else {
				lbDate = new JLabel(FastDateFormat.getDateInstance(
						FastDateFormat.MEDIUM).format(date));
			}
			JLabel lbWeekday = new JLabel();
			lbWeekday.setText(SwingTools.getDayName(date));

			if (showDateDiff && dateDifferenceLabel != null) {
				panel.add(dateDifferenceLabel);
			}
			panel.add(lbDate);
			panel.add(lbWeekday);
			add(panel);
		}
	}

	public Date getDate() {
		return date;
	}
}
