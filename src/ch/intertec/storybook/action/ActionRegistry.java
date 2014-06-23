package ch.intertec.storybook.action;

import java.util.ArrayList;

import javax.swing.AbstractAction;

import ch.intertec.storybook.action.ActionManager.SbAction;

public class ActionRegistry {

	private static ActionRegistry theInstance;
	private ArrayList<AbstractAction> actionList;

	private ActionRegistry() {
		actionList = new ArrayList<AbstractAction>(
				ActionManager.SbAction.values().length);
	}

	public static ActionRegistry getInstance() {
		if (theInstance == null) {
			theInstance = new ActionRegistry();
		}
		return theInstance;
	}

	public void addAction(SbAction action, AbstractAction abstractAction) {
		actionList.add(action.ordinal(), abstractAction);
	}

	public AbstractAction getAction(SbAction action) {
		return actionList.get(action.ordinal());
	}
	
	public void clear() {
		actionList.clear();
	}
}
