package ch.intertec.storybook.view.memoria;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;

import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.TableEditAction;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;

public class MemoriaGraphMouse extends AbstractPopupGraphMousePlugin implements
		MouseListener {

	private static Logger log = Logger.getLogger(MemoriaGraphMouse.class);
	
	public final static String ACTION_KEY_DB_OBECT = "DbObject";

	private MemoriaPanel parent;

	public MemoriaGraphMouse(MemoriaPanel parent) {
		this.parent = parent;
	}

	public MemoriaGraphMouse() {
		this(MouseEvent.BUTTON3_MASK);
	}

	public MemoriaGraphMouse(int modifiers) {
		super(modifiers);
	}

	/**
	 * If this event is over a Vertex, pop up a menu to allow the user to
	 * increase/decrease the voltage attribute of this Vertex
	 * 
	 * @param evt
	 */
	protected void handlePopup(MouseEvent evt) {
		@SuppressWarnings("unchecked")
		final VisualizationViewer<DbTable, Integer> vv = (VisualizationViewer<DbTable, Integer>) evt
				.getSource();
		Point2D p = evt.getPoint(); // vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());
		GraphElementAccessor<DbTable, Integer> pickSupport = vv
				.getPickSupport();
		if (pickSupport != null) {
			final DbTable dbObj = pickSupport.getVertex(vv.getGraphLayout(),
					p.getX(), p.getY());
			if (dbObj != null) {
				JPopupMenu popup = createPopupMenu(dbObj);
				popup.show(vv, evt.getX(), evt.getY());
			}

			// pop up for edges
			// else {
			// final Number edge = pickSupport.getEdge(vv.getGraphLayout(),
			// p.getX(), p.getY());
			// if (edge != null) {
			// JPopupMenu popup = new JPopupMenu();
			// popup.add(new AbstractAction(edge.toString()) {
			// public void actionPerformed(ActionEvent e) {
			// System.err.println("got " + edge);
			// }
			// });
			// popup.show(vv, e.getX(), e.getY());
			// }
			// }
		}
	}

	private JPopupMenu createPopupMenu(DbTable dbObj) {
		JPopupMenu menu = new JPopupMenu();

		Scene realScene = null;

		TableEditAction editAction = null;
		if (dbObj.isClone()) {
			// get real object
			if (dbObj instanceof Item) {
				Item realItem = ItemPeer.doSelectById(dbObj.getRealId());
				editAction = new TableEditAction(realItem);
			} else if (dbObj instanceof Tag) {
				Tag realTag = TagPeer.doSelectById(dbObj.getRealId());
				editAction = new TableEditAction(realTag);
			} else if (dbObj instanceof Scene) {
				realScene = ScenePeer.doSelectById(dbObj.getRealId());
				editAction = new TableEditAction(realScene);
			} else {
				log.fatal("Getting real object failed.");
			}
		} else {
			if (dbObj instanceof Scene) {
				realScene = (Scene) dbObj;
			}
			editAction = new TableEditAction(dbObj);
		}
		JFrame frame = SwingTools.findParentFrame(parent);
		editAction.setParentFrame(frame);
		menu.add(editAction);

		AbstractAction a = getChangePovObjectAction();
		a.putValue(ACTION_KEY_DB_OBECT, dbObj);
		JMenuItem miChange = new JMenuItem(a);
		miChange.setText(I18N.getMsg("msg.graph.focus.on.object"));
		miChange.setIcon(I18N.getIcon("icon.small.memoria"));
		menu.add(miChange);

		if (dbObj instanceof Scene) {
			menu.add(new JSeparator());

			ActionRegistry ar = ActionRegistry.getInstance();
			AbstractAction action = ar.getAction(SbAction.SHOW_IN_CHRONO_VIEW);
			action.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(),
					realScene);
			menu.add(action);
			action = ar.getAction(SbAction.SHOW_IN_MANAGE_VIEW);
			action.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(),
					realScene);
			menu.add(action);
			action = ar.getAction(SbAction.SHOW_IN_BOOK_VIEW);
			action.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(),
					realScene);
			menu.add(action);
		}

		return menu;
	}

	@Override
	public void mouseClicked(MouseEvent evt) {
		if (evt.getClickCount() == 2) {
			// double click
			@SuppressWarnings("unchecked")
			final VisualizationViewer<DbTable, Integer> vv = (VisualizationViewer<DbTable, Integer>) evt
					.getSource();
			Point2D p = evt.getPoint();
			// vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());
			GraphElementAccessor<DbTable, Integer> pickSupport = vv
					.getPickSupport();
			if (pickSupport != null) {
				final DbTable dbObj = pickSupport.getVertex(
						vv.getGraphLayout(), p.getX(), p.getY());
				if (dbObj != null) {
					parent.refresh(dbObj);
				}
			}
		}
		super.mouseClicked(evt);
	}

	public MemoriaPanel getParent() {
		return parent;
	}

	private MemoriaGraphMouse getThis() {
		return this;
	}

	@SuppressWarnings("serial")
	private AbstractAction getChangePovObjectAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				DbTable dbObj = (DbTable) getValue(ACTION_KEY_DB_OBECT);
				getThis().getParent().refresh(dbObj);
			}
		};
	}
}
