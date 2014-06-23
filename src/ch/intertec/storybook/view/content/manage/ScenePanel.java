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

package ch.intertec.storybook.view.content.manage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.accessibility.Accessible;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.AbstractTableAction;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.TableDeleteAction;
import ch.intertec.storybook.action.TableEditAction;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.InternalPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public class ScenePanel extends JPanel implements MouseListener, Accessible,
		IRefreshable {
	public final static int TYPE_NONE = 0;
	public final static int TYPE_UNASSIGNED = 1;
	public final static int TYPE_MAKE_UNASSIGNED = 2;
	public final static int TYPE_BEGIN = 3;
	public final static int TYPE_NEXT = 4;

	private int type = TYPE_NONE;

	private Scene scene;
	
	private JLabel lbScene;

	public ScenePanel() {
		this(null);
	}

	public ScenePanel(Scene scene) {
		this(scene, TYPE_NONE);
	}

	public ScenePanel(Scene scene, int type) {
		super();
		this.scene = scene;
		this.type = type;
		setFocusable(true);
		addMouseListener(this);
		initGUI();
	}

	private void initGUI() {
		int scale = MainFrame.getInstance().getContentPanelType()
				.getCalculatedScale();

		setOpaque(true);

		if (type == TYPE_MAKE_UNASSIGNED) {
			setBackground(SwingTools.getNiceDarkGray());
			setMinimumSize(new Dimension(30, 25));
			return;
		}

		if (scene == null) {
			setBackground(SwingTools.getNiceDarkGray());
			setMinimumSize(new Dimension(30, 15));
			return;
		}

		MigLayout layout = new MigLayout(
				"wrap 2, insets 4",
				"",
				"");
		setLayout(layout);
		setBorder(SwingTools.getBorderDefault());
		Color clr = ColorUtil.getPastel2(scene.getStrand().getColor());
		setBackground(clr);

		JLabel lbColor = scene.getStatusLabel();

		lbScene = new JLabel();
		Dimension dim = lbScene.getPreferredSize();
		dim.setSize(80 + scale * 5, dim.getHeight());
		lbScene.setPreferredSize(dim);
		StringBuffer buf = new StringBuffer();
		if (type != TYPE_UNASSIGNED) {
			buf.append(scene.getLabelText());
			buf.append(": ");
		}
//		int scaleFactor = InternalPeer.getScaleFactorManage();
		int textLength = InternalPeer.getManageViewTextLength();
		if (scene.getTitle() == null || scene.getTitle().isEmpty()) {
			buf.append(scene.getText(true, textLength));
		} else {
			buf.append(scene.getTitle(true, textLength));
			// doesn't work, drag-n-drop runs into troubles
			// lbScene.setToolTipText(scene.getTitle());
		}
		lbScene.setText("<HTML>" + buf.toString() + "</HTML>");

		// layout
		add(lbColor, "top");
		add(lbScene);
	}

	@Override
	public void refresh() {
		removeAll();
		initGUI();
		validate();
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent evt) {
		if (scene == null) {
			return;
		}
		
		requestFocusInWindow();
		
		if (evt.getClickCount() == 2) {
			AbstractTableAction action = new TableEditAction(scene);
			action.putValue(
					AbstractTableAction.ActionKey.CONTAINER.toString(),
					getThis());
			action.actionPerformed(null);
			return;
		}
		
		if (SwingUtilities.isRightMouseButton(evt)) {
			JComponent comp = (JComponent) evt.getSource();
			Point p = SwingUtilities.convertPoint(comp, evt.getPoint(), this);
			JPopupMenu menu = createPopupMenu();
			menu.show(this, p.x, p.y);
		}
	}

	private JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		
		// edit
		AbstractTableAction action = new TableEditAction(scene);
		action.putValue(
				AbstractTableAction.ActionKey.CONTAINER.toString(),
				getThis());
		menu.add(action);
		
		// delete
		action = new TableDeleteAction(scene);
		action.putValue(
				AbstractTableAction.ActionKey.CONTAINER.toString(),
				getThis());
		menu.add(action);
		
		menu.add(new Separator());
		
		// new
		action = new TableNewAction(scene);
		action.putValue(
				AbstractTableAction.ActionKey.SCENE.toString(),
				scene);
		action.putValue(
				AbstractTableAction.ActionKey.DATE.toString(),
				scene.getDate());
		menu.add(action);
		
		menu.addSeparator();
		
		ActionRegistry ar = ActionRegistry.getInstance();
		AbstractAction act = ar.getAction(SbAction.SHOW_IN_CHRONO_VIEW);
		Scene sc = getScene();
		act.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(), sc);
		menu.add(act);
		act = ar.getAction(SbAction.SHOW_IN_BOOK_VIEW);
		act.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(), sc);
		menu.add(act);
		act = ar.getAction(SbAction.SHOW_IN_MEMORIA);
		act.putValue(Constants.ActionKey.MEMORIA_DBOBJ.toString(), sc);
		menu.add(act);
		
		return menu;
	}

	protected ScenePanel getThis() {
		return this;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (type == TYPE_NONE || type == TYPE_UNASSIGNED) {
			Color strandColor = scene.getStrand().getColor();
			if (ColorUtil.isDark(strandColor)) {
				lbScene.setForeground(Color.white);
			}
			setBackground(strandColor);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (type == TYPE_NONE || type == TYPE_UNASSIGNED) {
			Color clr = ColorUtil.getPastel2(scene.getStrand().getColor());
			setBackground(clr);
			lbScene.setForeground(Color.black);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public int getType() {
		return type;
	}
}
