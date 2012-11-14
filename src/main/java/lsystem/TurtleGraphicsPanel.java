package lsystem;

import java.awt.Panel;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;

/*
lsystem.TurtleGraphicsPanel is the heart of rLogo's graphics. It uses simple
geometry to draw lines based on the current angle and position of the
turtle. Notice that all drawing commands are queued instead of being
immediately executed, and that drawing is first done to an
offScreenImage, to reduce screen flicker.
*/
public class TurtleGraphicsPanel extends Panel {

    private Image offScreenImage;
    private Graphics offScreenGraphics;
    private Dimension offScreenSize;

    private boolean repaint = true;

    private DrawQueue drawQueue = new DrawQueue();

    private float curX, curY;
    private boolean penIsDown = true;
    private boolean turtleIsShowing = false;
    private int theta;

    private Color backColor = Color.white;
    private Color curColor = Color.black;

    public final void setRepaint(boolean b) {
        repaint = b;
    }

    public TurtleGraphicsPanel() {
        showTurtle();
    }

    public Float getCurX() {
        return curX;
    }

    public Float getCurY() {
        return curY;
    }

    public void setCurX(Float x) {
        curX = x;
    }

    public void setCurY(Float y) {
        curY = y;
    }

    public final void penUp() {
        penIsDown = false;
    }

    public final void penDown() {
        penIsDown = true;
    }

    public final void showTurtle() {
        turtleIsShowing = true;
        if (repaint) repaint();
    }

    public final void hideTurtle() {
        turtleIsShowing = false;
        if (repaint) repaint();
    }

    public final int getTheta() {
        return theta;
    }

    public final void setColor(Color c) {
        curColor = c;
    }

    public final void setTheta(int angle) {
        theta = angle;
    }

    public final void turnRight(int angle) {
        setTheta(getTheta() + angle);
        if (repaint) repaint();
    }

    public final void turnLeft(int angle) {
        setTheta(getTheta() - angle);
        if (repaint) repaint();
    }

    public final void backward(int distance) {
        forward(-1 * distance);
    }

    public final void forward(int distance) {
        float newX = curX;
        float newY = curY;
        newX += distance * Math.cos(Math.PI * theta / 180.0);
        newY += distance * Math.sin(Math.PI * theta / 180.0);
        if (penIsDown) {
            drawQueue.line((int) Math.round(curX), (int) Math.round(curY), (int) Math.round(newX), (int) Math.round(newY), curColor);
        }
        curX = newX;
        curY = newY;
        if (repaint) repaint();
    }

    private final void drawTurtle(Graphics g) {

        int baseX1 = (int) (7 * Math.cos(Math.PI * (getTheta() + 90) / 180.0));
        int baseY1 = (int) (7 * Math.sin(Math.PI * (getTheta() + 90) / 180.0));
        int baseX2 = -1 * baseX1;
        int baseY2 = -1 * baseY1;

        int tipX = (int) (9 * Math.cos(Math.PI * getTheta() / 180.0));
        int tipY = (int) (9 * Math.sin(Math.PI * getTheta() / 180.0));

        g.setColor(Color.blue);
        g.translate((int) curX, (int) curY);
        g.drawLine(baseX1, baseY1, baseX2, baseY2);
        g.drawLine(baseX1, baseY1, tipX, tipY);
        g.drawLine(baseX2, baseY2, tipX, tipY);
    }

    public final void home(int x, int y) {
        setTheta(0);
        curX = x;
        curY = y;
        if (repaint) repaint();
    }

    public final void setBackColor(Color c) {
        backColor = c;
        drawQueue.setBackColor(backColor);
        if (repaint) repaint();
    }

    public final void clearScreen() {
        drawQueue.clearScreen(backColor);
        if (repaint) repaint();
    }

    public final void paint(Graphics g) {
        repaint(); //repaints the screen whenever another window obliterates it.
    }

    @Override
    public final synchronized void update(Graphics g) {
        Dimension d = size();
        if ((offScreenImage == null) || (d.width != offScreenSize.width) || (d.height != offScreenSize.height)) {
            offScreenImage = createImage(d.width, d.height);
            offScreenSize = d;
            offScreenGraphics = offScreenImage.getGraphics();
            offScreenGraphics.translate(d.width / 2, d.height / 2);
            drawQueue.redraw(offScreenGraphics, d);
        }
        drawQueue.draw(offScreenGraphics, d);
        g.drawImage(offScreenImage.getScaledInstance(d.width, d.height, Image.SCALE_AREA_AVERAGING + Image.SCALE_SMOOTH), 0, 0, null);

        if (turtleIsShowing) {
            g.translate(size().width / 2, size().height / 2);
            drawTurtle(g);
        }
    }
}

class DrawQueue extends Vector {

    private int nextCommand = 0;

    public final void draw(Graphics g, Dimension size) {
        DrawCommand d;
        while (nextCommand < size()) {
            if (nextCommand < 0) nextCommand = 0;
            d = (DrawCommand) elementAt(nextCommand);
            switch (d.getType()) {
                case DrawCommand.CLEAR_SCREEN:
                    for (int i = 0; i <= nextCommand; i++) {
                        removeElementAt(0);
                    }
                    d.draw(g, size);
                    nextCommand = 0;
                    break;
                case DrawCommand.SET_BACK_COLOR:
                    if (nextCommand > 0) {
                        DrawCommand firstCommand = (DrawCommand) elementAt(0);
                        removeElementAt(nextCommand);
                        if (firstCommand.getType() == DrawCommand.SET_BACK_COLOR) {
                            setElementAt(d, 0);
                        } else {
                            insertElementAt(d, 0);
                        }
                        nextCommand = 0;
                        break;
                    }
                default:
                    d.draw(g, size);
                    nextCommand++;
            }
        }
    }

    public final void redraw(Graphics g, Dimension size) {
        nextCommand = 0;
        draw(g, size);
    }

    public final void line(int x1, int y1, int x2, int y2, Color c) {
        DrawCommand d = new DrawCommand(DrawCommand.LINE);
        d.setStart(x1, y1);
        d.setStop(x2, y2);
        d.setColor(c);
        addElement(d);
    }

    public final void clearScreen(Color c) {
        DrawCommand d = new DrawCommand(DrawCommand.CLEAR_SCREEN);
        d.setColor(c);
        addElement(d);
    }

    public final void setBackColor(Color c) {
        DrawCommand d = new DrawCommand(DrawCommand.SET_BACK_COLOR);
        d.setColor(c);
        addElement(d);
    }

    public final void drawString(String s, Color c, int x, int y) {
        DrawCommand d = new DrawCommand(DrawCommand.DRAW_STRING);
        d.setColor(c);
        d.setStart(x, y);
        d.setString(s);
        addElement(d);
    }

}

class DrawCommand {

    public static final int LINE = 0;
    public static final int CLEAR_SCREEN = 1;
    public static final int SET_BACK_COLOR = 2;
    public static final int DRAW_STRING = 3;

    private String s;
    private int x1, y1, x2, y2;
    private Color c;
    private int type;

    public DrawCommand(int TYPE) {
        type = TYPE;
    }

    public final int getType() {
        return type;
    }

    public final void setStart(int x, int y) {
        x1 = x;
        y1 = y;
    }

    public final void setStop(int x, int y) {
        x2 = x;
        y2 = y;
    }

    public final void setColor(Color C) {
        c = C;
    }

    public final void setString(String S) {
        s = S;
    }

    public final void draw(Graphics g, Dimension d) {
        switch (type) {
            case LINE:
                g.setColor(c);
                g.drawLine(x1, y1, x2, y2);
                break;
            case CLEAR_SCREEN:
            case SET_BACK_COLOR:
                g.setColor(c);
                g.fillRect(-d.width / 2, -d.height / 2, d.width, d.height);
                break;
            case DRAW_STRING:
                g.setColor(c);
                if (s != null) g.drawString(s, x1, y1);
                break;
        }
    }

}