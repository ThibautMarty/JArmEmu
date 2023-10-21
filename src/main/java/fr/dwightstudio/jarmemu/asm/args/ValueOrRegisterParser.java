package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;

// Correspond à "arg"
public class ValueOrRegisterParser implements ArgumentParser<ValueOrRegisterParser.ValueView> {
    @Override
    public ValueOrRegisterParser.ValueView parse(StateContainer stateContainer, String string) {
        return null;
    }

    public static final class ValueView {
        private Register register;
        private int shift;
        private byte value;


        public ValueView(Register register, int shift) {
            this.register = register;
            this.shift = shift;
        }

        public ValueView(byte value) {
            this.register = null;
            this.value = value;
        }

        byte[] value() {
            if (this.register == null) {
                return new byte[]{value};
            } else {
                return register.getData();
            }
        }
    }
}
