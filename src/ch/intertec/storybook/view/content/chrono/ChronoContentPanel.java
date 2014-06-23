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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.Internal;
import ch.intertec.storybook.model.InternalPeer;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.DateTools;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.label.VerticalLabelUI;
import ch.intertec.storybook.view.IconButton;
import ch.intertec.storybook.view.content.AbstractContentPanel;
import ch.intertec.storybook.view.content.chrono.cell.ChronoScenePanel;
import ch.intertec.storybook.view.content.chrono.cell.SpacePanel;

@SuppressWarnings("serial")
public class ChronoContentPanel extends AbstractContentPanel implements
		PropertyChangeListener, MouseWheelListener {

	public static final String CN_DATE_COLUMN = "date_column";
	public static final boolean ALIGNMENT_HORIZONTAL = true;
	public static final boolean ALIGNMENT_VERTICAL = false;
	
	private boolean alignment;
	
    public ChronoContentPanel() {
        super();
        PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(Property.SCENE, this);
		pcs.addPropertyChangeListener(Property.STRAND_ORDER, this);
		addMouseWheelListener(this);
    }

	@Override
	protected void init() {
		setLayoutAlignment(getSavedChronoViewAlignment());
	}
    
    @Override
    public void initGUI() {
		MigLayout layout = new MigLayout(
				"",
				"", // columns
				"[top]" // rows
		);
		setLayout(layout);
		setBackground(SwingTools.getBackgroundColor());
		setComponentPopupMenu(createPopupMenu());
        refreshPanel();
    }

    private void refreshPanel() {
        // remove listeners
        PCSDispatcher pcs = PCSDispatcher.getInstance();
        pcs.removeListenersByClass(ChronoScenePanel.class);
        pcs.removeListenersByClass(SpacePanel.class);

		Boolean showDateDiff = PrefManager.getInstance().getBooleanValue(
				Constants.Preference.SHOW_DATE_DIFFERENCE);
		if (showDateDiff == null) {
			showDateDiff = false;
		}

        JScrollPane scroller = MainFrame.getInstance().getScroller();
        // set scroll unit increments
        scroller.getVerticalScrollBar().setUnitIncrement(20);
        scroller.getHorizontalScrollBar().setUnitIncrement(20);
        // save values
		int vsb = scroller.getVerticalScrollBar().getValue();
		int hsb = scroller.getHorizontalScrollBar().getValue();
		
		removeAll();
		
		Set<Date> dates = ScenePeer.doSelectDistinctDate();
		DateTools.expandDatesToFuture(dates);
		List<Strand> strands = StrandPeer.doSelectAll();
		
		if(alignment == ALIGNMENT_VERTICAL){
			Date lastDate = null;
			for (Date date : dates) {
				int i = 0;
				if (showDateDiff && lastDate != null) {
					for (int j = 0; j < strands.size(); ++j) {
						DateDiffLabel lbDiff = new DateDiffLabel(lastDate, date);
						if (lbDiff.getDays() > 1) {
							String wrap = "";
							if (j == strands.size() - 1) {
								wrap = "wrap,";
							}
							add(lbDiff, wrap + "growx,al center");
						}
					}
				}
				for (Strand strand : strands) {
					JLabel lbStrand = new JLabel(" " + strand.toString() + " ");
					lbStrand.setMinimumSize(new Dimension(20, lbStrand
							.getHeight()));
					lbStrand.setBackground(ColorUtil.getPastel(strand
							.getColor()));
					lbStrand.setOpaque(true);
					String wrap = "";
					if (i == strands.size() - 1) {
						wrap = "wrap,";
					}
					add(lbStrand, wrap + "grow");
					++i;
				}
				i = 0;
				for (Strand strand : strands) {
					String wrap = "";
					if (i == strands.size() - 1) {
						wrap = "wrap";
					}
					RowPanel rowPanel = new RowPanel(strand, date);
					add(rowPanel, wrap+",grow");
					++i;
				}
				lastDate = date;
			}
		} else {
			// horizontal layout
			for (Strand strand : strands) {
				int i = 0;
				Date lastDate = null;
				for (Date date : dates) {
					if (showDateDiff && lastDate != null) {
						DateDiffLabel lbDiff = new DateDiffLabel(lastDate, date, true);
						lbDiff.setUI(new VerticalLabelUI(false));
						if (lbDiff.getDays() > 1) {
							add(lbDiff, "growy");
						}
					}
					
					JLabel lbStrand = new JLabel(" " + strand.toString() + " ");
					lbStrand.setMinimumSize(new Dimension(20, lbStrand.getHeight()));
					lbStrand.setUI(new VerticalLabelUI(false));
					lbStrand.setBackground(ColorUtil.getPastel(strand.getColor()));
					lbStrand.setOpaque(true);
					add(lbStrand, "grow");
					String wrap = "";
					if (i == dates.size() - 1) {
						wrap = ",wrap";
					}
					ColumnPanel colPanel = new ColumnPanel(strand, date);
					add(colPanel, "grow" + wrap);
					++i;
					
					lastDate = date;
				}
			}
    	}		
    	
		scroller.setViewportView(this);
		scroller.getVerticalScrollBar().setValue(vsb);
		scroller.getHorizontalScrollBar().setValue(hsb);		
    }
    
	private void setLayoutAlignment(boolean layoutAlignment) {
		this.alignment = layoutAlignment;
	}
    
	public boolean getLayoutAlignment() {
		return alignment;
	}
	
	public void changeLayoutAlignment() {
		setLayoutAlignment(!alignment);
		saveChronoViewAlignment();
		refreshPanel();
	}
	
	private boolean getSavedChronoViewAlignment() {
		try {
			Internal internal = InternalPeer
					.doSelectByKey(Constants.ProjectSetting.CHRONO_VIEW_ALIGNMENT);
			if (internal == null) {
				saveChronoViewAlignment();
				return ALIGNMENT_HORIZONTAL;
			}
			return internal.getBooleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ALIGNMENT_HORIZONTAL;
	}
    
	private void saveChronoViewAlignment() {
		try {
			Internal internal = InternalPeer
					.doSelectByKey(Constants.ProjectSetting.CHRONO_VIEW_ALIGNMENT);
			if (internal == null) {
				internal = new Internal();
				internal.setKey(Constants.ProjectSetting.CHRONO_VIEW_ALIGNMENT);
			}
			internal.setBooleanValue(alignment);
			internal.save();
		} catch (Exception e) {
			e.printStackTrace();
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
    public void propertyChange(PropertyChangeEvent evt) {
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		
		if (PCSDispatcher.isPropertyFired(Property.SCENE, evt)) {
			if (oldValue != null && newValue != null) {
				// scene edited
				Scene editedScene = (Scene) newValue;
				Strand oldStrand = ((Scene)oldValue).getStrand();
				Strand newStrand = ((Scene)newValue).getStrand();				
				if (oldStrand.getId() == newStrand.getId()) {
					// same strand
					Date oldDate = ((Scene) oldValue).getDate();
					Date newDate = ((Scene) newValue).getDate();
					if (oldDate.compareTo(newDate) == 0) {
						// same date
						refreshScene(editedScene);
						return;
					}
					// date changed
					refreshStrandDate(editedScene, oldDate);
					refreshStrandDate(editedScene, newDate);
					return;
				}

				// strand changed
				refreshStrands(editedScene, oldStrand, newStrand);
				return;
			}

			if (oldValue == null && newValue != null) {
				// new scene
				Scene newScene = (Scene) newValue;
				refreshStrandDate(newScene);
				return;
			}

			if (oldValue != null && newValue == null) {
				// scene deleted
				Scene deletedScene = (Scene) oldValue;
				refreshStrandDate(deletedScene);
				return;
			}
		}		
		
        if (PCSDispatcher.isPropertyFired(Property.STRAND_ORDER, evt)) {
        	// strand order changed
        	refreshPanel();
            return;
        }

        // everything else is handled by super class
        super.propertyChange(evt);
    }
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			int modifiers = e.getModifiers();
	        if ((modifiers & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
				scrollHorizontal(e.getScrollAmount(), e.getWheelRotation());
				// turn vertical scrolling into horizontal
				return;
			}
			scrollVertical(e.getScrollAmount(), e.getWheelRotation());
		}
	}

	private Action getChangeLayoutAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeLayoutAlignment();
			}
		};
	}

	@Override
	protected void initOptionsPanel(JPanel optionsPanel) {
		IconButton bt = new IconButton(getChangeLayoutAction());
		bt.setSize32x20();
		bt.setIcon("icon.small.switch");
		bt.setToolTipText(I18N.getMsg("msg.statusbar.change.layout.direction"));
		optionsPanel.add(bt);
	}
}
