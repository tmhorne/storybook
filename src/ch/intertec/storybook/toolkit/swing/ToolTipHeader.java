package ch.intertec.storybook.toolkit.swing;

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class ToolTipHeader extends JTableHeader {
	String[] toolTips;

	public ToolTipHeader(TableColumnModel model) {
		super(model);
	}

	public String getToolTipText(MouseEvent e) {
		try {
			int col = columnAtPoint(e.getPoint());
			int modelCol = getTable().convertColumnIndexToModel(col);
			String retStr = toolTips[modelCol];
			if (retStr == null || retStr.length() < 1) {
				return super.getToolTipText(e);
			}
			return retStr;
		} catch (Exception e1) {
			// ignore
		}
		return "";
	}

	public void setToolTipStrings(String[] toolTips) {
		this.toolTips = toolTips;
	}
}
