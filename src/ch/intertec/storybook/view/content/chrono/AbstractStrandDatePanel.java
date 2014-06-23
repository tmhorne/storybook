package ch.intertec.storybook.view.content.chrono;

import java.sql.Date;

import javax.swing.JPanel;

import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public abstract class AbstractStrandDatePanel extends JPanel implements
		IRefreshable {

	protected Strand strand;
	protected Date date;
	
	abstract protected void initGUI();
	
	public AbstractStrandDatePanel(Strand strand, Date date) {
		this.strand = strand;
		this.date = date;
		initGUI();
	}

	@Override
	public void refresh() {
		removeAll();
		initGUI();
		revalidate();
		repaint();
	}
	
	public Strand getStrand() {
		return strand;
	}

	public Date getDate() {
		return date;
	}
}
