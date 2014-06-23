/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008-2009 Martin Mustun

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

package ch.intertec.storybook.view.memoria;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.swing.ProGlassPane;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public class MemoriaFrame extends JFrame implements IRefreshable,
		PropertyChangeListener, WindowListener {

	public static final String MEMORIA_FRAME_NAME = "memoria_frame";

	private MemoriaPanel memoriaPanel;
	private Timer timer;

	public MemoriaFrame() {
		setName(MEMORIA_FRAME_NAME);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(this);
		addListeners();
		initGUI();
	}

	@Override
	public void refresh() {
		if (memoriaPanel == null) {
			return;
		}
		memoriaPanel.refresh();
	}

	public MemoriaPanel getMemoriaPanel() {
		return memoriaPanel;
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("wrap,fill", "[]", "[grow][]");
		setLayout(layout);
		setPreferredSize(getSizeFromPerferences());
		setTitle(I18N.getMsg("msg.menu.view.pov"));
		ImageIcon icon = (ImageIcon) I18N.getIcon("icon.medium.memoria");
		setIconImage(icon.getImage());

		memoriaPanel = new MemoriaPanel();

		// refresh button
		JButton btRefresh = new JButton();
		btRefresh.setAction(getRefreshAction());
		btRefresh.setText(I18N.getMsg("msg.common.refresh"));
		btRefresh.setIcon(I18N.getIcon("icon.small.refresh"));

		// export PNG button
		JButton btExportPNG = new JButton();
		btExportPNG.setAction(getExportPNGAction());
		btExportPNG.setText(I18N.getMsg("msg.common.export.png"));
		btExportPNG.setIcon(I18N.getIcon("icon.small.export"));

		// zoom in button
		JButton btZoomIn = new JButton();
		btZoomIn.setAction(getZoomInAction());
		btZoomIn.setText(I18N.getMsg("msg.graph.zoom.in"));
		btZoomIn.setIcon(I18N.getIcon("icon.small.zoom.in"));

		// zoom out button
		JButton btZoomOut = new JButton();
		btZoomOut.setAction(getZoomOutAction());
		btZoomOut.setText(I18N.getMsg("msg.graph.zoom.out"));
		btZoomOut.setIcon(I18N.getIcon("icon.small.zoom.out"));

		// close button
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		SwingTools.addEscAction(btClose, getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		btClose.setIcon(I18N.getIcon("icon.small.close"));

		if (!Constants.Application.IS_PRO_VERSION.toBoolean()) {
			ProGlassPane glassPane = new ProGlassPane(this, true);
			setGlassPane(glassPane);
			glassPane.setVisible(true);
			timer = new Timer(0, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					memoriaPanel.animate();
				}
			});
			timer.setDelay(2000);
			timer.start();

		}

		// layout
		add(memoriaPanel, "grow");
		add(btRefresh, "sg,split 5");
		add(btZoomIn, "sg");
		add(btZoomOut, "sg");
		add(btExportPNG, "sg");
		add(btClose, "sg,gap push");
	}

	private AbstractAction getCloseAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				removeListeners();
				getThis().dispose();
			}
		};
	}

	private AbstractAction getRefreshAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				refresh();
			}
		};
	}

	private AbstractAction getExportPNGAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				memoriaPanel.export();
			}
		};
	}

	private AbstractAction getZoomInAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				memoriaPanel.zoomIn();
			}
		};
	}

	private AbstractAction getZoomOutAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				memoriaPanel.zoomOut();
			}
		};
	}

	private MemoriaFrame getThis() {
		return this;
	}

	private void addListeners() {
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(Property.SCENE, this);
		pcs.addPropertyChangeListener(Property.PROJECT, this);
	}

	private void removeListeners() {
		PCSDispatcher.getInstance().removeAllPropertyChangeListener(this);
	}

	public static boolean checkIfAvailable() {
		TreeSet<Date> dateSet = ScenePeer.doSelectDistinctDate(false);
		if (dateSet.isEmpty()) {
			JOptionPane.showMessageDialog(MainFrame.getInstance(),
					I18N.getMsg("msg.memoria.error.text"),
					I18N.getMsg("msg.memoria.error.title"),
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}

	private Dimension getSizeFromPerferences() {
		try {
			PrefManager prefManager = PrefManager.getInstance();
			int h = prefManager
					.getIntegerValue(Constants.Preference.MEMORIA_HEIGHT);
			int w = prefManager
					.getIntegerValue(Constants.Preference.MEMORIA_WIDTH);
			return new Dimension(w, h);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return new Dimension(1100, 800);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.PROJECT, evt)) {
			if (PCSDispatcher.isPropertyRemoved(evt)) {
				dispose();
				return;
			}
		}
		refresh();
	}

	@Override
	public void windowActivated(WindowEvent e) {
		if (this.memoriaPanel.hasAutoRefresh()) {
			this.memoriaPanel.refresh();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		removeWindowListener(this);
		removeListeners();
		if (timer != null) {
			timer.stop();
		}

		// save frame size
		try {
			PrefManager prefManager = PrefManager.getInstance();
			prefManager.setValue(Constants.Preference.MEMORIA_HEIGHT,
					new Integer(getHeight()));
			prefManager.setValue(Constants.Preference.MEMORIA_WIDTH,
					new Integer(getWidth()));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}
