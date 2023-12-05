/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.parse.legacy.LegacyDirectiveParser;
import fr.dwightstudio.jarmemu.sim.parse.legacy.LegacySectionParser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.logging.Logger;

public class LegacySourceParser implements SourceParser {

    private final Logger logger = Logger.getLogger(getClass().getName());
    protected Instruction instruction;
    protected boolean updateFlags;
    protected SourceScanner sourceScanner;
    protected LegacySectionParser legacySectionParser;
    protected LegacyDirectiveParser legacyDirectiveParser;

    protected DataMode dataMode;
    protected UpdateMode updateMode;
    protected Condition conditionExec;
    protected String currentLine;
    protected String instructionString;
    protected ArrayList<String> arguments;
    protected Section section;
    protected Section currentSection;
    protected ParsedObject directive;
    protected String label;

    /**
     * Création d'un parseur vide
     */
    public LegacySourceParser() {
        this.legacySectionParser = new LegacySectionParser();
        this.legacyDirectiveParser = new LegacyDirectiveParser();

        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.conditionExec = Condition.AL;
        this.currentLine = "";
        this.instructionString = "";
        this.arguments = new ArrayList<>();
        this.section = null;
        this.currentSection = Section.NONE;
        this.directive = null;
        this.label = "";
    }

    /**
     * Création du lecteur de code du fichier *.s
     * @param file Le fichier
     * @throws FileNotFoundException Exception si le fichier n'est pas trouvé
     */
    public LegacySourceParser(File file) throws IOException {
        this();

        this.sourceScanner = new SourceScanner(file);
    }

    /**
     * Création du lecteur de code de l'éditeur
     * @param sourceScanner le lecteur de source utilisé
     */
    public LegacySourceParser(SourceScanner sourceScanner) {
        this();

        this.sourceScanner = sourceScanner;
    }

    /**
     * Définie la liste des fichiers
     *
     * @param source le SourceScanner utilisé
     */
    @Override
    public void setSource(SourceScanner source) {
        this.sourceScanner = source;
    }

    /**
     * @return la ligne actuellement interprétée
     */
    public int getCurrentLine() {
        return sourceScanner.getCurrentInstructionValue();
    }

    /**
     * Retire le commentaire de la ligne s'il y en a un
     * @param line La ligne sur laquelle on veut appliquer la fonction
     * @return La ligne modifiée ou non
     */
    public String removeComments(@NotNull String line){
        return line.split("@")[0];
    }

    /**
     * Retire les espaces blancs avant et après l'instruction
     * @param line La ligne sur laquelle on veut appliquer la fonction
     * @return La ligne modifiée ou non
     */
    public String removeBlanks(@NotNull String line){
        return line.strip();
    }

    /**
     * Retire les drapeaux S, H, B et les "update modes"
     * @param instructionString La ligne à modifier
     * @return La ligne modifiée ou non
     */
    public String removeFlags(@NotNull String instructionString){
        if (instructionString.endsWith("S") && (instructionString.length()%2==0)){
            updateFlags = true;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.endsWith("H")) {
            dataMode = DataMode.HALF_WORD;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.endsWith("B") && ((!instructionString.equals("SUB") && !instructionString.equals("RSB") && !instructionString.equals("B")))) {
            dataMode = DataMode.BYTE;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.length()==7 || instructionString.length()==5) {
            UpdateMode[] updateModes = UpdateMode.values();
            for (UpdateMode updatemode:updateModes) {
                if (instructionString.endsWith(updatemode.toString().toUpperCase())) {
                    updateMode = updatemode;
                    instructionString = instructionString.substring(0, instructionString.length()-2);
                }
            }
        }
        return instructionString;
    }

    /**
     * Retire les conditions d'exécution
     * @param instructionString La ligne à modifier
     * @return La ligne modifiée ou non
     */
    public String removeCondition(String instructionString){
        Condition[] conditions = Condition.values();
        for (Condition condition:conditions) {
            if (instructionString.endsWith(condition.toString().toUpperCase())){
                if (instructionString.endsWith("MLAL")) continue;
                if (instructionString.endsWith("TEQ")) continue;

                this.conditionExec = condition;
                instructionString = instructionString.substring(0, instructionString.length()-2);
            }
        }
        return instructionString;
    }

    /**
     * Méthode principale
     * Lecture du fichier et renvoie des instructions parsées à verifier
     */
    public ParsedFile parse(){
        TreeMap<Integer, ParsedObject> rtn = new TreeMap<>();

        sourceScanner.goTo(-1);
        while (this.sourceScanner.hasNextLine()){
            ParsedObject inst = parseOneLine();
            if (inst != null) rtn.put(sourceScanner.getCurrentInstructionValue(), inst);
        }

        return new ParsedFile(sourceScanner, rtn);
    }

    /**
     * Lecture d'une ligne
     */
    public void readOneLineASM() {
        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.dataMode = null;
        this.section = null;
        this.directive = null;
        this.label = "";
        this.conditionExec = Condition.AL;
        this.arguments.clear();

        currentLine = this.sourceScanner.nextLine();
        currentLine = this.removeComments(currentLine);
        currentLine = this.removeBlanks(currentLine);

        if (!currentLine.isEmpty()){
            Section section = this.legacySectionParser.parseOneLine(currentLine);
            if (section != null) this.section = section;

            ParsedObject directives = this.legacyDirectiveParser.parseOneLine(sourceScanner, currentLine, currentSection);
            if (directives != null) this.directive = directives;

            if (currentSection == Section.TEXT){
                if (currentLine.contains(":")){
                    this.label = currentLine.substring(0, currentLine.indexOf(":")).strip().toUpperCase();
                    currentLine = currentLine.substring(currentLine.indexOf(":")+1).strip();
                }
                if(!currentLine.isEmpty()){
                    instructionString = currentLine.split(" ")[0].toUpperCase();
                    int instructionLength = instructionString.length();
                    instructionString = this.removeFlags(instructionString);
                    instructionString = this.removeCondition(instructionString);

                    Instruction[] instructions = Instruction.values();
                    for (Instruction instruction:instructions) {
                        if(instruction.toString().toUpperCase().equals(instructionString)) this.instruction = instruction;
                    }

                    if (this.instruction == null) throw new SyntaxASMException("Unknown instruction '" + instructionString + "'");

                    if (currentLine.contains("{")) {
                        StringBuilder argument = new StringBuilder(currentLine.substring(instructionLength).split(",", 2)[1].strip());
                        argument.deleteCharAt(0);
                        argument.deleteCharAt(argument.length() - 1);
                        ArrayList<String> argumentArray = new ArrayList<>(Arrays.asList(argument.toString().split(",")));
                        argumentArray.replaceAll(String::strip);
                        argument = new StringBuilder(currentLine.substring(instructionLength).split(",")[0].strip() + "," + "{");
                        for (String arg : argumentArray) {
                            arg = this.joinString(arg);
                            argument.append(arg).append(",");
                        }
                        argument.deleteCharAt(argument.length() - 1);
                        argument.append("}");
                        this.arguments.addAll(Arrays.asList(argument.toString().split(",", 2)));
                        this.arguments.replaceAll(String::strip);
                    } else if (currentLine.contains("[")) {
                        StringBuilder argument = new StringBuilder(currentLine.split("\\[")[1]);
                        boolean toUpdate = argument.toString().strip().charAt(argument.toString().strip().length() - 1) == '!';
                        argument = new StringBuilder(argument.substring(0, argument.length() - 1));
                        if (argument.toString().split("]").length==2){
                            this.arguments.addAll(Arrays.asList(currentLine.substring(instructionLength).split(",")));
                            this.arguments.replaceAll(String::strip);
                            this.arguments = this.joinStringArray(this.arguments);
                        } else {
                            argument = new StringBuilder(argument.toString().split("]")[0]);
                            ArrayList<String> argumentArray = new ArrayList<>(Arrays.asList(argument.toString().split(",")));
                            argumentArray.replaceAll(String::strip);
                            argument = new StringBuilder(currentLine.substring(instructionLength).split(",")[0].strip() + "," + "[");
                            for (String arg : argumentArray) {
                                arg = this.joinString(arg);
                                argument.append(arg).append(",");
                            }
                            argument.deleteCharAt(argument.length() - 1);
                            argument.append("]");
                            if (toUpdate) argument.append("!");
                            this.arguments.addAll(Arrays.asList(argument.toString().split(",", 2)));
                            this.arguments.replaceAll(String::strip);
                        }
                    } else {
                        this.arguments.addAll(Arrays.asList(currentLine.substring(instructionLength).split(",")));
                        this.arguments.replaceAll(String::strip);
                    }

                    if (arguments.size() > 4) throw new SyntaxASMException("Invalid instruction '" + currentLine + "' (too many arguments");
                }
            }
        }
    }

    /**
     * Supprime les espaces composant le String
     * @param argument Le String
     * @return Le String avec les espaces en moins
     */
    private String joinString(String argument) {
        StringBuilder newArg = new StringBuilder();
        ArrayList<String> elements = new ArrayList<>(Arrays.asList(argument.split(" ")));
        for (String ele:elements) {
            newArg.append(ele);
        }
        return String.valueOf(newArg);
    }

    /**
     * Supprime les espaces des Strings composants une ArrayList<String>
     * @param arguments Une ArrayList<String>
     * @return L'ArrayList avec les espaces en moins
     */
    private ArrayList<String> joinStringArray(ArrayList<String> arguments) {
        ArrayList<String> returnString = new ArrayList<>();
        for (String arg:arguments) {
            StringBuilder newArg = new StringBuilder();
            ArrayList<String> elements = new ArrayList<>(Arrays.asList(arg.split(" ")));
            for (String ele:elements) {
                newArg.append(ele);
            }
            returnString.add(String.valueOf(newArg));
        }
        return returnString;
    }

    /**
     * Lecture d'une ligne et teste de tous ses arguments
     *
     * @return un ParsedObject non vérifié
     */
    public ParsedObject parseOneLine() {
        try {
            readOneLineASM();
        } catch (SyntaxASMException ignored) {}

        if (this.section != null) {
            this.currentSection = this.section;
            return null;
        }

        if (this.directive != null) return this.directive;

        String arg1 = null;
        String arg2 = null;
        String arg3 = null;
        String arg4 = null;

        if (!this.label.isEmpty()) {
            try {
                arg1 = arguments.get(0);
                arg2 = arguments.get(1);
                arg3 = arguments.get(2);
                arg4 = arguments.get(3);
            } catch (IndexOutOfBoundsException ignored) {}
            if (instruction == null) {
                return new ParsedLabel(this.label);
            } else {
                return new ParsedLabel(this.label).withInstruction(new ParsedInstruction(instruction, conditionExec, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4));
            }
        }

        try {
            arg1 = arguments.get(0);
            arg2 = arguments.get(1);
            arg3 = arguments.get(2);
            arg4 = arguments.get(3);
        } catch (IndexOutOfBoundsException ignored) {}

        if (instruction == null) return null;
        return new ParsedInstruction(instruction, conditionExec, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }
}
