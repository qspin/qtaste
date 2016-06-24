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

package com.qspin.qtaste.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author lvboque
 */
public class ObjectFile extends File {

    private static final long serialVersionUID = -326049015728080117L;

    public ObjectFile(String f) {
        super(f);
    }

    public void save(Serializable s) throws Exception {
        FileOutputStream f_out = new FileOutputStream(this);
        ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
        obj_out.writeObject(s);
        obj_out.flush();
        f_out.close();
    }

    public Serializable load() throws Exception {
        FileInputStream f_in = new FileInputStream(this);
        ObjectInputStream obj_in = new ObjectInputStream(f_in);
        Serializable ser = (Serializable) obj_in.readObject();
        f_in.close();
        return ser;
    }
}
