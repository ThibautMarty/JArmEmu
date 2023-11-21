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

package fr.dwightstudio.jarmemu.gui.factory;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.util.converters.ASCIIStringConverter;
import fr.dwightstudio.jarmemu.util.converters.BinStringConverter;
import fr.dwightstudio.jarmemu.util.converters.HexStringConverter;
import fr.dwightstudio.jarmemu.util.converters.ValueStringConverter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ValueTableCell<S> extends TextFieldTableCell<S, Number> {

    private ValueTableCell(JArmEmuApplication application) {
        super(new ValueStringConverter(application));
        this.getStyleClass().add("data-value");
    }

    private ValueTableCell(StringConverter<Number> converter) {
        super(converter);
        this.getStyleClass().add("data-value");
    }


    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryDynamicFormat(JArmEmuApplication application) {
        return (val) -> new ValueTableCell<>(application);
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticHex() {
        return (val) -> new ValueTableCell<>(new HexStringConverter());
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticBin() {
        return (val) -> new ValueTableCell<>(new BinStringConverter());
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticASCII() {
        return (val) -> new ValueTableCell<>(new ASCIIStringConverter());
    }
}
