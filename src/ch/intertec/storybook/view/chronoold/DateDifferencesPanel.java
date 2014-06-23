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

@SuppressWarnings("serial")
public class DateDifferencesPanel extends JPanel implements PropertyChangeListener {

	private Date date;
	
    public DateDifferencesPanel(Date date) {
		this.date = date;
        this.initGUI();
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(
				PCSDispatcher.Property.SCENE, this);
    }

    private void initGUI() {
        ContentPanelType contentPanelType = MainFrame.getInstance().getContentPanelType();
		int width = contentPanelType.getCalculatedScale();	
		MigLayout layout = new MigLayout(
				"insets 0 8 0 0",
				"[" + width + ",fill]",
				"[30,top]");
		setLayout(layout);
		// setBackground(ChronoContentPanel.getRowColor(row));
		setBackground(SwingTools.getBackgroundColor());
				
		try {
			for (Strand strand : StrandPeer.doSelectAll()) {
				List<Scene> sceneList =
					ScenePeer.doSelectByStrandIdAndDate(strand.getId(), date);
				boolean addEmptyPanel = true;
				if (!sceneList.isEmpty()) {
					// show items
					//we retrieve the first one, they all have the same date
					Scene scene = sceneList.get(0);
					Date previousDate = ScenePeer.getPreviousChronologicalSceneDateInStrand(scene);
					if(previousDate != null) {
						long difference = scene.getDate().getTime() - previousDate.getTime();
						StrandDateDifferencePanel dateDiffPanel = new StrandDateDifferencePanel(difference);
						add(dateDiffPanel);
						addEmptyPanel = false;
					}
				}
				if(addEmptyPanel) {
					JPanel spacePanel = new JPanel();
					int size = contentPanelType.getCalculatedScale();
					spacePanel.setPreferredSize(new Dimension(size, 30));
					// This wrapper panel is needed. Without it, the
					// space panel takes whole space, not just one
					// single row.
					spacePanel.setBackground(SwingTools.getBackgroundColor());
					add(spacePanel);					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	

	public Date getDate() {
		return this.date;
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
}
