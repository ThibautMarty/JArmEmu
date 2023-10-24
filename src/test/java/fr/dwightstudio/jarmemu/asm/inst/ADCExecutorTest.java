package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ADCExecutorTest {

    private StateContainer stateContainer;
    private StateContainer stateContainerBis;
    private ADCExecutor adcExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainerBis = new StateContainer();
        adcExecutor = new ADCExecutor();
    }

    @Test
    public void simpleAddTest() {
        stateContainer.registers[0].setData(25);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(5);
        Register r2 = stateContainerBis.registers[2];
        r2.setData(20);
        adcExecutor.execute(stateContainerBis, false, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), stateContainerBis.registers[0].getData());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(1);
        adcExecutor.execute(stateContainerBis, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertFalse(stateContainerBis.cpsr.getN());
        assertTrue(stateContainerBis.cpsr.getZ());
        assertTrue(stateContainerBis.cpsr.getC());
        assertFalse(stateContainerBis.cpsr.getV());
        stateContainer.registers[0].setData(26);
        r1.setData(5);
        r2.setData(20);
        adcExecutor.execute(stateContainerBis, true, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), stateContainerBis.registers[0].getData());
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
    }

}
