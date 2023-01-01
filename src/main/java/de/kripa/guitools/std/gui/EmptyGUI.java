package de.kripa.guitools.std.gui;

import de.kripa.guitools.gui.GUIElement;
import de.kripa.guitools.std.element.EmptyElement;
import lombok.NonNull;

import java.util.Arrays;

public class EmptyGUI extends SimpleGUI {
    public EmptyGUI(@NonNull String title, int rows) {
        this(title, rows, "GRAY");
    }
    public EmptyGUI(@NonNull String title, int rows, @NonNull String color) {
        super(title, generateFillerElementArray(rows * 9, color));
    }

    protected static GUIElement[] generateFillerElementArray(int length) {
        return generateFillerElementArray(length, "GRAY");
    }

    protected static GUIElement[] generateFillerElementArray(int length, String color) {
        return Arrays.stream(new GUIElement[length]).map(guiElement -> guiElement = new EmptyElement(color)).toArray(GUIElement[]::new);
    }

    protected void reset() {
        this.content = generateFillerElementArray(this.content.length);
    }
}
