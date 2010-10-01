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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qspin.qtaste.ui.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author vdubois
 */
public class FileSearch {

    private ArrayList<String> searchPaths;
    public FileSearch() {
        searchPaths = new ArrayList<String>();
    }
    public void addSearchPath(String path) {
        searchPaths.add(path);
    }
    public String getFirstFileFound(String fileName) {
        Iterator<String> it = searchPaths.iterator();
        while (it.hasNext()) {
            String path = it.next();
            File file = new File(path + File.separator + fileName);
            if (file.exists())
                return path + File.separator + fileName;
        }
        return null;
    }
    
}
