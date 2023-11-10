package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class StackController extends AbstractJArmEmuModule {

    protected static final String HEX_FORMAT = "%08x";
    protected int DATA_FORMAT;
    private static final int MAX_NUMBER = 500;
    private int spDisplayer;

    private final Logger logger = Logger.getLogger(getClass().getName());
    protected ArrayList<Text[]> stackTexts;
    public StackController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stackTexts = new ArrayList<>();
    }

    /**
     * Met à jour les registres sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void updateGUI(StateContainer stateContainer) {
        // TODO: Corriger le scroll sur grand écran (windows)
        if (stateContainer == null) return;
        TreeMap<Integer, Integer> stack = new TreeMap<>();

        DATA_FORMAT = getSettingsController().getDataFormat();

        stack.putAll(getLowerValues(stateContainer));
        stack.putAll(getHigherValues(stateContainer));

        int i = 0;
        int sp = stateContainer.registers[RegisterUtils.SP.getN()].getData();
        spDisplayer = -1;
        for (Map.Entry<Integer, Integer> entry : stack.entrySet()) {
            boolean hasSp = entry.getKey().equals(sp);
            if (stackTexts.size() > i) {
                update(entry, i, hasSp);
            } else {
                create(entry, i, hasSp);
            }
            if (hasSp) spDisplayer = i;
            i++;
        }

        int s = stackTexts.size();
        for (int j = i; j < s; j++) {
            Text[] texts = stackTexts.remove(i);

            Platform.runLater(() -> {
                getController().stackGrid.getChildren().remove(texts[0]);
                getController().stackGrid.getChildren().remove(texts[1]);
                getController().stackGrid.getChildren().remove(texts[2]);
            });
        }

        if (spDisplayer != -1) {
            Platform.runLater(() -> {
                final double current = getController().stackScroll.getVvalue();

                final double totalSize = getController().stackGrid.getBoundsInParent().getHeight();
                final double viewSize = getController().stackScroll.getViewportBounds().getHeight();
                final double lineSize = getController().stackGrid.getChildren().getFirst().getBoundsInParent().getHeight();
                final double linePos = spDisplayer * lineSize;

                final double currentViewTop = (totalSize - viewSize) * current;
                final double currentViewBottom = currentViewTop + viewSize;

                if (linePos < currentViewTop) {
                    getController().stackScroll.setVvalue(linePos / (totalSize - viewSize));
                } else if ((linePos + lineSize) > currentViewBottom) {
                    getController().stackScroll.setVvalue((linePos - viewSize + lineSize * 1.3) / (totalSize - viewSize));
                }
            });
        }
    }

    private HashMap<Integer, Integer> getLowerValues(StateContainer container) {
        HashMap<Integer, Integer> rtn = new HashMap<>();
        int address = container.getStackAddress() - 4;
        int sp = container.registers[RegisterUtils.SP.getN()].getData();

        int number = 0;
        while (container.memory.isWordInitiated(address) || (sp < container.getStackAddress() && address >= sp)) {
            if (number > MAX_NUMBER) {
                rtn.put(address, null);
                break;
            }
            rtn.put(address, container.memory.getWord(address));
            address -= 4;
            number++;
        }

        return rtn;
    }

    private HashMap<Integer, Integer> getHigherValues(StateContainer container) {
        HashMap<Integer, Integer> rtn = new HashMap<>();
        int address = container.getStackAddress();
        int sp = container.registers[RegisterUtils.SP.getN()].getData();

        int number = 0;
        while (container.memory.isWordInitiated(address) || (sp >= container.getStackAddress() && address <= sp)) {
            if (number > MAX_NUMBER) {
                rtn.put(address, null);
                break;
            }
            rtn.put(address, container.memory.getWord(address));
            address += 4;
            number++;
        }

        return rtn;
    }

    private void create(Map.Entry<Integer, Integer> entry, int line, boolean sp) {
        Text[] texts = new Text[3];

        Text indicator = new Text(sp ? "➤" : "");
        indicator.getStyleClass().add("reg-data");
        indicator.setTextAlignment(TextAlignment.CENTER);
        Platform.runLater(() -> getController().stackGrid.add(indicator, 0, line));
        texts[0] = indicator;

        Text address = new Text(String.format(HEX_FORMAT, entry.getKey()).toUpperCase());
        address.getStyleClass().add("reg-address");
        address.setTextAlignment(TextAlignment.CENTER);
        Platform.runLater(() -> getController().stackGrid.add(address, 1, line));
        texts[1] = address;

        Text value;
        if (entry.getValue() == null) {
            value = new Text("...");
        } else {
            value = new Text(getApplication().getFormattedData(entry.getValue(), DATA_FORMAT));
        }
        value.getStyleClass().add("reg-data");
        value.setTextAlignment(TextAlignment.CENTER);
        Platform.runLater(() -> getController().stackGrid.add(value, 2, line));
        texts[2] = value;

        stackTexts.add(texts);
    }

    private void update(Map.Entry<Integer, Integer> entry, int line, boolean sp) {
        Text[] texts = stackTexts.get(line);

        texts[0].setText(sp ? "➤" : "");
        texts[1].setText(String.format(HEX_FORMAT, entry.getKey()).toUpperCase());
        if (entry.getValue() == null) {
            texts[2].setText("...");
        } else {
            texts[2].setText(getApplication().getFormattedData(entry.getValue(), DATA_FORMAT));
        }
    }

    public void clear() {
        int s = stackTexts.size();
        for (int j = 0; j < s; j++) {
            Text[] texts = stackTexts.remove(0);

            Platform.runLater(() -> {
                getController().stackGrid.getChildren().remove(texts[0]);
                getController().stackGrid.getChildren().remove(texts[1]);
                getController().stackGrid.getChildren().remove(texts[2]);
            });
        }
    }
}
