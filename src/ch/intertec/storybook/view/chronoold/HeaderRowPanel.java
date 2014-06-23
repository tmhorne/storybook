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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.label.PastelLabel;
import ch.intertec.storybook.view.IconButton;

@SuppressWarnings("serial")
public class HeaderRowPanel extends JPanel implements PropertyChangeListener {
	
	private boolean showSort = false;
	
	public HeaderRowPanel() {
		super();
		initGUI();
		PCSDispatcher.getInstance().addPropertyChangeListener(
				PCSDispatcher.Property.STRAND.toString(), this);
	}
	
	private void initGUI() {
		setBorder(SwingTools.getEtchedBorder());
		
		int width = MainFrame.getInstance().getContentPanelType()
				.getCalculatedScale();
		--width;
		MigLayout panelLayout = new MigLayout(
				"insets 0 n 0 n",
				"[" + width + ",fill]",
				"[20,fill,top]");
		setLayout(panelLayout);
		int i = 0;
		int count = StrandPeer.doCount();
		List<Strand> strands = StrandPeer.doSelectAll();
		for (Strand strand : strands) {
			StrandPanel strandPanel = new StrandPanel(strand, i, count);
			add(strandPanel);
			++i;
		}
	}

	private HeaderRowPanel getThis() {
		return this;
	}
	
	private AbstractAction getSortAction(Strand strand, boolean inc) {
		return new SortAction(strand, inc);
	}
	
	private class SortAction extends AbstractAction {
		private Strand strand;
		private boolean inc;

		public SortAction(Strand strand, boolean inc) {
			this.strand = strand;
			this.inc = inc;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			try {
				Strand strand2 = null;
				if (inc) {
					strand2 = StrandPeer.findLeftNeighbor(strand);
				} else {
					strand2 = StrandPeer.findRightNeighbor(strand);
				}
				if (strand2 == null) {
					return;
				}

				// swap strands
				int sort1 = strand.getSort();
				int sort2 = strand2.getSort();
				strand.setSort(sort2);
				strand.save();
				strand2.setSort(sort1);
				strand2.save();
				
				int i=0;
				int count = StrandPeer.doCount();
				for(Component comp: getThis().getComponents()){
					StrandPanel panel = (StrandPanel)comp;
					if(panel.getStrandId() == strand.getId()){
						remove(i);
						add(new StrandPanel(strand2, i, count), i);
					} else if(panel.getStrandId() == strand2.getId()){
						remove(i);
						add(new StrandPanel(strand, i, count), i);
					}
					++i;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			getThis().validate();
			PCSDispatcher.getInstance().firePropertyChange(
					Property.STRAND_ORDER, null, null);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!ProjectTools.isProjectOpen()) {
			return;
		}
		
		if (PCSDispatcher.isSource(evt)) {
			if (PCSDispatcher.isPropertyFired(Property.STRAND, evt)) {
				pcStrand(evt);
			}
			return;
		}
		
		if(evt.getSource() instanceof LockContentPanel){
			if(evt.getNewValue() == Boolean.FALSE){
				showSort = true;
			} else {
				showSort = false;
			}
			int count = StrandPeer.doCount();
			int i = 0;
			for (Component comp : getComponents()) {
				StrandPanel panel = (StrandPanel) comp;
				Strand strand = StrandPeer.doSelectById(panel.getStrandId());
				remove(i);
				add(new StrandPanel(strand, i, count), i);
				++i;
			}
			validate();
		}
	}

	private void pcStrand(PropertyChangeEvent evt) {
		Strand oldStrand = (Strand) evt.getOldValue();
		Strand newStrand = (Strand) evt.getNewValue();
		int count = StrandPeer.doCount();
		if (PCSDispatcher.isPropertyNew(oldStrand, newStrand)
				|| PCSDispatcher.isPropertyRemoved(oldStrand, newStrand)) {
			// handled on a parent container
			return;
		} else {
			int i = 0;
			for (Component comp : getComponents()) {
				StrandPanel panel = (StrandPanel) comp;
				if (PCSDispatcher.isPropertyRemoved(oldStrand, newStrand)) {
					if (panel.getStrandId() == oldStrand.getId()) {
						remove(i);
					}
				} else if (PCSDispatcher.isPropertyEdited(oldStrand,
						newStrand)) {
					if (panel.getStrandId() == newStrand.getId()) {
						remove(i);
						add(new StrandPanel(newStrand, i, count), i);
					}
				}
				++i;
			}
		}
		validate();
	}
	
	private class StrandPanel extends JPanel{
		private int strandId;
		
		public StrandPanel(Strand strand, int pos, int count){			
			if (strand == null) {
				return;
			}
			
			strandId = strand.getId();
			
			setLayout(new MigLayout("insets 0,fill"));
			
			IconButton btLeft = new IconButton(
					"icon.small.arrow.left", null,
					getSortAction(strand, true));
			btLeft.setSize20x20();

			IconButton btRight = new IconButton(
					"icon.small.arrow.right", null,
					getSortAction(strand, false));
			btRight.setSize20x20();

			PastelLabel lbStrand = new PastelLabel(
					strand.toString(), SwingConstants.CENTER,
					strand.getColor());
			if (showSort && pos > 0) {
				add(btLeft);
			}
			add(lbStrand, "grow");
			if (showSort && pos < count - 1) {
				add(btRight);
			}
		}
		
		public int getStrandId() {
			return strandId;
		}
	}
}
