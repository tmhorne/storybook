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

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.DateTools;
import ch.intertec.storybook.toolkit.DbTools;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.ViewTools;
import ch.intertec.storybook.view.content.AbstractContentPanel;
import ch.intertec.storybook.view.content.chrono.cell.ChronoScenePanel;
import ch.intertec.storybook.view.content.chrono.cell.SpacePanel;

@SuppressWarnings("serial")
@Deprecated
public class ChronoContentPanelOld extends AbstractContentPanel implements
        PropertyChangeListener {

    public static final String COMP_NAME = "chrono_content_panel";
    private TimelinePanel timelinePanel;

    public ChronoContentPanelOld() {
        super();
        PCSDispatcher pcs = PCSDispatcher.getInstance();
        pcs.addPropertyChangeListener(
                PCSDispatcher.Property.SCENE, this);
        pcs.addPropertyChangeListener(
                PCSDispatcher.Property.STRAND_ORDER, this);
    }

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}
    
    @Override
    public void initGUI() {
        MigLayout layout = new MigLayout("wrap,insets 0,fillx");
        setLayout(layout);

        setBackground(SwingTools.getBackgroundColor());
        setComponentPopupMenu(createPopupMenu());
        refreshPanel();

        JScrollPane scroller = MainFrame.getInstance().getScroller();

        // head row panel
        HeaderRowPanel hrPanel = new HeaderRowPanel();
        scroller.setColumnHeaderView(hrPanel);

        // time line panel
        timelinePanel = new TimelinePanel();
        scroller.setRowHeaderView(timelinePanel);
        scroller.getVerticalScrollBar().setUnitIncrement(20);
        scroller.getHorizontalScrollBar().setUnitIncrement(20);

        // custom corner
        JPanel lockPanel = new LockContentPanel();
        lockPanel.addPropertyChangeListener(hrPanel);
        scroller.setCorner(JScrollPane.UPPER_LEFT_CORNER, lockPanel);
    }

    private void refreshPanel() {
        // remove listeners
        PCSDispatcher pcs = PCSDispatcher.getInstance();
        pcs.removeListenersByClass(DateRowPanel.class);
        pcs.removeListenersByClass(ChronoScenePanel.class);
        pcs.removeListenersByClass(SpacePanel.class);

        // add date row panel
        Set<Date> dates = ScenePeer.doSelectDistinctDate();
        DateTools.expandDatesToFuture(dates);
        if (dates.isEmpty()) {
            dates.add(DbTools.getNowAsSqlDate());
        }
        
		Boolean showDateDiff = PrefManager.getInstance().getBooleanValue(
				Constants.Preference.SHOW_DATE_DIFFERENCE);
		if (showDateDiff == null) {
			showDateDiff = false;
		}
		
		if (!showDateDiff) {
			int i = 0;
			for (Date date : dates) {
				DateRowPanel rowPanel;
				if (i == dates.size() - 1) {
					// footer panel
					rowPanel = new DateRowPanel(date, i, true);
				} else {
					rowPanel = new DateRowPanel(date, i);
				}
				add(rowPanel, "growx");
				++i;
			}
		} else {
			int i = 0;
			Date previousDate = null;
			for (Date date : dates) {
				DateRowPanel rowPanel;
				if (i == dates.size() - 1) {
					// footer panel
					rowPanel = new DateRowPanel(date, i, true);
				} else {
					if (previousDate == null) {
						// first date of this part
						int previousPartId = PartPeer
								.getIdOfPreviousPart(MainFrame.getInstance()
										.getActivePartId());
						if (previousPartId != -1) {
							// if it is not the first part
							// it means that there is a
							// "last date in the previous part"
							previousDate = PartPeer
									.getMaxDateForPart(previousPartId);
						}
					}
					if (previousDate != null) {
						add(new DateDifferencesPanel(date), "growx");
					}
					rowPanel = new DateRowPanel(date, i);
				}
				add(rowPanel, "growx");
				++i;
				previousDate = date;
			}
		}
        
        revalidate();
    }

    public static Color getRowColor(int row) {
        return getRowColor(row, false);
    }

    public static Color getRowColor(int row, boolean darker) {
        if (row % 2 == 0) {
            if (darker) {
                return ColorUtil.blend(Color.lightGray, Color.white, 0.2);
            }
            return ColorUtil.blend(Color.lightGray, Color.white, 0.1);
        } else {
            if (darker) {
                return ColorUtil.blend(Color.lightGray, Color.white, 0.4);
            }
            return ColorUtil.blend(Color.lightGray, Color.white, 0.3);
        }
    }

    public TimelinePanel getTimelinePanel() {
        return timelinePanel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PCSDispatcher.isPropertyFired(Property.SCENE, evt)) {
            pcScene(evt);
            return;
        }

        if (PCSDispatcher.isPropertyFired(Property.STRAND_ORDER, evt)) {
            pcStrand();
            return;
        }

        // everything else is handled by super class
        super.propertyChange(evt);
    }

    private void pcStrand() {
        removeAll();
        refreshPanel();
    }

    private void pcScene(PropertyChangeEvent evt) {
        Scene oldScene = (Scene) evt.getOldValue();
        Scene newScene = (Scene) evt.getNewValue();
        boolean validateParent = false;
        boolean dateChanged = false;
        boolean strandChanged = false;
        if (PCSDispatcher.isPropertyEdited(evt)) {
            if (oldScene.getDate().compareTo(newScene.getDate()) != 0) {
                // date changed, validation on parent container needed
                validateParent = true;
                dateChanged = true;
            }
            if (oldScene.getStrandId() != newScene.getStrandId()) {
                // strand changed, scrolling needed
                strandChanged = true;
            }
        } else {
            // check for empty rows -> parent validation needed
            for (Component comp : getComponents()) {
                if (comp instanceof DateRowPanel) {
                    DateRowPanel drp = (DateRowPanel) comp;
                    if (drp.isFooter()) {
                        // an empty footer panel is allowed
                        continue;
                    }
                    if (ScenePeer.doCountByDate(drp.getDate()) == 0) {
                        validateParent = true;
                        break;
                    }
                }
            }
        }

        // check for new scene with new date -> parent validation needed
        if (validateParent == false && PCSDispatcher.isPropertyNew(evt)) {
            int count = ScenePeer.doCountByDate(newScene.getDate());
            if (count == 1) {
                validateParent = true;
            }
        }

        if (validateParent) {
            JScrollPane scroller = MainFrame.getInstance().getScroller();
            Point p = scroller.getViewport().getViewPosition();
            PCSDispatcher.getInstance().firePropertyChange(
                    Property.VIEW, null, null);
            if (dateChanged) {
                // calling this only once doesn't work reliably
                ViewTools.scrollToScene(newScene);
                ViewTools.scrollToScene(newScene);
                return;
            } else {
                scroller.getViewport().setViewPosition(p);
                return;
            }
        }

        // scroll if strand has been changed
        if (strandChanged) {
            ViewTools.scrollToScene(newScene);
        }
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(new TableNewAction(new Scene()));
        menu.add(new TableNewAction(new Chapter()));
        menu.add(new Separator());
        menu.add(new TableNewAction(new SbCharacter()));
        menu.add(new TableNewAction(new Location()));
        menu.add(new TableNewAction(new Tag()));
        menu.add(new Separator());
        menu.add(new TableNewAction(new Strand()));
        menu.add(new TableNewAction(new Part()));
        return menu;
    }

	@Override
	protected void initOptionsPanel(JPanel optionsPanel) {
	}
}
