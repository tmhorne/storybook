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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.AbstractTableAction;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.IconButton;

@SuppressWarnings("serial")
public class SpacePanel extends JPanel implements PropertyChangeListener,
		IRefreshable, MouseListener {

	public static final String COMP_NAME = "space_panel";

	private IconButton btNewScene;
	private int strandId = -1;
	private Date date = null;
	private AbstractTableAction newSceneAction;

	public SpacePanel() {
		super();
		initGUI();
		PCSDispatcher.getInstance().addPropertyChangeListener(Property.STRAND,
				this);
	}

	public SpacePanel(int strandId, Date date) {
		super();
		this.strandId = strandId;
		this.date = date;
		initGUI();
		PCSDispatcher.getInstance().addPropertyChangeListener(Property.STRAND,
				this);
	}

	private void initGUI() {
		addMouseListener(this);
		setFocusable(true);
		setMinimumSize(new Dimension(150, 150));
		
		Color color = Color.gray;
		if (strandId != -1) {
			color = StrandPeer.doSelectById(strandId).getColor();
		}
		MigLayout layout = new MigLayout(
				"fill",
				"[center]",
				"[center]");
		setLayout(layout);
		setBorder(BorderFactory.createLineBorder(color, 2));
		setOpaque(false);
		if (strandId != -1 && date != null) {
			setName(createComponentName(strandId, date));
		}

		// add new scene action
		newSceneAction = new TableNewAction(new Scene(), true);
		if (strandId != -1) {
			Scene scene = new Scene();
			scene.setStrandId(strandId);
			newSceneAction.putValue(AbstractTableAction.ActionKey.SCENE.toString(),
					scene);
		}
		if (date != null) {
			newSceneAction.putValue(AbstractTableAction.ActionKey.DATE.toString(), date);
		}
		newSceneAction.putValue(AbstractTableAction.ActionKey.CONTAINER.toString(),
				this);
		btNewScene = new IconButton("icon.small.plus", newSceneAction);
		btNewScene.setNoBorder();
		btNewScene.setToolTipText(I18N.getMsg("msg.space.panel.add.new"));
		add(btNewScene, "ax center");
	}

	public static String createComponentName(int strandId, Date date) {
		return COMP_NAME + "_" + strandId + "_" + date;
	}

	protected SpacePanel getThis() {
		return this;
	}

	public int getTraceId() {
		return this.strandId;
	}

	@Override
	public void refresh() {
		removeAll();
		initGUI();
		validate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!PCSDispatcher.isPropertyEdited(evt.getOldValue(),
				evt.getNewValue())) {
			return;
		}
		Strand strand = (Strand) evt.getNewValue();
		if (strand.getId() == strandId) {
			refresh();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		newSceneAction.actionPerformed(null);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
