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

package ch.intertec.storybook.toolkit.swing.crchooser;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Popup;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class ColRowChooser extends JPanel implements MouseListener,
		MouseMotionListener, FocusListener {

	private int cols;
	private int rows;
	private int oldCols;
	private int oldRows;
	private Popup popup;
	private IColRowChooserService service;

	public ColRowChooser(IColRowChooserService service, int cols, int rows) {
		this(service, cols, rows, 0, 0);
	}
	
	public ColRowChooser(IColRowChooserService service, int cols, int rows,
			int oldCols, int oldRows) {
		super();
		this.service = service;
		this.cols = cols;
		this.rows = rows;
		this.oldCols = oldCols - 1;
		if (oldRows > 0) {
			this.oldRows = oldRows - 1;
		} else {
			this.oldRows = 1;
		}
		setEnabled(true);
		setFocusable(true);
		initGUI();
		addFocusListener(this);
		addMouseListener(this);
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("ins 4,gap 2");
		setLayout(layout);
		setBorder(SwingTools.getBorderDefault());
		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col) {
				CellLabel cell = new CellLabel(col, row);
				if (col <= oldCols && row <= oldRows) {
					cell.setBorder(SwingTools.getBorderDefault(2));
				}
				cell.addMouseListener(this);
				cell.addMouseMotionListener(this);
				String wrap = "";
				if (col == cols - 1) {
					wrap = "wrap";
				}
				add(cell, wrap);
			}
		}
		add(new JButton(getCancelAction()), "span,gapy 5,al center");
		SwingTools.addEscAction(this, getCancelAction());
		requestFocusInWindow();
	}

	public Popup getPopup() {
		return popup;
	}

	public void setPopup(Popup popup) {
		this.popup = popup;
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		popup.hide();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof CellLabel) {
			CellLabel cell = (CellLabel) e.getSource();
			requestFocusInWindow();
			popup.hide();
			service.setCol(cell.getColumn());
			service.setRow(cell.getRow());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		requestFocusInWindow();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// popup.hide();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Component comp = e.getComponent();
		if (!(comp instanceof CellLabel)) {
			return;
		}
		CellLabel cell = (CellLabel) comp;
		for (Component cmp : getComponents()) {
			if (!(cmp instanceof CellLabel)) {
				continue;
			}
			CellLabel lb = (CellLabel) cmp;
			int r = lb.getRow();
			int c = lb.getColumn();
			if (r <= cell.getRow() && c <= cell.getColumn()) {
				lb.setHighlighted(true);
			} else {
				lb.setHighlighted(false);
			}
		}
	}
	
	private ColRowChooser getThis() {
		return this;
	}

	private AbstractAction getCancelAction() {
		return new AbstractAction(I18N.getMsg("msg.common.cancel")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				getThis().getPopup().hide();
			}
		};
	}
}
