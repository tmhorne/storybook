package ch.intertec.storybook.view.memoria;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

@SuppressWarnings("serial")
public class DateSlider extends JSlider implements ComponentListener {

	private ArrayList<Date> dates;
	private Hashtable<Integer, JLabel> labelTable;
	private int startDateIndex;
	private int numberOfTickers;
	private int value = -1;

	public DateSlider(int orientation) {
		super(orientation);
		this.dates = new ArrayList<Date>();
		this.labelTable = new Hashtable<Integer, JLabel>();
		this.startDateIndex = 0;
		this.numberOfTickers = 1;
		addComponentListener(this);
	}

	public void setDates(ArrayList<Date> dates) {
		this.dates = dates;
		refresh();
	}

	public void setDate(Date date) {
		if (date == null) {
			return;
		}
		refresh();
		int index = getIndex(date);
		if (index >-1 && index <= getMaximum()) {
			setValue(index);
		} else {
			// find date
			index = -1;
			int i=0;
			for (Date d : dates) {
				if (d.compareTo(date) == 0) {
					index = i;
				}
				++i;
			}
			startDateIndex = index - numberOfTickers + 1;
			if (startDateIndex < 0) {
				startDateIndex = 0;
				value = index;
			} else {
				value = numberOfTickers;
			}
			refresh();
			value = -1;
		}
	}
	
	private int getIndex(Date date) {
		@SuppressWarnings("unchecked")
		Dictionary<Integer, JLabel> dict = getLabelTable();
		Enumeration<Integer> keys = dict.keys();
		int index = -1;
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			JLabel lb = dict.get(key);
			if (lb.getText().compareTo(date.toString()) == 0) {
				index = key;
				break;
			}
		}
		return index;
	}

	public Date getDate() {
		return dates.get(startDateIndex + getValue());
	}

	public boolean isIncrementAvailable() {
		return startDateIndex + 1 <= dates.size() - numberOfTickers;
	}

	public void inc() {
		if (isIncrementAvailable()) {
			++startDateIndex;
		}
	}

	public boolean isDecrementAvailable() {
		return startDateIndex - 1 >= 0;
	}

	public void dec() {
		if (isDecrementAvailable()) {
			--startDateIndex;
		}
	}

	public void refresh() {
		refresh(null);
	}

	public void refresh(Boolean inc) {
		int endDateIndex = startDateIndex + numberOfTickers;
		if (endDateIndex > dates.size()) {
			endDateIndex = dates.size();
			startDateIndex = endDateIndex - numberOfTickers;
		}
		if (startDateIndex < 0) {
			startDateIndex = 0;
		}
		int i = 0;
		for (Date d : dates.subList(startDateIndex, endDateIndex)) {
			labelTable.put(new Integer(i), new JLabel(d.toString()));
			++i;
		}
		setMinimum(0);
		setMaximum(i - 1);
		if (inc != null) {
			if (inc) {
				setValue(getValue() - 1);
			} else {
				setValue(getValue() + 1);
			}
		}
		if(value!=-1){
			setValue(value);
		}
		setLabelTable(labelTable);
		setPaintTrack(true);
		setMinorTickSpacing(1);
		setMajorTickSpacing(2);
		setPaintTicks(true);
		setPaintLabels(true);
		setSnapToTicks(true);
		repaint();
	}

	public int getNumberOfTickers() {
		return numberOfTickers;
	}

	public void setNumberOfTickers(int numberOfTickers) {
		this.numberOfTickers = numberOfTickers;
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		numberOfTickers = getWidth() / 100;
		setNumberOfTickers(numberOfTickers);
		Date date = getDate();
		setDate(date);
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}
}
