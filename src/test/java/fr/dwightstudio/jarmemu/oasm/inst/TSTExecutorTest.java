/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.oasm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TSTExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private TSTExecutor tstExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        tstExecutor = new TSTExecutor();
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(0b110101);
        r1.setData(0b110101);
        tstExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), shift(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r0.setData(0b0011);
        r1.setData(0b1100);
        tstExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), shift(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(0b11111111111111111111111111111111);
        tstExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), shift(), null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
    }

}
