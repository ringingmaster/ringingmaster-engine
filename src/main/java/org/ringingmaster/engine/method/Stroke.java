package org.ringingmaster.engine.method;

/**
 * TODO comments???
 * User: Stephen
 */
public enum Stroke {
    HANDSTROKE("Handstroke"),
    BACKSTROKE("Backstroke"),
    ;

    public static Stroke flipStroke(Stroke stroke) {
        return (stroke == HANDSTROKE) ? BACKSTROKE : HANDSTROKE;
    }

    private String displayString;

    Stroke(String displayString) {
        this.displayString = displayString;
    }

    public String getDisplayString() {
        return displayString;
    }
}
