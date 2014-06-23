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

package ch.intertec.storybook.playground.swing.pcs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.testng.database.AbstractTest;

@SuppressWarnings("serial")
public class PgPCSTest extends JFrame implements PropertyChangeListener {

	private JTextField tfStrandName;
	private JTextField tfPartName;
	private JList strandList;
	private JList partList;
	private JList sceneList;
	
	private Strand currentStrand;
	private Part currentPart;
	
	private AbstractTest test;
	private PCSDispatcher pcs;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new PgPCSTest();
			}
		});
	}

	public PgPCSTest() {
		super();
		
		pcs = PCSDispatcher.getInstance();
		
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		test = new AbstractTest(){};
		test.getPersistenceManager().init("demo");
		// test.getPersistenceManager().init("test");
		// test.getPersistenceManager().initDbModel();
		// book = new Book();
		initGUI();
		setVisible(true);
	}

	private void initGUI() {
		setLayout(new MigLayout("wrap 2"));

		JLabel lbStrandName = new JLabel("Strand name: ");
		tfStrandName = new JTextField(20);
		
		JLabel lbPartName = new JLabel("Part name: ");
		tfPartName = new JTextField(20);		
		
		// strand list
		strandList = new JList(new DefaultListModel());
		updateStrandList();

		// part list
		partList = new JList(new DefaultListModel());
		updatePartList();
		
		// scene list
		sceneList = new JList(new DefaultListModel());
		updateSceneList();
		
		// listeners
		pcs.addPropertyChangeListener(this);
		
		// buttons
		JButton btAddStrand = new JButton(getAddStrandAction());
		JButton btLoadStrand = new JButton(getLoadStrandAction());
		JButton btEditStrand = new JButton(getEditStrandAction());
		JButton btAddPart = new JButton(getAddPartAction());
		JButton btLoadPart = new JButton(getLoadPartAction());
		JButton btEditPart = new JButton(getEditPartAction());

		add(lbStrandName);
		add(tfStrandName);
		add(btAddStrand, "span,split 3");
		add(btLoadStrand);
		add(btEditStrand);
		
		add(lbPartName);
		add(tfPartName);
		add(btAddPart, "span, split 3");
		add(btLoadPart);
		add(btEditPart);
		
		add(strandList);
		add(partList);
		add(sceneList, "span");
	}
	
	private AbstractAction getAddStrandAction() {
		return new AbstractAction("Add Strand") {
			public void actionPerformed(ActionEvent evt) {
				Strand strand = new Strand();
				strand.setName(tfStrandName.getText());
				strand.setColor(Color.black);
				strand.setAbbreviation("STR");
				try {
					strand.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
				pcs.firePropertyChange(
						PCSDispatcher.Property.STRAND.toString(), null, strand);
			}
		};
	}
	
	private AbstractAction getLoadStrandAction() {
		return new AbstractAction("Load Strand") {
			public void actionPerformed(ActionEvent evt) {
				Strand strand = (Strand) strandList.getSelectedValue();
				tfStrandName.setText(strand.getName());
				currentStrand = strand;
			}
		};
	}
	
	private AbstractAction getEditStrandAction() {
		return new AbstractAction("Edit Strand") {
			public void actionPerformed(ActionEvent evt) {
				currentStrand.setName(tfStrandName.getText());
				try {
					currentStrand.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
				pcs.firePropertyChange(
						PCSDispatcher.Property.STRAND.toString(),
						null, currentStrand);				
			}
		};
	}
	
	private AbstractAction getAddPartAction() {
		return new AbstractAction("Add Part") {
			public void actionPerformed(ActionEvent evt) {
				Part part = new Part();
				part.setName(tfPartName.getText());
				part.setNumber(42);
				try {
					part.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
				pcs.firePropertyChange(
						PCSDispatcher.Property.STRAND.toString(),
						null, part);
			}
		};
	}

	private AbstractAction getLoadPartAction() {
		return new AbstractAction("Load Part") {
			public void actionPerformed(ActionEvent evt) {
				Part part = (Part) partList.getSelectedValue();
				tfPartName.setText(part.getName());
				currentPart = part;
			}
		};
	}
	
	private AbstractAction getEditPartAction() {
		return new AbstractAction("Edit Part") {
			public void actionPerformed(ActionEvent evt) {
				currentPart.setName(tfPartName.getText());
				try {
					currentPart.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
				pcs.firePropertyChange(
						PCSDispatcher.Property.STRAND.toString(),
						null, currentPart);				
			}
		};
	}

	@SuppressWarnings("unused")
	private JFrame getThis() {
		return this;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof PCSDispatcher) {
			if (PCSDispatcher.Property.PART.toString().equals(evt.getPropertyName())) {
				updatePartList();
			} else if(PCSDispatcher.Property.STRAND.toString().equals(evt.getPropertyName())){
				updateStrandList();
				Strand strand = (Strand)evt.getNewValue();
				if (strand == null) {
					updateSceneList();
				} else {
					updateSceneList(strand);
				}
			} else if(PCSDispatcher.Property.SCENE.toString().equals(evt.getPropertyName())){
				Scene scene = (Scene)evt.getNewValue();
				updateSceneList(scene);
			}
		}
	}
	
	private void updatePartList(){
		DefaultListModel model = (DefaultListModel) partList.getModel();
		model.clear();
		for (Part part : PartPeer.doSelectAll()) {
			model.addElement(part);
		}
	}
	
	private void updateStrandList(){
		DefaultListModel model = (DefaultListModel)strandList.getModel();
		model.clear();
		for(Strand strand: StrandPeer.doSelectAll()){
			model.addElement(strand);
		}
	}

	private void updateSceneList(Strand newStrand) {
		DefaultListModel model = (DefaultListModel) sceneList.getModel();
		int i = 0;
		for (Scene scene : ScenePeer.doSelectAll()) {
			if ( scene.getStrand().getId() == newStrand.getId()) {
				model.remove(i);
				String str = "" + scene + " [" + scene.getStrand() + "]";
				model.add(i, str);
			}
			++i;
		}
	}
	
	private void updateSceneList() {
		DefaultListModel model = (DefaultListModel) sceneList.getModel();
		model.clear();
		for (Scene scene : ScenePeer.doSelectAll()) {
			String str = "" + scene + " [" + scene.getStrand() + "]";
			model.addElement(str);
		}		
	}
	
	private void updateSceneList(Scene newScene) {
		DefaultListModel model = (DefaultListModel) sceneList.getModel();
		int i = 0;
		for (Scene scene : ScenePeer.doSelectAll()) {
			if (scene.getId() == newScene.getId()) {
				model.remove(i);
				String str = "" + scene + " [" + scene.getStrand() + "]";
				model.add(i, str);
			}
			++i;
		}
	}
}
