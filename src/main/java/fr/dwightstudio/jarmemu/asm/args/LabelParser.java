package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm24" TODO: Prendre en compte les nombres
public class LabelParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        string = string.substring(0, string.length()-1);
        Integer value = stateContainer.labels.get(string);
        if (value == null) throw new SyntaxASMException("Unknown label '" + string +"'");

        return value;
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing label identifier or constant");
    }

}
