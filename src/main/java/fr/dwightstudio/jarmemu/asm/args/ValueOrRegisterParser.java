package fr.dwightstudio.jarmemu.asm.args;

import java.util.function.Supplier;

public class ValueOrRegisterParser implements ArgumentParser {
    @Override
    public int parse(String string) {
        return 0; // Nombre sur 8 bits, penser à faire les shifts
    }
}
