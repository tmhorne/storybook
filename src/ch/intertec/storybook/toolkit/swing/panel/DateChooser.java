package ch.intertec.storybook.toolkit.swing.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import org.apache.commons.lang3.time.DateUtils;

import net.miginfocom.swing.MigLayout;

import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.view.IconButton;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

@SuppressWarnings("serial")
public class DateChooser extends JPanel {
	
	private static final Color ERROR_COLOR = new Color(255, 0, 0);
	private static final int COMPONENT_INDEX_DATE_EDITOR = 1;
	
	JDateChooser jdateChooser;
	private boolean allowEmptyValue = false;
	private boolean showButtons = true;
	
	public DateChooser() {
		jdateChooser = new JDateChooser();
		initGUI();
	}

	public DateChooser(Date date) {
		this(date, true);
	}
	
	public DateChooser(Date date, boolean showButtons) {
		jdateChooser = new JDateChooser(date);
		this.showButtons = showButtons;
		initGUI();
	}
	
	private void initGUI() {
		MigLayout layout = new MigLayout(
				"flowx,insets 2,fill",
				"[]20[][]");
		setLayout(layout);
		
		getJDateChooser().setPreferredSize(new Dimension(180, 20));
		setPreferredSize(new Dimension(220, 20));
		add(jdateChooser, "grow");
		
		if (showButtons) {
			// previous date
			IconButton btPrevious = new IconButton(
					"icon.small.arrow.left", null,
					getPreviousDayAction());
			btPrevious.setSize20x20();

			// next date
			IconButton btNext = new IconButton(
					"icon.small.arrow.right", null,
					getNextDayAction());
			btNext.setSize20x20();
			add(btPrevious);
			add(btNext);
		}
	}

	private AbstractAction getNextDayAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					Date date = DateUtils.addDays(getDate(), 1);
					setDate(date);
				} catch (IllegalArgumentException e) {
					setDate(ScenePeer.getLastDate());
				}
			}
		};
	}

	private AbstractAction getPreviousDayAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					Date date = DateUtils.addDays(getDate(), -1);
					setDate(date);
				} catch (IllegalArgumentException e) {
					setDate(ScenePeer.getFirstDate());
				}
			}
		};
	}
	
	public JDateChooser getJDateChooser(){
		return jdateChooser;
	}
	
	public Date getDate() {
		return jdateChooser.getDate();
	}

	public void setDate(Date date) {
		jdateChooser.setDate(date);
	}

	public void setAllowEmptyValue(boolean allowEmptyValue) {
		this.allowEmptyValue = allowEmptyValue;
	}	
	
	public boolean getAllowEmptyValue() {
		return allowEmptyValue;
	}
	
	public boolean isShowButtons() {
		return showButtons;
	}

	public boolean hasError() {
		JTextFieldDateEditor tf = (JTextFieldDateEditor) getJDateChooser()
				.getComponent(COMPONENT_INDEX_DATE_EDITOR);
		if (tf.getForeground().getRGB() == ERROR_COLOR.getRGB()) {
			return true;
		}
		return false;
	}
	
	public boolean isEmpty() {
		JTextFieldDateEditor tf = (JTextFieldDateEditor) getJDateChooser()
				.getComponent(COMPONENT_INDEX_DATE_EDITOR);
		if (tf.getText().isEmpty()) {
			return true;
		}
		return false;
	}
	
	public JTextFieldDateEditor getTextFieldDateEditor() {
		return (JTextFieldDateEditor) getJDateChooser().getComponent(
				COMPONENT_INDEX_DATE_EDITOR);
	}
}
