package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.StateContainer;

public class EORExecutor implements InstructionExecutor {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, int arg1, int arg2, int arg3, int arg4) {
        //TODO: Faire l'instruction EOR
        throw new IllegalStateException("Instruction EOR not implemented");
    }
}
