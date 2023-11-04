package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.args.ShiftParser;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class STRExecutor implements InstructionExecutor<Register, AddressParser.UpdatableInteger, Integer, ShiftParser.ShiftFunction> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, AddressParser.UpdatableInteger arg2, Integer arg3, ShiftParser.ShiftFunction arg4) {
        //TODO: Faire l'instruction STR
        throw new IllegalStateException("Instruction STR not implemented");
    }
}
