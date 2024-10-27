package org.cef;

import java.awt.*;

public class DummyComponent extends Component {
    public Point getLocationOnScreen() {
        return new Point(0, 0);
    }
}
