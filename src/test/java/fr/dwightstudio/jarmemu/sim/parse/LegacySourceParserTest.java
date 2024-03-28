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

package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.asm.parser.legacy.LegacySourceParser;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNull;

public class LegacySourceParserTest extends JArmEmuTest {

    StateContainer container;

    @BeforeEach
    public void setup() {
        container = new StateContainer();
    }

    @Test
    public void TestFormatLine() throws URISyntaxException, IOException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/singleLine.s")).toURI());

        LegacySourceParser parser = new LegacySourceParser(new SourceScanner(file, 0));
        parser.currentSection = Section.TEXT;

        assertEquals(
                new ParsedInstruction(OInstruction.ADD, Condition.AL, false, null, null, "R1", "R0", null, null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.ADC, Condition.CC, true, null, null, "R2", "R1", "R3", null, 0),
                parser.parseOneLine()
        );

    }

    @Test
    public void TestReadInstruction() throws URISyntaxException, IOException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/normalLine.s")).toURI());

        LegacySourceParser parser = new LegacySourceParser(new SourceScanner(file, 0));
        parser.currentSection = Section.TEXT;

        assertEquals(
                new ParsedInstruction(OInstruction.LDR, Condition.AL, false, null, null, "R1", "[R2]", null, null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.ADD, Condition.CC, false, null, null, "R1", "[R2]", null, null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.ADD, Condition.EQ, false, DataMode.BYTE, null, "R1", "[R2]", null, null, 0),
                parser.parseOneLine()
        );

    }

    @Test
    public void TestReadInstructionSub() throws URISyntaxException, IOException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/subLine.s")).toURI());

        LegacySourceParser parser = new LegacySourceParser(new SourceScanner(file, 0));
        parser.currentSection = Section.TEXT;

        assertEquals(
                new ParsedInstruction(OInstruction.SUB, Condition.AL, false, null, null, "r2", "r0", "r1", null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.SUB, Condition.AL, false, null, null, "r0", "r1", null, null, 0),
                parser.parseOneLine()
        );
    }

    @Test
    public void TestReadInstructionComplexer() throws URISyntaxException, IOException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/multipleLines.s")).toURI());

        LegacySourceParser parser = new LegacySourceParser(new SourceScanner(file, 0));
        parser.currentSection = Section.TEXT;

        assertEquals(
                new ParsedInstruction(OInstruction.ADD, Condition.CC, true, null, null, "r0", "r9", "#2", null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.MLA, Condition.EQ, false, null, null, "r0", "r0", "r1", "r2", 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.SMLAL, Condition.AL, true, null, null, "r4", "r5", "r6", "r7", 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.BIC, Condition.LO, false, null, null, "r5", "r6", "#5", null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.LDR, Condition.AL, false, DataMode.BYTE, null, "r0", "=x", null, null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.STM, Condition.AL, false, null, UpdateMode.FD, "sp!", "{r0,r1,r2}", null, null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.B, Condition.AL, false, null, null, "etiquette", null, null, null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedLabel("CECIESTUNEETIQUETTE"),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.LDR, Condition.AL, false, null, null, "R1", "[R0,R1,LSL#2]", null, null, 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.LDR, Condition.AL, false, null, null, "R1", "[R0]", "R1" , "LSL#2", 0),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(OInstruction.STR, Condition.AL, false, null, null, "fp", "[sp,#-4]!", null , null, 0),
                parser.parseOneLine()
        );
    }

    @Test
    public void TestReadDirectives() throws URISyntaxException, IOException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/directiveMultipleLinesLegacy.s")).toURI());

        LegacySourceParser parser = new LegacySourceParser(new SourceScanner(file, 0));

        ParsedDirectivePack parsedDirectivePack;
        parser.parseOneLine();
        assertEquals(
                Section.BSS,
                parser.currentSection
        );

        parser.parseOneLine();
        assertEquals(
                Section.DATA,
                parser.currentSection
        );

        assertEquals(
                new ParsedDirective(Directive.GLOBAL, "ExEC", Section.DATA),
                parser.parseOneLine()
        );

        parser.currentSection = Section.TEXT;
        assertEquals(
                new ParsedLabel("A"),
                parser.parseOneLine()
        );

        parser.currentSection = Section.DATA;
        parsedDirectivePack = new ParsedDirectivePack();
        parsedDirectivePack.add(new ParsedDirectiveLabel("b", Section.DATA));
        parsedDirectivePack.add(new ParsedDirective(Directive.WORD, "3", Section.DATA));
        assertEquals(
                parsedDirectivePack.close(),
                parser.parseOneLine()
        );

        parsedDirectivePack = new ParsedDirectivePack();
        parsedDirectivePack.add(new ParsedDirective(Directive.BYTE, "'x'", Section.DATA));
        assertEquals(
                parsedDirectivePack.close(),
                parser.parseOneLine()
        );

        parsedDirectivePack = new ParsedDirectivePack();
        parsedDirectivePack.add(new ParsedDirective(Directive.GLOBAL, "Test", Section.DATA));
        assertEquals(
                parsedDirectivePack.close(),
                parser.parseOneLine()
        );

        parsedDirectivePack = new ParsedDirectivePack();
        parsedDirectivePack.add(new ParsedDirective(Directive.ASCII, "", Section.DATA));
        assertEquals(
                parsedDirectivePack.close(),
                parser.parseOneLine()
        );

        parsedDirectivePack = new ParsedDirectivePack();
        parsedDirectivePack.add(new ParsedDirective(Directive.ASCIZ, "\"\"", Section.DATA));
        assertEquals(
                parsedDirectivePack.close(),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedDirective(Directive.EQU, "laBEL, 'c'", Section.DATA),
                parser.parseOneLine()
        );

        parser.parseOneLine();
        assertEquals(
                Section.DATA,
                parser.currentSection
        );

        parser.parseOneLine();
        assertEquals(
                Section.COMMENT,
                parser.currentSection
        );

        assertNull(
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedDirective(Directive.ASCII, "\"Hey\"", Section.COMMENT),
                parser.parseOneLine()
        );

        parser.parseOneLine();
        assertEquals(
                Section.TEXT,
                parser.currentSection
        );

        assertEquals(
                new ParsedInstruction(OInstruction.LDR, Condition.AL, false, null, null, "R1", "=b", null, null, 0),
                parser.parseOneLine()
        );

        parser.parseOneLine();
        assertEquals(
                Section.END,
                parser.currentSection
        );
    }

}
