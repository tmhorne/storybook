package ch.intertec.storybook.view.ideas;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Idea;
import ch.intertec.storybook.model.Idea.Status;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class FoiDialog extends JDialog {

	private JTextArea taNote;

	public FoiDialog() {
		super(MainFrame.getInstance());
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("wrap", "", "");
		setLayout(layout);
		setTitle(I18N.getMsg("msg.foi.title"));

		JLabel lbEnterNew = new JLabel(I18N.getMsgColon("msg.foi.enter.new"));
		
		taNote = new JTextArea();
		taNote.setBorder(SwingTools.getEtchedBorder());
		taNote.setPreferredSize(new Dimension(300, 200));

		// OK button
		JButton btOk = new JButton();
		btOk.setAction(getOkAction());
		SwingTools.addEnterAction(btOk, getOkAction());
		btOk.setText(I18N.getMsg("msg.common.ok"));
		SwingTools.addCtrlEnterAction(btOk, getOkAction());

		// cancel button
		JButton btCancel = new JButton();
		btCancel.setAction(getCancelAction());
		SwingTools.addEscAction(btCancel, getCancelAction());
		btCancel.setText(I18N.getMsg("msg.common.cancel"));
		btCancel.setIcon(I18N.getIcon("icon.small.close"));
		SwingTools.addEscAction(btCancel, getCancelAction());

		add(lbEnterNew);
		add(taNote);
		add(btOk, "sg,span,split 2,gap push");
		add(btCancel, "sg");
	}

	private AbstractAction getOkAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					String note = taNote.getText();
					Idea idea = new Idea();
					idea.setStatus(Status.NOT_STARTED);
					idea.setNote(note);
					idea.setCategory(I18N.getMsg("msg.foi.title"));
					idea.save();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				getThis().dispose();
			}
		};
	}

	private AbstractAction getCancelAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().dispose();
			}
		};
	}

	private FoiDialog getThis() {
		return this;
	}
}
