package ch.intertec.storybook.view.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.verifier.IntegerVerifier;

@SuppressWarnings("serial")
public class GenerateChaptersDialog extends JDialog {

	private List<JComponent> inputComponentList = new ArrayList<JComponent>();;

	// actions
	private AbstractAction cancelAction;
	private AbstractAction okAction;

	private JTextField tfNumber;
	
	public GenerateChaptersDialog() {
		super();
		initGUI();
	}
	
	public GenerateChaptersDialog(JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout(
				"wrap",
				"[grow]",
				"[][grow][][][]");
		setLayout(layout);
		setPreferredSize(new Dimension(350, 170));
		setTitle(I18N.getMsg("msg.generate.chapters"));

		// error label
		JLabel lbError = new JLabel();
		
		// number
		JLabel lbNumber = new JLabel(I18N.getMsg("msg.generate.chapters.text"));
		IntegerVerifier intVerifier = new IntegerVerifier(true);
		intVerifier.setErrorLabel(lbError);
		tfNumber = new JTextField();
		tfNumber.setInputVerifier(intVerifier);
		tfNumber.setColumns(10);

		// OK button
		JButton btOk = new JButton();
		btOk.setAction(getOkAction());
		btOk.setText(I18N.getMsg("msg.common.ok"));
		SwingTools.addEnterAction(btOk, getOkAction());
		
		// close button
		JButton btCancel = new JButton();
		btCancel.setAction(getCancelAction());
		btCancel.setText(I18N.getMsg("msg.common.cancel"));
		btCancel.setVerifyInputWhenFocusTarget(false);
		SwingTools.addEscAction(btCancel, getCancelAction());
		
		// fill in input list
		inputComponentList.add(tfNumber);
		
		// layout
		add(lbNumber);
		add(tfNumber);
		add(lbError, "growx");
		add(btOk, "split 2,sg,gap push");
		add(btCancel, "sg");
	}
	
	private AbstractAction getCancelAction() {
		if (cancelAction == null) {
			cancelAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					getThis().dispose();
				}
			};
		}
		return cancelAction;
	}

	private AbstractAction getOkAction() {
		if (okAction == null) {
			okAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					if (!SwingTools.checkInputComponents(inputComponentList)) {
						return;
					}
					int number = Integer.parseInt(tfNumber.getText());
					if (number > 0 && number <= 100) {
						ChapterPeer.generateChapters(number);
					} else {
						JOptionPane.showMessageDialog(
								getThis(),
								"Can't make 0 or more than 100 chapters",
								"Wrong Number", JOptionPane.WARNING_MESSAGE);
						return;
					}
					getThis().dispose();
					PCSDispatcher.getInstance().firePropertyChange(
							Property.CHAPTER, null, null);
				}
			};
		}
		return okAction;
	}

	private JDialog getThis() {
		return this;
	}
	
}
