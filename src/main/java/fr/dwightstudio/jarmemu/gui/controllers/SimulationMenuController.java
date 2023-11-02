package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.gui.LineStatus;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import javafx.fxml.FXML;

import java.util.logging.Logger;

public class SimulationMenuController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    public SimulationMenuController(JArmEmuApplication application) {
        super(application);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSimulate() {
        getController().simulate.setDisable(true);
        getEditorController().clearNotifs();
        getExecutionWorker().revive();
        getExecutionWorker().prepare();
    }

    /**
     * Méthode de rappel si la préparation de la simulation s'est effectué avec succès
     * @param errors les erreurs rencontrées lors de l'analyse du code
     */
    public void launchSimulation(AssemblyError[] errors) {
        if (errors.length == 0 && getCodeInterpreter().getInstructionCount() != 0) {
            getEditorController().clearLineMarking();
            getEditorController().markLine(getCodeInterpreter().getNextLine(), LineStatus.SCHEDULED);
            getController().memoryPage.setDisable(false);
            getController().addressField.setDisable(false);
            getController().codeArea.setDisable(true);
            getController().stepInto.setDisable(false);
            getController().stepOver.setDisable(false);
            getController().conti.setDisable(false);
            getController().pause.setDisable(true);
            getController().stop.setDisable(false);
            getController().restart.setDisable(false);
            getController().reset.setDisable(false);
            application.status = Status.SIMULATING;
        } else {
            getController().simulate.setDisable(false);
            if (getCodeInterpreter().getInstructionCount() == 0) {
                getEditorController().addNotif("Parsing error: ", "No instructions detected", "danger");
            }
            for (AssemblyError error : errors) {
                getEditorController().addError(error);
            }
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStepInto() {
        getEditorController().clearNotifs();
        getExecutionWorker().stepInto();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStepOver() {
        getEditorController().clearNotifs();
        getExecutionWorker().stepOver();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onContinue() {
        getEditorController().clearNotifs();
        getExecutionWorker().conti();
        getController().stepInto.setDisable(true);
        getController().stepOver.setDisable(true);
        getController().conti.setDisable(true);
        getController().pause.setDisable(false);
        getController().restart.setDisable(true);
        getController().reset.setDisable(true);
        getController().memoryPage.setDisable(true);
        getController().addressField.setDisable(true);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onPause() {
        getExecutionWorker().pause();
        getController().stepInto.setDisable(false);
        getController().stepOver.setDisable(false);
        getController().conti.setDisable(false);
        getController().pause.setDisable(true);
        getController().restart.setDisable(false);
        getController().reset.setDisable(false);
        getController().memoryPage.setDisable(false);
        getController().addressField.setDisable(false);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onStop() {
        getEditorController().clearNotifs();
        getExecutionWorker().pause();
        getController().codeArea.deselect();
        getController().codeArea.setDisable(false);
        getController().simulate.setDisable(false);
        getController().stepInto.setDisable(true);
        getController().stepOver.setDisable(true);
        getController().conti.setDisable(true);
        getController().pause.setDisable(true);
        getController().stop.setDisable(true);
        getController().restart.setDisable(true);
        getController().reset.setDisable(true);
        getController().memoryPage.setDisable(true);
        getController().addressField.setDisable(true);
        application.status = Status.EDITING;
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onRestart() {
        getEditorController().clearNotifs();
        getCodeInterpreter().restart();
        getEditorController().clearLineMarking();
        getEditorController().markLine(getCodeInterpreter().getNextLine(), LineStatus.SCHEDULED);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onReset() {
        getEditorController().clearNotifs();
        getCodeInterpreter().resetState();
        getExecutionWorker().updateGUI();
    }
}
