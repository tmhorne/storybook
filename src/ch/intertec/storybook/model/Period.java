package ch.intertec.storybook.model;

import java.sql.Date;

import org.apache.commons.lang3.time.FastDateFormat;

import ch.intertec.storybook.toolkit.I18N;

public class Period {
	private Date startDate;
	private Date endDate;

	public Period(Date start, Date end) {
		this.startDate = start;
		this.endDate = end;
	}

	public boolean isOverlapping(Period p) {
		return this.getStartDate().compareTo(p.getEndDate()) < 0
				&& this.getEndDate().compareTo(p.getStartDate()) > 0;
	}
	
	public boolean isInside(Date date) {
		if (date.compareTo(startDate) == 0) {
			return true;
		}
		if (date.compareTo(endDate) == 0) {
			return true;
		}
		return date.after(startDate) && date.before(endDate);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Period)) {
			return false;
		}
		Period p = (Period) o;
		return this.getStartDate().compareTo(p.getStartDate()) == 0
				&& this.getEndDate().compareTo(p.getEndDate()) == 0;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getStartDate().hashCode();
		hash = hash * 31 + getEndDate().hashCode();
		return hash;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public boolean isValid() {
		if (startDate == null || endDate == null) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (!isValid()) {
			return I18N.getMsg("msg.common.invalid.period");
		}
		String startStr = FastDateFormat.getDateInstance(FastDateFormat.LONG)
				.format(startDate);
		if (startDate.equals(endDate)) {
			return startStr;
		}
		String endStr = FastDateFormat.getDateInstance(FastDateFormat.LONG)
				.format(endDate);
		return startStr + " - " + endStr;
	}
}
