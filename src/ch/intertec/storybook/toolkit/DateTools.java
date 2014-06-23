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

package ch.intertec.storybook.toolkit;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;

public class DateTools {

	public static String calendarToString(Calendar cal) {
		return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	}
	
	public static int calculateDaysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}
	
	public static void expandDates(Set<Date> dates) {
		expandDates(dates, 1, 1);
	}

	public static void expandDates(Set<Date> dates, int count) {
		expandDates(dates, count, count);
	}

	public static void expandDates(Set<Date> dates, int countPast,
			int countFuture) {
		expandDatesToPast(dates, countPast);
		expandDatesToFuture(dates, countFuture);
	}

	public static void expandDatesToFuture(Set<Date> dates) {
		expandDatesToFuture(dates, 1);
	}

	public static void expandDatesToFuture(Set<Date> dates, int count) {
		if (dates.isEmpty()) {
			return;
		}
		for (int i = 0; i < count; ++i) {
			Date lastDate = Collections.max(dates);
			lastDate = new Date(DateUtils.addDays(lastDate, 1).getTime());
			dates.add(lastDate);
		}
	}

	public static void expandDatesToPast(Set<Date> dates) {
		expandDatesToPast(dates, 1);
	}

	public static void expandDatesToPast(Set<Date> dates, int count) {
		if (dates.isEmpty()) {
			return;
		}		
		for (int i = 0; i < count; ++i) {
			Date firstDate = Collections.min(dates);
			firstDate = new Date(DateUtils.addDays(firstDate, -1).getTime());
			dates.add(firstDate);
		}
	}
	
    public static String convertDifferenceToString(long difference) {
        String retour = "+";
        //convert as seconds
        difference = difference / 1000;
        long seconds = difference % 60;
        long minutes = (difference % 3600) / 60;
        long hours = difference / 3600;
        long days = hours / 24;
        hours = hours - days * 24;

        if (days != 0) {
            retour += days + " "+I18N.getMsg("msg.days")+" ";
        }
        if (hours != 0) {
            retour += hours + " "+I18N.getMsg("msg.hours")+" ";
        }
        if (minutes != 0) {
            retour += minutes + " "+I18N.getMsg("msg.minutes")+" ";
        }
        if (seconds != 0) {
            retour += seconds + " "+I18N.getMsg("msg.seconds")+" ";
        }
        return retour;
    }
}
