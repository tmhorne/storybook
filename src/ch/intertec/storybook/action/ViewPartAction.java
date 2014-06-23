package ch.intertec.storybook.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class ViewPartAction extends AbstractAction {

	public static final String ACTION_KEY_PART_ID = "partid";
	
	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrame mainFrame = MainFrame.getInstance();
		Integer partId = (Integer) getValue(ACTION_KEY_PART_ID);
		SwingTools.setWaitCursor();
		mainFrame.setActivePartId(partId);
		SwingTools.setDefaultCursor();
	}
}
