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

package ch.intertec.storybook.view.ideas;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.Idea;
import ch.intertec.storybook.model.IdeasPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;

@SuppressWarnings("serial")
public class EditIdeaDialog extends JDialog implements WindowListener {
    /**
     * Text area used to edit the idea
     */
    private UndoableTextArea textArea;
    /**
     * category
     */
    private JTextField categoryTF;
    /**
     * creation mode flag
     */
    private boolean creationMode;

    /**
     * idea status
     */
    private JComboBox status;

    /**
     * idea's id
     */
    private int ideaId;
    
    private boolean add;
    
    /**
     * constructor
     * @param modal <code>true</code> to make it a modal dialog, <code>false</code> otherwise
     * @param parent owner dialog frame
     * @param idea idea text. <code>null</code> to create a new note
     * @param category
     * @param status
     */
    public EditIdeaDialog(boolean modal, IdeasFrame parent, int ideaId,
            String idea, String category, Idea.Status status, boolean add) {
        super(parent, modal);
        this.ideaId = ideaId;
        this.textArea = new UndoableTextArea();
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.categoryTF = new JTextField();
        this.add = add;
        Vector<Idea.Status> datas = new Vector<Idea.Status>();
        datas.add(Idea.Status.NOT_STARTED);
        datas.add(Idea.Status.STARTED);
        datas.add(Idea.Status.COMPLETED);
        datas.add(Idea.Status.ABANDONED);
        this.status = new JComboBox(datas);
        this.status.setSelectedItem(status);
        this.creationMode = (idea == null);
        if (!this.creationMode) {
            this.textArea.setText(idea);
            this.categoryTF.setText(category);
        }
        this.initGUI();
        this.addWindowListener(this);
    }

    private void initGUI() {
        MigLayout layout = new MigLayout("wrap,fill",
				"[][grow]",
				"[][][grow][]");
        setLayout(layout);
        setPreferredSize(new Dimension(600, 400));
        setMinimumSize(new Dimension(600, 300));
        setTitle(I18N.getMsg("msg.idea.edit.title"));

        JScrollPane scroller = new JScrollPane(this.textArea);
        scroller.setBorder(SwingTools.getEtchedBorder());

        // edit button
        JButton btEdit = new JButton();
        btEdit.setAction(getEditAction());
		if (this.add) {
			btEdit.setText(I18N.getMsg("msg.common.new"));
			btEdit.setIcon(I18N.getIcon("icon.small.add"));
		} else {
			btEdit.setText(I18N.getMsg("msg.common.edit"));
			btEdit.setIcon(I18N.getIcon("icon.small.edit"));
		}

        // close button
        JButton btClose = new JButton();
        btClose.setAction(getCloseAction());
        SwingTools.addEscAction(btClose, getCloseAction());
        btClose.setText(I18N.getMsg("msg.common.close"));
        btClose.setIcon(I18N.getIcon("icon.small.close"));

		// layout
		add(new JLabel(I18N.getMsgColon("msg.idea.table.status")));
		add(this.status, "growx");
		add(new JLabel(I18N.getMsgColon("msg.idea.table.category")));
		add(this.categoryTF, "growx");
		add(scroller, "grow, span 2");
		add(btEdit, "sg,span 2,split 2,align right");
		add(btClose, "sg");
		
		this.setLocationRelativeTo(getParent());
    }

	private AbstractAction getEditAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				IdeasPeer.insertIdea(getThis().ideaId,
						(Idea.Status) getThis().status.getSelectedItem(),
						getThis().textArea.getText(), getThis().categoryTF
								.getText());
				getThis().dispose();
			}
		};
	}

	private AbstractAction getCloseAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().dispose();
				getThis().getParent().requestFocus();
			}
		};
	}

    private EditIdeaDialog getThis() {
        return this;
    }

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
		this.categoryTF.requestFocus();
	}
}
