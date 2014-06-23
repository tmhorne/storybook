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
package ch.intertec.storybook.view.ideas;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ch.intertec.storybook.model.Idea;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public class IdeasFrame extends JFrame implements IRefreshable, 
	PropertyChangeListener, WindowListener, ChangeListener {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(IdeasFrame.class);
	
    /**
     * table
     */
    private IdeasJTable notStartedTable;
    /**
     * table
     */
    private IdeasJTable startedTable;
    /**
     * table
     */
    private IdeasJTable completedTable;
    /**
     * table
     */
    private IdeasJTable abandonnedTable;
    
    /**
     * tabbed pane
     */
    private JTabbedPane tabbedPane;

    public IdeasFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(this);
        addListeners();
        initGUI();
    }

    @Override
    public void refresh() {
        this.notStartedTable.refresh();
        this.startedTable.refresh();
        this.completedTable.refresh();
        this.abandonnedTable.refresh();
    }

    private void initGUI() {
        MigLayout layout = new MigLayout("wrap,fill",
				"[grow]",
				"[grow]10[]");
        setLayout(layout);
        setPreferredSize(new Dimension(800, 600));
        setTitle(I18N.getMsg("msg.ideas.title"));
        ImageIcon icon = (ImageIcon) I18N.getIcon("icon.sb");
        setIconImage(icon.getImage());

        // new button
        JButton btNew = new JButton();
        btNew.setAction(getAddAction());
        btNew.setText(I18N.getMsg("msg.common.new"));
        btNew.setIcon(I18N.getIcon("icon.small.new"));

        this.notStartedTable = new IdeasJTable(this, Idea.Status.NOT_STARTED);
        this.startedTable = new IdeasJTable(this, Idea.Status.STARTED);
        this.completedTable = new IdeasJTable(this, Idea.Status.COMPLETED);
        this.abandonnedTable = new IdeasJTable(this, Idea.Status.ABANDONED);
        this.tabbedPane = new JTabbedPane();
        JScrollPane scroller = new JScrollPane(notStartedTable);
        scroller.setBorder(SwingTools.getEtchedBorder());
        this.tabbedPane.addTab(Idea.Status.NOT_STARTED.toString(), scroller);
        this.tabbedPane.addChangeListener(this);

        scroller = new JScrollPane(startedTable);
        scroller.setBorder(SwingTools.getEtchedBorder());
        this.tabbedPane.addTab(Idea.Status.STARTED.toString(), scroller);

        scroller = new JScrollPane(completedTable);
        scroller.setBorder(SwingTools.getEtchedBorder());
        this.tabbedPane.addTab(Idea.Status.COMPLETED.toString(), scroller);

        scroller = new JScrollPane(abandonnedTable);
        scroller.setBorder(SwingTools.getEtchedBorder());
        this.tabbedPane.addTab(Idea.Status.ABANDONED.toString(), scroller);

        // close button
        JButton btClose = new JButton();
        btClose.setAction(getCloseAction());
        SwingTools.addEscAction(btClose, getCloseAction());
        btClose.setText(I18N.getMsg("msg.common.close"));
        btClose.setIcon(I18N.getIcon("icon.small.close"));

        // layout
        this.add(this.tabbedPane, "grow");
        this.add(btNew, "split 2,sg");
        this.add(btClose, "sg,gap push");
        this.refresh();
    }
    
    public JTabbedPane getTabbedPane() {
    	return this.tabbedPane;
    }

	private AbstractAction getAddAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				Idea.Status status = Idea.Status.NOT_STARTED;
				switch (getThis().tabbedPane.getSelectedIndex()) {
				case 0:
					status = Idea.Status.NOT_STARTED;
					break;
				case 1:
					status = Idea.Status.STARTED;
					break;
				case 2:
					status = Idea.Status.COMPLETED;
					break;
				case 3:
					status = Idea.Status.ABANDONED;
					break;
				default:
					break;
				}
				EditIdeaDialog dialog = new EditIdeaDialog(true, getThis(), -1,
						null, null, status, true);
				dialog.setVisible(true);
			}
		};
	}

    private AbstractAction getCloseAction() {
        return new AbstractAction() {

            public void actionPerformed(ActionEvent evt) {
                removeListeners();
                getThis().dispose();
            }
        };
    }

    private IdeasFrame getThis() {
        return this;
    }

    private void addListeners() {
        PCSDispatcher pcs = PCSDispatcher.getInstance();
        pcs.addPropertyChangeListener(Property.IDEAS, this);
        pcs.addPropertyChangeListener(Property.PROJECT, this);
    }

    private void removeListeners() {
        PCSDispatcher.getInstance().removeAllPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PCSDispatcher.isPropertyFired(Property.IDEAS, evt)) {
            refresh();
            return;
        }
        if (PCSDispatcher.isPropertyFired(Property.PROJECT, evt)) {
            if (PCSDispatcher.isPropertyRemoved(evt)) {
                dispose();
                return;
            }
            if (PCSDispatcher.isPropertyNew(evt)) {
                refresh();
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void windowActivated(WindowEvent e) {
    	getThis().refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void windowClosed(WindowEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void windowClosing(WindowEvent e) {
        removeListeners();
    }

    /**
     * {@inheritDoc}
     */
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void windowOpened(WindowEvent e) {
    }

	@Override
	public void stateChanged(ChangeEvent e) {
		getThis().refresh();
	}
}
