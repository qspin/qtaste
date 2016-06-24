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

package com.qspin.qtaste.reporter;

import java.util.ArrayList;
import java.util.Date;

/**
 * The class that will extends a report manager is responsible for:
 * - keep track of the formatters of the report
 * - maintain the content (results) of the report
 *
 * @author lvboque
 */
public abstract class ReportManager {
    protected String reportName;
    protected ArrayList<ReportFormatter> formatters = new ArrayList<ReportFormatter>();

    public ReportManager() {
        formatters = new ArrayList<ReportFormatter>();
    }

    public void startReport(Date timeStamp, String name) {
        this.reportName = name;
        for (ReportFormatter formatter : formatters) {
            formatter.startReport(timeStamp, name);
        }
    }

    public String getReportName() {
        return this.reportName;
    }

    public void stopReport() {
        for (ReportFormatter formatter : formatters) {
            formatter.stopReport();
        }
        reportName = null;
    }

    public void refresh() {
        for (ReportFormatter formatter : formatters) {
            formatter.refresh();
        }
    }
}
