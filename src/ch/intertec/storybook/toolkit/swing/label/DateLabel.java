package ch.intertec.storybook.toolkit.swing.label;

import java.awt.Color;
import java.sql.Date;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.time.FastDateFormat;

import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class DateLabel extends JLabel {

	Date date;

	public DateLabel(Date date) {
		super();
		this.date = date;
		setText(getDateText());
		setToolTipText(getDateText());
		setIcon(I18N.getIcon("icon.small.chrono.view"));
		setBackground(new Color(240, 240, 240));
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	public String getDateText() {
		String dateStr = FastDateFormat.getDateInstance(FastDateFormat.MEDIUM)
				.format(date);
		String dayStr = SwingTools.getDayName(date);
		return dayStr + " - "+ dateStr;
	}
	
	public Date getDate() {
		return date;
	}
}
