package de.kripa.guitools.std.gui;

import de.kripa.guitools.gui.GUIElement;
import de.kripa.guitools.std.element.EmptyElement;
import lombok.NonNull;

public class EmptyGUI extends SimpleGUI {
    public EmptyGUI(@NonNull String title, int rows) {
        super(title, generateFillerElementArray(rows * 9));
    }

    private static GUIElement[] generateFillerElementArray(int length) {
        GUIElement[] result = new GUIElement[length];
        for (int i = 0; i < length; i++) {
            result[i] = new EmptyElement();
        }
        return result;
    }
}
