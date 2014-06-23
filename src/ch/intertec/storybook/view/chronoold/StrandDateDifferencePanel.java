package ch.intertec.storybook.view.chronoold;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import ch.intertec.storybook.toolkit.DateTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;

import com.lowagie.text.Font;

public class StrandDateDifferencePanel extends JLabel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5939608389415419860L;

    public StrandDateDifferencePanel(long difference) {
        this.initGUI();
        this.setText(DateTools.convertDifferenceToString(difference));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        FontMetrics fm = getFontMetrics(this.getFont());
        Rectangle2D textsize = fm.getStringBounds(this.getText(), g);

        GradientPaint bgPaint = new GradientPaint(
                (int)textsize.getWidth()+5,
                13,
                Color.black,
                getWidth(),
                16,
                SwingTools.getBackgroundColor());
        g2.setPaint(bgPaint);
        g2.fillRect((int)textsize.getWidth()+5, 13, getWidth(), 3);
    }

    private void initGUI() {
        this.setPreferredSize(new Dimension(150, 30));
        this.setOpaque(true);
        this.setBackground(SwingTools.getBackgroundColor());
        this.setFont(this.getFont().deriveFont(Font.ITALIC));
    }

}
