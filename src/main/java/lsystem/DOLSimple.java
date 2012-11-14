package lsystem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class DOLSimple extends JPanel {

    static final int THETA_ANGLE = 90;
    static final int WIDTH = 770;
    static final int HEIGHT = 750;

    private DOLSystem ls = null;
    private int startX = 0;
    private int startY = 0;
    private int theta = 90;
    private int step = 1;
    private int iter = 4;
    private final JTextField txtIter = new JTextField(5);
    private final JTextField txtTheta = new JTextField(5);
    private final JTextField txtSize = new JTextField(5);
    private final JTextField txtAxiom = new JTextField(10);
    private final JTextField txtProdF = new JTextField(15);
    private final JTextField txtProdG = new JTextField(15);
    private final JSlider slider1 = new JSlider();
    private final JSlider slider2 = new JSlider();
    private final JSlider slider3 = new JSlider();
    private TurtleGraphicsPanel turtle;
    private static Stack<Map<String, Float>> stack = new Stack<Map<String, Float>>();

    public DOLSimple() {
        turtle = new TurtleGraphicsPanel();
        turtle.setSize(new Dimension(WIDTH, HEIGHT));
        initDefaultValues();

        turtle.setRepaint(true);
        turtle.setBackColor(Color.WHITE);
        turtle.setVisible(true);

        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));

        this.add(createControlPanel(), BorderLayout.NORTH);
        this.add(turtle, BorderLayout.CENTER);
        this.add(createModificationPanel(), BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void render(String model, TurtleGraphicsPanel turtle, int theta, int step, int curX, int curY) {
        turtle.clearScreen();
        turtle.home(curX, curY);
        turtle.setTheta(THETA_ANGLE);
        for (int i = 0; i < model.length(); i++) {
            switch (model.charAt(i)) {
                case 'F':
                    turtle.backward(step);
                    break;
                case '+':
                    turtle.turnRight(theta);
                    break;
                case '-':
                    turtle.turnLeft(theta);
                    break;
                case '[':
                    Map saved = new HashMap<String, Float>();
                    saved.put("x", turtle.getCurX());
                    saved.put("y", turtle.getCurY());
                    saved.put("angle", new Float(turtle.getTheta()));
                    stack.push(saved);
                    break;
                case ']':
                    Map<String, Float> m = stack.pop();
                    turtle.setCurX(m.get("x"));
                    turtle.setCurY(m.get("y"));
                    turtle.setTheta(m.get("angle").intValue());
                    break;
                case 'G':
                    turtle.penUp();
                    turtle.backward(step);
                    turtle.penDown();
            }
        }
        turtle.showTurtle();

        txtIter.setText(String.valueOf(iter));
        txtTheta.setText(String.valueOf(theta));
        txtSize.setText(String.valueOf(step));
        txtAxiom.setText(String.valueOf(ls.axiom));
        txtProdF.setText(String.valueOf(ls.rules.get('F')));
        txtProdG.setText(String.valueOf(ls.rules.get('G')));
        slider1.setValue(theta);
        slider2.setValue(step);
        slider3.setValue(iter);
    }

    private JPanel createModificationPanel() {
        JPanel modificationPanel = new JPanel();
        modificationPanel.setLayout(new BorderLayout());
        modificationPanel.setVisible(true);


        JPanel subPanel1 = new JPanel();
        subPanel1.setLayout(new GridLayout(1, 3));

        JPanel subPanel2 = new JPanel();
        subPanel2.setLayout(new GridLayout(1, 6));


        txtTheta.setEditable(false);
        txtTheta.setBorder(BorderFactory.createTitledBorder("Theta:"));
        txtTheta.setSize(new Dimension(50, 30));

        txtSize.setEditable(false);
        txtSize.setBorder(BorderFactory.createTitledBorder("Tam:"));
        txtSize.setSize(new Dimension(50, 30));

        txtIter.setEditable(false);
        txtIter.setBorder(BorderFactory.createTitledBorder("Iter:"));
        txtIter.setSize(new Dimension(50, 30));

        txtAxiom.setEditable(false);
        txtAxiom.setBorder(BorderFactory.createTitledBorder("Axiom:"));
        txtAxiom.setSize(new Dimension(50, 30));
        txtAxiom.setText(ls.axiom);

        txtProdF.setEditable(false);
        txtProdF.setBorder(BorderFactory.createTitledBorder("Prod (F):"));
        txtProdF.setSize(new Dimension(50, 30));
        txtProdF.setText(ls.rules.get('F'));

        txtProdG.setEditable(false);
        txtProdG.setBorder(BorderFactory.createTitledBorder("Prod (G):"));
        txtProdG.setSize(new Dimension(50, 30));
        txtProdG.setText(ls.rules.get('G'));

        slider1.setBorder(BorderFactory.createTitledBorder("Angulo:"));
        slider1.setPaintTicks(true);
        slider1.setPaintTrack(true);
        slider1.setMinimum(-90);
        slider1.setMaximum(90);
        slider1.setOrientation(JSlider.HORIZONTAL);

        slider1.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                Object source = ce.getSource();
                if (source instanceof JSlider) {
                    JSlider theJSlider = (JSlider) source;
                    if (!theJSlider.getValueIsAdjusting()) {
                        theta = theJSlider.getValue();
                        txtTheta.setText(theta + "");
                        render(ls.getCode(), turtle, theta, step, startX, startY);
                    }
                } else {
                    System.out.println("Something changed: " + source);
                }
            }
        });

        slider2.setBorder(BorderFactory.createTitledBorder("Tamanho do passo:"));
        slider2.setPaintTicks(true);
        slider2.setMinimum(1);
        slider2.setMaximum(12);
        slider2.setOrientation(JSlider.HORIZONTAL);

        slider2.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                Object source = ce.getSource();
                if (source instanceof JSlider) {
                    JSlider theJSlider = (JSlider) source;
                    if (!theJSlider.getValueIsAdjusting()) {
                        step = theJSlider.getValue();
                        txtSize.setText(step + "");

                        render(ls.getCode(), turtle, theta, step, startX, startY);
                    }
                } else {
                    System.out.println("Something changed: " + source);
                }
            }
        });

        slider3.setBorder(BorderFactory.createTitledBorder("Iteracoes:"));
        slider3.setPaintTicks(true);
        slider3.setMinimum(1);
        slider3.setMaximum(9);
        slider3.setPaintLabels(true);
        slider3.setOrientation(JSlider.HORIZONTAL);

        slider3.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                Object source = ce.getSource();
                if (source instanceof JSlider) {
                    JSlider theJSlider = (JSlider) source;
                    if (!theJSlider.getValueIsAdjusting()) {
                        iter = theJSlider.getValue();
                        txtIter.setText(iter + "");

                        ls.iterate(iter);
                        render(ls.getCode(), turtle, theta, step, startX, startY);
                    }
                } else {
                    System.out.println("Something changed: " + source);
                }
            }
        });


        subPanel1.add(slider3);
        subPanel1.add(slider1);
        subPanel1.add(slider2);

        subPanel2.add(txtIter);
        subPanel2.add(txtTheta);
        subPanel2.add(txtSize);
        subPanel2.add(txtAxiom);
        subPanel2.add(txtProdF);
        subPanel2.add(txtProdG);


        modificationPanel.add(subPanel1, BorderLayout.CENTER);
        modificationPanel.add(subPanel2, BorderLayout.SOUTH);

        return modificationPanel;
    }

    private JPanel createControlPanel() {
        JPanel control = new JPanel();
        control.setLayout(new GridLayout(2, 5));
        control.setBackground(Color.GRAY);
        control.setVisible(true);

        JButton button1 = new JButton("Planta 1");
        button1.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                rules.put('G', "F+[[G]-G]-F[-FG]+G");
                ls = new DOLSystem("G", rules);
                iter = 7;

                ls.iterate(iter);
                turtle.clearScreen();
                turtle.setRepaint(true);

                startX = 50;
                startY = 250;
                theta = 22;
                step = 1;
                render(ls.getCode(), turtle, theta, step, startX, startY);
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });

        JButton button2 = new JButton("Planta 2");
        button2.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF+[+F-F-F]-[-F+F+F]");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                ls = new DOLSystem("F", rules);
                iter = 4;
                ls.iterate(iter);
                turtle.clearScreen();
                turtle.setRepaint(true);

                startX = 0;
                startY = 250;
                theta = 22;
                step = 7;
                render(ls.getCode(), turtle, theta, step, startX, startY);

            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });

        JButton button3 = new JButton("Planta 3");
        button3.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                rules.put('G', "F[-G]F[+G]-G");

                ls = new DOLSystem("G", rules);
                iter = 8;
                ls.iterate(iter);
                turtle.clearScreen();
                turtle.setRepaint(true);

                startX = 0;
                startY = 250;
                theta = 20;
                step = 1;
                render(ls.getCode(), turtle, theta, step, startX, startY);
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });

        JButton button4 = new JButton("Planta 4");
        button4.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                rules.put('G', "FG[-F[G]-G][G+G][+F[G]+G]");

                ls = new DOLSystem("G", rules);
                iter = 5;
                ls.iterate(iter);
                turtle.clearScreen();
                turtle.setRepaint(true);

                startX = 0;
                startY = 250;
                theta = 22;
                step = 3;
                render(ls.getCode(), turtle, theta, step, startX, startY);
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });

        JButton button5 = new JButton("Planta 5");
        button5.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                rules.put('G', "F[-G][+G]FG");

                ls = new DOLSystem("G", rules);
                iter = 8;
                ls.iterate(iter);


                startX = 0;
                startY = 250;
                theta = 26;
                step = 1;
                render(ls.getCode(), turtle, theta, step, startX, startY);
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });

        JButton button6 = new JButton("Planta 6");
        button6.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                rules.put('G', "F+[[G]-G]-F[-FG]+F");
                ls = new DOLSystem("G", rules);
                iter = 7;

                ls.iterate(iter);
                turtle.clearScreen();
                turtle.setRepaint(true);

                startX = 50;
                startY = 250;
                theta = 22;
                step = 1;
                render(ls.getCode(), turtle, theta, step, startX, startY);
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });

        JButton button7 = new JButton("Planta 7");
        button7.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF+[+F-F-F+F]-[-F+F+F-F]");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                ls = new DOLSystem("F", rules);
                iter = 4;
                ls.iterate(iter);
                turtle.clearScreen();
                turtle.setRepaint(true);

                startX = 0;
                startY = 250;
                theta = 22;
                step = 6;
                render(ls.getCode(), turtle, theta, step, startX, startY);

            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });


        JButton button8 = new JButton("Planta 8");
        button8.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                rules.put('G', "F[G-G]F[+G+G]-G");

                ls = new DOLSystem("G", rules);
                iter = 7;
                ls.iterate(iter);
                turtle.clearScreen();
                turtle.setRepaint(true);

                startX = 0;
                startY = 250;
                theta = -8;
                step = 1;
                render(ls.getCode(), turtle, theta, step, startX, startY);
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });

        JButton button9 = new JButton("Planta 9");

        button9.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                rules.put('G', "F[-GGF][+GGF]FG");

                ls = new DOLSystem("G", rules);
                iter = 6;
                ls.iterate(iter);


                startX = 0;
                startY = 250;
                theta = 26;
                step = 2;
                render(ls.getCode(), turtle, theta, step, startX, startY);
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });

        JButton button10 = new JButton("Planta 10");
        button10.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                Map<Character, String> rules = new HashMap<Character, String>();
                rules.put('F', "FF");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");
                rules.put('G', "F[-GG][+GG]FG");

                ls = new DOLSystem("G", rules);
                iter = 7;
                ls.iterate(iter);


                startX = 0;
                startY = 250;
                theta = 26;
                step = 1;
                render(ls.getCode(), turtle, theta, step, startX, startY);
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });

        JButton button11 = new JButton("Aleatorio");
        button11.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {

                Map<Character, String> rules = new HashMap<Character, String>();
                StringBuilder str = new StringBuilder();
                int push = 0;
                int max = new Random().nextInt(15);
                if (max < 4) {
                    max += 4;
                }
                for (int i = 0; i < max; i++) {
                    int r = new Random().nextInt(6);
                    switch (r) {
                        case 1:
                            str.append("F");
                            break;
                        case 2:
                            if (str.length() > 1) {
                                if (str.toString().charAt(str.toString().length() - 1) == '+') {
                                    if (Math.random() < 0.5) {
                                        str.append("F");
                                    } else {
                                        str.append("G");
                                    }
                                } else {
                                    str.append("+");
                                }

                            } else {
                                str.append("+");
                            }
                            break;
                        case 3:
                            if (str.length() > 1) {
                                if (str.toString().charAt(str.toString().length() - 1) == '-') {
                                    if (Math.random() < 0.5) {
                                        str.append("F");
                                    } else {
                                        str.append("G");
                                    }
                                } else {
                                    str.append("-");
                                }

                            } else {
                                str.append("-");
                            }
                            break;
                        case 4:
                            str.append("[");
                            push++;
                            break;
                        case 5:
                            if (str.length() > 1 && push > 0 && push % 2 != 0) {
                                if (str.toString().charAt(str.toString().length() - 1) == '[') {
                                    if (Math.random() < 0.5) {
                                        str.append("F");
                                    } else {
                                        str.append("G");
                                    }
                                } else {
                                    str.append("]");
                                    push--;
                                }

                            } else {
                                if (Math.random() < 0.5) {
                                    str.append("F");
                                } else {
                                    str.append("G");
                                }
                            }
                            break;
                        case 6:
                            str.append("G");
                            break;
                    }

                }
                while (push > 0) {
                    str.append("]");
                    push--;
                }
                //rules.put('G', str.toString());
                rules.put('F', "F[+FF][-FF]F[+FF][-FF]F");
                rules.put('+', "+");
                rules.put('-', "-");
                rules.put('[', "[");
                rules.put(']', "]");

                System.out.printf("Model: %s \n", str.toString());

                ls = new DOLSystem("F", rules);
                theta = 12;
                iter = 3;
                ls.iterate(iter);
                turtle.clearScreen();
                turtle.setRepaint(true);

                turtle.home(0, 0);
                render(ls.getCode(), turtle, theta, step, 0, 0);
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });


        control.add(button1);
        control.add(button2);
        control.add(button3);
        control.add(button4);
        control.add(button5);
        control.add(button6);
        control.add(button7);
        control.add(button8);
        control.add(button9);
        control.add(button10);


        return control;
    }

    private void initDefaultValues() {
        Map<Character, String> rules = new HashMap<Character, String>();
        rules.put('F', "FF");
        rules.put('+', "+");
        rules.put('-', "-");
        rules.put('[', "[");
        rules.put(']', "]");
        rules.put('G', "F+[[G]-G]-F[-FG]+G");
        ls = new DOLSystem("G", rules);

        startX = 50;
        startY = 250;
        theta = 22;
        step = 1;
        iter = 7;
    }
    
    public static void main(String[] args) {
	for (int j=0; j<50; j++){
	for (int i=0; i< 30; i++) {
	    System.out.printf("%s ", new Random().nextInt(20));
	}
	    System.out.printf("\n");
	}
    }

}
