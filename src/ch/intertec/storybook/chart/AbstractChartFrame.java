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

package ch.intertec.storybook.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ScreenImage;
import ch.intertec.storybook.toolkit.filefilter.PNGFileFilter;
import ch.intertec.storybook.toolkit.swing.ProGlassPane;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public abstract class AbstractChartFrame extends JFrame implements IChart,
		IRefreshable, WindowListener, PropertyChangeListener {

	protected JPanel panel;
	protected JPanel optionsPanel;
	private JPanel outerPanel;

	// actions
	AbstractAction closeAction;
	AbstractAction exportAction;
	AbstractAction refreshAction;

	public AbstractChartFrame() {
		internalInit();
		init();
		initGUI();
		internalInitGUI();
		addWindowListener(this);
	}

	abstract protected void init();

	abstract protected void initGUI();

	abstract protected void beforeRefresh();
	
	abstract protected void afterRefresh();
	
	private JFrame getThis() {
		return this;
	}

	@Override
	public void refresh() {
		SwingTools.setWaitCursor(this);
		beforeRefresh();
		getContentPane().removeAll();
		internalInit();
		initGUI();
		internalInitGUI(false);
		outerPanel.validate();
		outerPanel.repaint();
		panel.validate();
		panel.repaint();
		pack();
		afterRefresh();
		SwingTools.setDefaultCursor(this);
	}

	private void internalInit() {
		panel = new JPanel(new MigLayout("wrap,fill,insets 20"));
		panel.setOpaque(true);
		panel.setBackground(Color.white);
		optionsPanel = new JPanel(new MigLayout(""));
		optionsPanel.setOpaque(false);
		optionsPanel.setBorder(SwingTools.getBorderLightGray());
	}

	private void internalInitGUI() {
		internalInitGUI(true);
	}

	private void internalInitGUI(boolean setSize) {
		MigLayout layout = new MigLayout("", "[grow]", "[grow][]");
		setLayout(layout);

		setIconImage(I18N.getImageIcon("icon.small.chart").getImage());

		if (setSize) {
			Dimension dim = SwingTools.getMainFrameSize();
			Dimension prefDim = new Dimension(dim.width - 200, dim.height - 100);
			setPreferredSize(prefDim);
		} else {
			setPreferredSize(getSize());
		}

		// options panel
		panel.add(optionsPanel);

		// outer panel
		outerPanel = new JPanel(new MigLayout(
				"wrap 2,insets 20",
				"[grow]",
				"[][grow][]"));
		outerPanel.setOpaque(true);
		outerPanel.setBackground(Color.white);
		outerPanel.setBorder(SwingTools.getEtchedBorder());

		// title
		JLabel lbTitle = new JLabel(getTitle());

		// time stamp
		JLabel lbTimestamp = SwingTools.createTimestampLabel();

		// slogan
		JLabel lbSlogan = new JLabel(
				Constants.Application.SLOGAN_AND_URL.toString());

		// scroll pane
		JScrollPane scroller = new JScrollPane(outerPanel);
		scroller.getHorizontalScrollBar().setUnitIncrement(20);
		scroller.getVerticalScrollBar().setUnitIncrement(20);

		// export PNG button
		JButton btExportPNG = new JButton();
		btExportPNG.setAction(getExportPNGAction());
		btExportPNG.setText(I18N.getMsg("msg.common.export.png"));

		// refresh button
		JButton btRefresh = new JButton();
		btRefresh.setAction(getRefreshAction());
		btRefresh.setIcon(I18N.getIcon("icon.small.refresh"));
		btRefresh.setToolTipText(I18N.getMsg("msg.common.refresh"));

		// close button
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		SwingTools.addEscAction(btClose, getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		btClose.setIcon(I18N.getIcon("icon.small.close"));

		if (!Constants.Application.IS_PRO_VERSION.toBoolean()) {
			ProGlassPane glassPane = new ProGlassPane(this);
			setGlassPane(glassPane);
			glassPane.setVisible(true);
		}

		// layout
		outerPanel.add(lbTitle);
		outerPanel.add(lbTimestamp, "gap push");
		outerPanel.add(panel, "span,grow");
		outerPanel.add(lbSlogan, "span,right");
		add(scroller, "grow,wrap");
		add(btExportPNG, "split");
		add(btRefresh);
		add(btClose, "gap push");
	}

	public JPanel getOuterPanel() {
		return outerPanel;
	}

	protected AbstractAction getExportPNGAction() {
		if (exportAction == null) {
			exportAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					try {
						JFileChooser fc = new JFileChooser();
						fc.setFileFilter(new PNGFileFilter());
						int ret = fc.showOpenDialog(getThis());
						if (ret == JFileChooser.CANCEL_OPTION) {
							return;
						}
						File file = fc.getSelectedFile();
						if (!file.getName().endsWith(".png")) {
							file = new File(file.getPath() + ".png");
						}
						ScreenImage.createImage(outerPanel, file.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}
		return exportAction;
	}

	protected AbstractAction getRefreshAction() {
		if (refreshAction == null) {
			refreshAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					refresh();
				}
			};
		}
		return refreshAction;
	}

	protected AbstractAction getCloseAction() {
		if (closeAction == null) {
			closeAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					removeListeners();
					getThis().dispose();
				}
			};
		}
		return closeAction;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		removeListeners();
	}

	private void removeListeners() {
		PCSDispatcher.getInstance().removeAllPropertyChangeListener(this);
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}
}
