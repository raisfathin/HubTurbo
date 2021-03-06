package guitests;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.api.FxToolkit;
import org.apache.commons.lang3.RandomStringUtils;

import ui.IdGenerator;
import ui.TestController;
import ui.UI;
import ui.issuepanel.FilterPanel;
import ui.issuepanel.PanelControl;
import util.PlatformEx;
import util.events.ShowRenamePanelEvent;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static ui.components.KeyboardShortcuts.CLOSE_PANEL;
import static ui.components.KeyboardShortcuts.CREATE_RIGHT_PANEL;
import static ui.components.KeyboardShortcuts.MAXIMIZE_WINDOW;

import java.util.concurrent.TimeoutException;

public class PanelRenameTest extends UITest {

    public static final int EVENT_DELAY = 1000;
    public static final int PANEL_MAX_NAME_LENGTH = 36;
    private PanelControl panels;

    @Override
    public void setup() throws TimeoutException {
        FxToolkit.setupApplication(TestUI.class, "--bypasslogin=true");
    }

    @Before
    public void before() {
        panels = TestController.getUI().getPanelControl();
        int panelCount = panels.getPanelCount();
        while (panelCount-- > 1) {
            pushKeys(CLOSE_PANEL);
        }
        pushKeys(MAXIMIZE_WINDOW);
        sleep(EVENT_DELAY);
    }

    @Test
    public void panelRenameTest() {

        // Test for saving panel name

        // Testing case where rename is canceled with ESC
        // Expected: change not reflected
        PlatformEx.runAndWait(() -> UI.events.triggerEvent(new ShowRenamePanelEvent(0)));
        sleep(EVENT_DELAY);
        type("Renamed panel");
        push(KeyCode.ESCAPE);
        FilterPanel panel0 = (FilterPanel) panels.getPanel(0);
        Text panelNameText0 = panel0.getNameText();
        assertEquals("Panel", panelNameText0.getText());
        sleep(EVENT_DELAY);

        pushKeys(CREATE_RIGHT_PANEL);

        // Testing case where a name with whitespaces at either end is submitted
        // Expected: new name accepted with whitespaces removed
        PlatformEx.runAndWait(() -> UI.events.triggerEvent(new ShowRenamePanelEvent(1)));
        sleep(EVENT_DELAY);
        type("   Renamed panel  ");
        push(KeyCode.ENTER);
        FilterPanel panel1 = (FilterPanel) panels.getPanel(1);
        Text panelNameText1 = panel1.getNameText();
        assertEquals("Renamed panel", panelNameText1.getText());
        sleep(EVENT_DELAY);

        pushKeys(CREATE_RIGHT_PANEL);

        // Testing case where empty name is submitted
        // Expected: new name not accepted
        PlatformEx.runAndWait(() -> UI.events.triggerEvent(new ShowRenamePanelEvent(2)));
        sleep(EVENT_DELAY);
        push(KeyCode.BACK_SPACE);
        push(KeyCode.ENTER);
        FilterPanel panel2 = (FilterPanel) panels.getPanel(2);
        Text panelNameText2 = panel2.getNameText();
        assertEquals("Panel", panelNameText2.getText());
        sleep(EVENT_DELAY);

        // Testing whether the close button appears once rename box is opened.
        // Expected: Close button should not appear once rename box is opened and while edits are being made.
        //           It should appear once the rename box is closed and the edits are done.
        pushKeys(CREATE_RIGHT_PANEL);
        String panelCloseButtonId = IdGenerator.getPanelCloseButtonIdReference(3);
        String panelRenameTextFieldId =  IdGenerator.getPanelRenameTextFieldIdReference(3);
        String panelNameAreaId = IdGenerator.getPanelNameAreaIdReference(3);
        waitUntilNodeAppears(panelCloseButtonId);
        boolean isPresentBeforeEdit = GuiTest.exists(panelCloseButtonId);
        PlatformEx.runAndWait(() -> UI.events.triggerEvent(new ShowRenamePanelEvent(3)));
        PlatformEx.waitOnFxThread();
        assertFalse(existsQuiet(panelCloseButtonId));

        String randomName3 = RandomStringUtils.randomAlphanumeric(PANEL_MAX_NAME_LENGTH - 1);
        TextField renameTextField3 = GuiTest.find(panelRenameTextFieldId);
        renameTextField3.setText(randomName3);
        push(KeyCode.ENTER);
        boolean isPresentAfterEdit = GuiTest.exists(panelCloseButtonId);
        Text panelNameText3 = GuiTest.find(panelNameAreaId);
        assertEquals(true, isPresentBeforeEdit);
        assertEquals(true, isPresentAfterEdit);
        assertEquals(randomName3, panelNameText3.getText());
        PlatformEx.waitOnFxThread();

        // Testing case where the edit is confirmed using the tick button in rename mode
        // Expected: Panel is renamed after the button is pressed
        pushKeys(CREATE_RIGHT_PANEL);
        PlatformEx.runAndWait(() -> UI.events.triggerEvent(new ShowRenamePanelEvent(4)));
        type("Renamed panel with confirm tick");
        clickOn(IdGenerator.getOcticonButtonIdReference(4, "confirmButton"));
        FilterPanel panel4 = (FilterPanel) panels.getPanel(4);
        Text panelNameText4 = panel4.getNameText();
        assertEquals("Renamed panel with confirm tick", panelNameText4.getText());

        // Testing case where the edit is undone after pressing the undo button in edit mode
        // Expected: Panel name is unchanged after the button is pressed.
        pushKeys(CREATE_RIGHT_PANEL);
        PlatformEx.runAndWait(() -> UI.events.triggerEvent(new ShowRenamePanelEvent(5)));
        type("Renamed panel with undo");
        clickOn(IdGenerator.getOcticonButtonIdReference(5, "undoButton"));
        FilterPanel panel5 = (FilterPanel) panels.getPanel(5);
        Text panelNameText5 = panel5.getNameText();
        assertEquals("Panel", panelNameText5.getText());

        // Quitting to update json
        traverseMenu("File", "Quit");
        push(KeyCode.ENTER);
        sleep(EVENT_DELAY);
    }

    @Test
    public void panelRenameButton_onClick_panelGetsSelected() {
        // Test for the currently selected panel when trying to rename

        clickOn(IdGenerator.getOcticonButtonIdReference(0, "renameButton"));
        assertEquals(Optional.of(0), panels.getCurrentlySelectedPanel());
        pushKeys(CREATE_RIGHT_PANEL);
        clickOn(IdGenerator.getOcticonButtonIdReference(1, "renameButton"));
        assertEquals(Optional.of(1), panels.getCurrentlySelectedPanel());
        pushKeys(CLOSE_PANEL);
        pushKeys(KeyCode.ESCAPE);
        sleep(EVENT_DELAY);
    }
}
