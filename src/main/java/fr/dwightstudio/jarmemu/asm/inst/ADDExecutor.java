package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;

import java.util.function.Function;

public class ADDExecutor implements InstructionExecutor<Register, Register, Integer, Function<Integer, Integer>> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, Function<Integer, Integer> arg4) {
        //TODO: Faire l'instruction ADD
        Integer i1 = arg4.apply(arg3);

        arg1.setData(arg2.getData() + i1); // arg1 = arg2 + (arg4 SHIFT arg3)
        if (updateFlags){
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
            stateContainer.cpsr.setC(MathUtils.hasCarry(arg2.getData(), i1));
            stateContainer.cpsr.setV(MathUtils.hasOverflow(arg2.getData(), i1));
        }
    }
}
