import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.lang.Math;

class Vector2 {
    double x;
    double y;

    public String toString() { return "(" + this.x + ", " + this.y + ")"; }

    Vector2() {} // NOTE: x and y automatically initialized to zero
    Vector2(double s) {
        this.x = s;
        this.y = s;
    }
    Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    Vector2(Vector2 p) {
        this.x = p.x;
        this.y = p.y;
    }

    static final Vector2 right = new Vector2( 1.0,  0.0);
    static final Vector2 left  = new Vector2(-1.0,  0.0);
    static final Vector2 up    = new Vector2( 0.0,  1.0);
    static final Vector2 down  = new Vector2( 0.0, -1.0);

    // NOTE: These are nice
    // TODO: pythagorean theorem question
    Vector2 plus(Vector2 other) { return new Vector2(this.x + other.x, this.y + other.y); }
    Vector2 minus(Vector2 other) { return new Vector2(this.x - other.x, this.y - other.y); }
    Vector2 times(double scalar) { return new Vector2(scalar * this.x, scalar * this.y); }
    Vector2 dividedBy(double scalar) { return this.times(1.0 / scalar); }
    double squaredLength() { return this.x * this.x + this.y * this.y; }
    double length() { return Math.sqrt(this.squaredLength()); }
    Vector2 direction() { return this.dividedBy(this.length()); }

    static double distanceBetween(Vector2 a, Vector2 b) { return (b.minus(a)).length(); }
    static Vector2 directionFrom(Vector2 a, Vector2 b) { return (b.minus(a)).direction(); }
}

class Vector3 {
    double x;
    double y;
    double z;

    public String toString() { return "(" + this.x + ", " + this.y + ", " + this.z + ")"; }

    Vector3() { }
    Vector3(double s) {
        this.x = s;
        this.y = s;
        this.z = s;
    }
    Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    Vector3(Vector3 p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    static final Vector3 white   = new Vector3(1.0, 1.0, 1.0);
    static final Vector3 black   = new Vector3(0.0, 0.0, 0.0);
    static final Vector3 red     = new Vector3(1.0, 0.0, 0.0);
    static final Vector3 yellow  = new Vector3(1.0, 1.0, 0.0);
    static final Vector3 green   = new Vector3(0.0, 1.0, 0.0);
    static final Vector3 cyan    = new Vector3(0.0, 1.0, 1.0);
    static final Vector3 blue    = new Vector3(0.0, 0.0, 1.0);
    static final Vector3 magenta = new Vector3(1.0, 0.0, 1.0);
    static final Vector3 gray    = new Vector3(0.5, 0.5, 0.5);
    static Vector3 rainbowSwirl(double time) {
        return new Vector3(_rainbowSwirlHelper(time, 0.0), _rainbowSwirlHelper(time, 0.33), _rainbowSwirlHelper(time, -0.33));
    }

    ////////////////////////////////////////////////////////////////////////////

    static double _rainbowSwirlHelper(double time, double offset) {
        return 0.5 + 0.5 * Math.cos(6.28 * (offset - time));
    }
}

class DemoApp extends App {
    Vector2 chaserPosition;
    double time;

    void setup() {
        chaserPosition = new Vector2();
        time = 0.0;
    }

    void loop() {
        if (!keyToggled('P')) { time += 0.0167; }
        if (mouseHeld) {
            chaserPosition = chaserPosition.plus(Vector2.directionFrom(chaserPosition, mousePosition));
        }
        drawLine(chaserPosition, mousePosition, Vector3.white);
        drawCircle(chaserPosition, 2.0, Vector3.rainbowSwirl(time));
        drawCenterRectangle(mousePosition, new Vector2(4.0), Vector3.cyan);
    }

    public static void main(String[] arguments) { new DemoApp().run(); }
}

class App extends JPanel {
    // app
    void setup() { }
    void loop() {}
    void run(double _windowWidthInWorldUnits, double _windowHeightInWorldUnits, double _windowCenterXInWorldUnits, double _windowCenterYInWorldUnits, int windowHeightInPixels) {
        this.setBackground(Color.BLACK);
        this._windowWidthInWorldUnits = _windowWidthInWorldUnits;
        this._windowHeightInWorldUnits = _windowHeightInWorldUnits;
        this._windowCenterXInWorldUnits = _windowCenterXInWorldUnits;
        this._windowCenterYInWorldUnits = _windowCenterYInWorldUnits;
        this._jFrame.setSize((int) (_windowWidthInWorldUnits / _windowHeightInWorldUnits * windowHeightInPixels), (int) (windowHeightInPixels));
        _jFrame.setVisible(true);
        while (!keyHeld('Q')) {
            this.repaint();
            try { Thread.sleep(1000 / 60); } catch (Exception e) { }
        }
        System.exit(0);
    }
    void run() { this.run(128, 128, 0, 0, 512); }
    
    // graphics
    void drawLine(Vector2 _pointA, Vector2 _pointB, Vector3 color) {
        Vector2 pointA = _windowPixelFromWorld(_pointA);
        Vector2 pointB = _windowPixelFromWorld(_pointB);
        _graphicsSetColor(color);
        _graphics.drawLine((int) pointA.x, (int) pointA.y, (int) pointB.x, (int) pointB.y);
    }
    void drawCircle(Vector2 center, double radius, Vector3 color) { _drawCenterShape(center, new Vector2(2 * radius), color, 1); }
    void drawCenterRectangle(Vector2 center, Vector2 size, Vector3 color) { _drawCenterShape(center, size, color, 0); }
    void drawCornerRectangle(Vector2 _cornerA, Vector2 _cornerB, Vector3 color) { _drawCornerShape(_cornerA, _cornerB, color, 0); }

    // input
    Vector2 mousePosition;
    boolean mousePressed = false;
    boolean mouseHeld = false;
    boolean mouseReleased = false;
    boolean keyHeld(int key) { return _keyHeld.getOrDefault(_keyMakeCaseInvariant(key), false); }
    boolean keyPressed(int key) { return _keyPressed.getOrDefault(_keyMakeCaseInvariant(key), false); }
    boolean keyReleased(int key) { return _keyReleased.getOrDefault(_keyMakeCaseInvariant(key), false); }
    boolean keyToggled(int key) { return _keyToggled.getOrDefault(_keyMakeCaseInvariant(key), false); }

    ////////////////////////////////////////////////////////////////////////////

    int _windowWidthInPixels;
    int _windowHeightInPixels;
    double _windowWidthInWorldUnits;
    double _windowHeightInWorldUnits;
    double _windowCenterXInWorldUnits;
    double _windowCenterYInWorldUnits;
    double _windowPixelsPerWorldUnits() { return _windowHeightInPixels / _windowHeightInWorldUnits; }
    Vector2 _windowPixelFromWorld(Vector2 sWorld) {
        Vector2 sPixel = new Vector2();
        double scale = _windowPixelsPerWorldUnits();
        sPixel.x = (int) (scale *                             ((sWorld.x - (_windowCenterXInWorldUnits - .5 *  _windowWidthInWorldUnits))));
        sPixel.y = (int) (scale * (_windowHeightInWorldUnits - (sWorld.y - (_windowCenterYInWorldUnits - .5 * _windowHeightInWorldUnits))));
        return sPixel;
    }
    Vector2 _windowWorldFromPixel(Vector2 sPixel) {
        Vector2 sWorld = new Vector2();
        double scale = _windowPixelsPerWorldUnits();
        sWorld.x =                             (sPixel.x / scale) + (_windowCenterXInWorldUnits - .5 *  _windowWidthInWorldUnits);
        sWorld.y = _windowHeightInWorldUnits - (sPixel.y / scale) + (_windowCenterYInWorldUnits - .5 * _windowHeightInWorldUnits);
        return sWorld;
    }


    void _graphicsSetColor(Vector3 color) {
        _graphics.setColor(new Color((float) color.x, (float) color.y, (float) color.z));
    }
    void _drawCenterShape(Vector2 center, Vector2 size, Vector3 color, int shapeType) {
        Vector2 halfSize = size.dividedBy(2.0);
        _drawCornerShape(center.minus(halfSize), center.plus(halfSize), color, shapeType);
    }
    void _drawCornerShape(Vector2 _cornerA, Vector2 _cornerB, Vector3 color, int shapeType) {
        Vector2 cornerA = _windowPixelFromWorld(_cornerA);
        Vector2 cornerB = _windowPixelFromWorld(_cornerB);

        // swap if necessary to make A lower-left and B upper-right
        if (cornerA.x > cornerB.x) { double tmp = cornerA.x; cornerA.x = cornerB.x; cornerB.x = tmp; }
        if (cornerA.y > cornerB.y) { double tmp = cornerA.y; cornerA.y = cornerB.y; cornerB.y = tmp; }

        _graphicsSetColor(color);
        int arg0 = (int) (cornerA.x);
        int arg1 = (int) (cornerA.y);
        int arg2 = (int) (cornerB.x - cornerA.x);
        int arg3 = (int) (cornerB.y - cornerA.y);
        if (shapeType == 0) {
            _graphics.fillRect(arg0, arg1, arg2, arg3);
        } else {
            _graphics.fillOval(arg0, arg1, arg2, arg3);
        }
    }
    void _drawLineStrip(Collection<Vector2> points, Vector3 color) { _drawLineStrip(points.toArray(new Vector2[0]), color); }
    void _drawLineStrip(Vector2[] points, Vector3 color) {
        int nPoints = points.length;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];

        for (int i = 0; i < nPoints; ++i) {
            Vector2 tmp = _windowPixelFromWorld(points[i]);
            xPoints[i] = (int) tmp.x;
            yPoints[i] = (int) tmp.y;
        }

        _graphicsSetColor(color);
        _graphics.drawPolyline(xPoints, yPoints, nPoints);
    }


    JFrame _jFrame;
    Hashtable<Integer, Boolean> _keyPressed = new Hashtable<>();
    Hashtable<Integer, Boolean> _keyHeld = new Hashtable<>();
    Hashtable<Integer, Boolean> _keyReleased = new Hashtable<>();
    Hashtable<Integer, Boolean> _keyToggled = new Hashtable<>();
    int _keyMakeCaseInvariant(int key) {
        if ('a' <= key && key <= 'z') {
            return 'A' + (key - 'a');
        }
        return key;
    }


    App() {
        super();

        {
            this.addMouseListener( new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    mousePressed = true;
                    mouseHeld = true;
                }

                @Override public void mouseReleased(MouseEvent e) {
                mouseHeld = false;
                mouseReleased = true;
                }
            });

            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
                synchronized (App.class) {
                    int key = event.getKeyCode();
                    if (event.getID() == KeyEvent.KEY_PRESSED) {
                        if (!keyHeld(key)) {
                            _keyPressed.put(key, true);
                        }
                        _keyHeld.put(key, true);
                        _keyToggled.put(key, !_keyToggled.getOrDefault(key, false));
                    }
                    if (event.getID() == KeyEvent.KEY_RELEASED) {
                        _keyReleased.put(key, true);
                        _keyHeld.put(key, false);
                    }
                    return false;
                }
            });
        }
        {
            _jFrame = new JFrame("CS136");
            _jFrame.setSize(1000, 500);
            _jFrame.setLocation(256, 64); // TODO: expose
            _jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            _jFrame.getContentPane().add(this, BorderLayout.CENTER);
            _jFrame.setUndecorated(true);
        }
    }


    boolean _initialized = false;
    Graphics _graphics;

    @Override 
    public void paintComponent(Graphics _graphics) {
        // NOTE: try-catch to actually kill the app on an error
        try {
            super.paintComponent(_graphics);
            this._graphics = _graphics; {
                Rectangle rectangle = _jFrame.getBounds();
                _windowHeightInPixels = rectangle.height;
                _windowWidthInPixels = rectangle.width;
            }

            {
                Point point;
                {
                    point = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(point, this);
                }
                this.mousePosition = _windowWorldFromPixel(new Vector2(point.x, point.y));
            }

            if (!_initialized || keyPressed('r')) {
                _initialized = true;
                setup();

                mousePressed = false;
                mouseHeld = false;
                mouseReleased = false;
                _keyPressed.clear();
                _keyHeld.clear();
                _keyReleased.clear();
                _keyToggled.clear();
            }

            loop();

            { // end of _jFrame
                mousePressed = false;
                mouseReleased = false;
                _keyPressed.clear();
                _keyReleased.clear();
            }
        } catch (Exception exception) {
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }

    public static void main(String[] arguments) {
        new App().run();
    }
}

