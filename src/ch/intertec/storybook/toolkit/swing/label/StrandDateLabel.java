package ch.intertec.storybook.toolkit.swing.label;

import java.sql.Date;

import ch.intertec.storybook.model.Strand;

@SuppressWarnings("serial")
public class StrandDateLabel extends DateLabel {

	Strand strand;

	public StrandDateLabel(Strand strand, Date date) {
		super(date);
		this.strand = strand;
		String text = getDateText();
		setText(text);
		setToolTipText("<html>" + text + "<br>" + strand);
	}

	public Strand getStrand() {
		return strand;
	}
}
