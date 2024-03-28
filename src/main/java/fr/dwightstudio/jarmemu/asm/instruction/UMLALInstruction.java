package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

public class UMLALInstruction extends ParsedInstruction<Register, Register, Register, Register> {
    public UMLALInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg2Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg3Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg4Class() {
        return RegisterArgument.class;
    }

    @Override
    public boolean doModifyPC() {
        return false;
    }

    @Override
    public boolean hasWorkingRegister() {
        return false;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, Register arg2, Register arg3, Register arg4) throws ExecutionASMException {
        long r3 = arg3.getData() & 0xFFFFFFFFL;
        long r4 = arg4.getData() & 0xFFFFFFFFL;
        long result = (((long) arg2.getData() << 32) | (arg1.getData() & 0xFFFFFFFFL)) + r3 * r4;   // result = (arg2[63..32]..arg1[31..0]) + (unsigned) arg3 * (unsigned) arg4
        arg1.setData((int) (result));   // arg1 = result[31..0]
        arg2.setData((int) (result >>> 32));    // arg1 = result[63..32]

        if (updateFlags) {
            stateContainer.getCPSR().setN(arg2.getData() < 0);
            stateContainer.getCPSR().setZ(arg1.getData() == 0 && arg2.getData() == 0);
        }
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, Register arg2, Register arg3, Register arg4) {

    }
}
