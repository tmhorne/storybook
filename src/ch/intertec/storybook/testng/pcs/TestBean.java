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

package ch.intertec.storybook.testng.pcs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;

public class TestBean implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(TestBean.class);

	private int counter = 0;
	private int counterProperty1 = 0;
	private int counterProperty2 = 0;

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		logger.info(evt);
		++counter;
		if (PCSDispatcherTest.PROPERTY1.equals(evt.getPropertyName())) {
			++counterProperty1;
		} else if (PCSDispatcherTest.PROPERTY2.equals(evt.getPropertyName())) {
			++counterProperty2;
		}

		logger.info("counter: " + counter);
		logger.info("counter property1: " + counterProperty1);
		logger.info("counter property2: " + counterProperty2);
	}

	public int getCounter() {
		return counter;
	}

	public int getCounterProperty1() {
		return counterProperty1;
	}

	public int getCounterProperty2() {
		return counterProperty2;
	}
}
