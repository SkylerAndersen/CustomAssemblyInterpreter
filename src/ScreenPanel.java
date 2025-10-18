import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

class ScreenPanel extends JPanel {
    int originX;
    int originY;

    public ScreenPanel () {
        super.setLayout(null);
        originX = 31;
        originY = 20;
    }

    @Override
    public void setLayout (LayoutManager manager) {
        System.out.println("Refused: layout manager cannot be specified for ScreenPanel");
    }

    @Override
    public Component add (Component component) {
        component.setBounds(originX,originY,component.getWidth(),component.getHeight());
        super.add(component);
        return component;
    }

    @Override
    protected void paintComponent (Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) (g.create());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        // define colors and shapes
        Color screenColor = new Color(206,206,206);
        Color plasticColor = new Color(51,51,51);
        RoundRectangle2D border = new RoundRectangle2D.Double(10,10,180,158,10,10);
        Rectangle2D screen = new Rectangle2D.Double(20,20,160,138);
        int center = 100;
        Rectangle2D stand = new Rectangle2D.Double(center-17,165,34,25);
        Line2D base = new Line2D.Double(30,190,170,190);

        // draw on shapes
        g2d.setPaint(plasticColor);
        g2d.fill(border);
        g2d.fill(stand);
        Stroke defaultStroke = g2d.getStroke();
        Stroke thickerStroke = new BasicStroke(5);
        g2d.setStroke(thickerStroke);
        g2d.draw(base);
        g2d.setStroke(defaultStroke);
        g2d.setPaint(screenColor);
        g2d.fill(screen);

        g2d.dispose();
    }

    public void update () {
        revalidate();
        repaint();
    }

    public int getScreenWidth () {
        return 138;
    }

    public int getScreenHeight () {
        return 138;
    }
}