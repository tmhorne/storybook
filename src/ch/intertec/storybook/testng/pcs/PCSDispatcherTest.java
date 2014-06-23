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

package ch.intertec.storybook.testng.pcs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.testng.database.AbstractTest;

public class PCSDispatcherTest extends AbstractTest implements
		PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(PCSDispatcherTest.class);

	public final static String PROPERTY1 = "property1";
	public final static String PROPERTY2 = "property2"; 
	
	private PCSDispatcher pcs;
	private TestBean testBean;
	
	public PCSDispatcherTest() {
		super();
		pcs = PCSDispatcher.getInstance();
		testBean = new TestBean();
	}

	@BeforeClass
	public void setUp() {
		PCSDispatcherTest test = new PCSDispatcherTest();
		test.getPersistenceManager().init("test");
		test.getPersistenceManager().initDbModel();
	}

	@Test
	public void testInit() throws Exception {
		logger.info("=== testInit()");

		List<Strand> strands = StrandPeer.doSelectAll();
		logger.info("strands: " + strands);
		Assert.assertEquals(strands.size(), 1);
		
		List<Part> parts = PartPeer.doSelectAll();
		logger.info("parts: " + parts);
		Assert.assertEquals(parts.size(), 1);
	}

	@Test(dependsOnMethods = { "testInit" })
	public void testAdd() throws Exception {
		pcs.addPropertyChangeListener(this);
		Assert.assertEquals(pcs.getPropertyChangeListeners().length, 1);
		
		pcs.addPropertyChangeListener(this);
		Assert.assertEquals(pcs.getPropertyChangeListeners().length, 1);

		pcs.addPropertyChangeListener(this);
		Assert.assertEquals(pcs.getPropertyChangeListeners().length, 1);

		pcs.addPropertyChangeListener(testBean);
		Assert.assertEquals(pcs.getPropertyChangeListeners().length, 2);

		pcs.addPropertyChangeListener(PROPERTY1, this);
		Assert.assertEquals(pcs.getPropertyChangeListeners(PROPERTY1).length, 1);
		
		pcs.addPropertyChangeListener(PROPERTY1, this);
		Assert.assertEquals(pcs.getPropertyChangeListeners(PROPERTY1).length, 1);
	}
	
	@Test(dependsOnMethods = { "testAdd" })
	public void testFire() throws Exception {
		pcs.firePropertyChange(PROPERTY1, false, true);
		Assert.assertEquals(testBean.getCounterProperty1(), 1);
		
		pcs.firePropertyChange(PROPERTY2, false, true);
		Assert.assertEquals(testBean.getCounterProperty2(), 1);
		
		pcs.firePropertyChange(PROPERTY1, false, true);
		Assert.assertEquals(testBean.getCounterProperty1(), 2);
	}
	
	@Test(dependsOnMethods = { "testFire" })
	public void testRemove() throws Exception {
		pcs.removePropertyChangeListener(PROPERTY2, testBean);
		Assert.assertEquals(pcs.getPropertyChangeListeners(PROPERTY2).length, 0);
		
		pcs.removePropertyChangeListener(PROPERTY1, this);
		Assert.assertEquals(pcs.getPropertyChangeListeners(PROPERTY1).length, 0);
		
		pcs.removePropertyChangeListener(this);
		pcs.removePropertyChangeListener(testBean);
	}
	
	@Test(dependsOnMethods = { "testRemove" })
	public void testAddMany() throws Exception {
		for (int i = 0; i < 20; ++i) {
			pcs.addPropertyChangeListener(new TestBean());
		}
		Assert.assertEquals(pcs.getPropertyChangeListeners().length, 20);
	}
	
	@Test(dependsOnMethods = { "testAddMany" })
	public void testRemoveByClass() throws Exception {
		pcs.removeListenersByClass(TestBean.class);
		Assert.assertEquals(pcs.getPropertyChangeListeners().length, 0);
	}

	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		logger.info(evt);
	}
}
