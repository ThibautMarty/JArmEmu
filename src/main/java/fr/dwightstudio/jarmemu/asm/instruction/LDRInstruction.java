package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.asm.argument.*;
import fr.dwightstudio.jarmemu.asm.directive.WordDirective;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.MemoryAccessMisalignedASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class LDRInstruction extends ParsedInstruction<Register, AddressArgument.UpdatableInteger, Integer, ShiftArgument.ShiftFunction> implements PseudoInstruction {
    private static final Pattern PSEUDO_OP_PATTERN = Pattern.compile("=(?<VALUE>[^\n\\[\\]\\{\\}]+)");

    public LDRInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<AddressArgument.UpdatableInteger>> getParsedArg2Class() {
        return AddressArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg3Class() {
        return ImmediateOrRegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<ShiftArgument.ShiftFunction>> getParsedArg4Class() {
        return ShiftArgument.class;
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
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, AddressArgument.UpdatableInteger arg2, Integer arg3, ShiftArgument.ShiftFunction arg4) throws ExecutionASMException {
        int i1 = arg4.apply(arg3);
        int address = arg2.toInt() + i1;

        if (!forceExecution) {
            int dataLength;

            switch (dataMode) {
                case HALF_WORD -> dataLength = 2;
                case BYTE -> dataLength = 1;
                case null, default -> dataLength = 4;
            }

            if (address % dataLength != 0) throw new MemoryAccessMisalignedASMException();
        }

        switch (dataMode) {
            case null -> arg1.setData(stateContainer.getMemory().getWord(address));
            case HALF_WORD -> arg1.setData(stateContainer.getMemory().getHalf(address));
            case BYTE -> arg1.setData(stateContainer.getMemory().getByte(address));
        }

        arg2.update();
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, AddressArgument.UpdatableInteger arg2, Integer arg3, ShiftArgument.ShiftFunction arg4) {

    }

    @Override
    public boolean isPseudoInstruction() {
        return ((AddressArgument) arg2).isPseudoInstruction();
    }

    @Override
    public void allocate(StateContainer container) {
        container.getCurrentFilePos().incrementPos(4);
    }

    @Override
    public ParsedObject generate(StateContainer container) throws ASMException {
        MatchResult matchResult = PSEUDO_OP_PATTERN.matcher(arg2.getOriginalString()).toMatchResult();
        int value = container.evalWithAccessible(matchResult.group("VALUE"));
        WordDirective dir = new WordDirective(Section.RODATA, Integer.toString(value));
        dir.setGenerated();
        dir.setLineNumber(this.getLineNumber());
        return dir;
    }
}
