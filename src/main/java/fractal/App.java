package fractal;

import fractal.canvas.FractalCanvas;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class App extends Frame implements WindowListener {
    private FractalCanvas fractalCanvas;

    public App() {
        init();
    }

    private void init() {
        fractalCanvas = new FractalCanvas();
        fractalCanvas.setSize(new Dimension(256, 256));
        this.add(fractalCanvas);
        this.addWindowListener(this);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    public void start() {
        final Point[] points = {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 0)};

        fractalCanvas.setPoints(points);
        fractalCanvas.drawFractal(1);
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        dispose();
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {

    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }

    public static void main(String[] args) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final App app = new App();
//                app.start();
            }
        };

        EventQueue.invokeLater(runnable);
    }
}
