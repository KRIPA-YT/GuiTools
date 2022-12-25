package de.kripa.guitools.std.gui;

import de.kripa.guitools.gui.GUIElement;
import de.kripa.guitools.std.element.EmptyElement;
import lombok.NonNull;

import java.util.Arrays;

public class EmptyGUI extends SimpleGUI {
    public EmptyGUI(@NonNull String title, int rows) {
        super(title, generateFillerElementArray(rows * 9));
    }

    private static GUIElement[] generateFillerElementArray(int length) {
        return Arrays.stream(new GUIElement[length]).map(guiElement -> guiElement = new EmptyElement()).toArray(GUIElement[]::new);
    }

    protected void reset() {
        this.content = generateFillerElementArray(this.content.length);
    }
}
