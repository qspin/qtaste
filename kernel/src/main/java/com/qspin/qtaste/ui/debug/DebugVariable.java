/*
    Copyright 2007-2009 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

package com.qspin.qtaste.ui.debug;

import java.util.ArrayList;

public class DebugVariable {

    private ArrayList<DebugVariable> mFieldList;
    private String m_Value;
    private String m_Type;
    private String m_VarName;

    public ArrayList<DebugVariable> getFieldList() {
        return mFieldList;
    }

    public void setValue(String value) {
        this.m_Value = value;
    }

    public String getValue() {
        return m_Value;
    }

    public void setVariableType(String type) {
        this.m_Type = type;
    }

    public String getType() {
        return m_Type;
    }

    public DebugVariable(String variableName, String variableType, String value) {
        m_Value = value;
        m_Type = variableType;
        m_VarName = variableName;
        mFieldList = new ArrayList<>();
    }

    public ArrayList<DebugVariable> getFields() {
        return mFieldList;
    }

    public void addField(DebugVariable variableField) {
        mFieldList.add(variableField);
    }

    public void setVarName(String varName) {
        this.m_VarName = varName;
    }

    public String getVarName() {
        return m_VarName;
    }

}
