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

package com.qspin.qtaste.ui.testcampaign;

import java.io.File;
import java.util.ArrayList;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.ui.tools.FileMask;
import com.qspin.qtaste.util.FileUtilities;

/**
 * @author vdubois
 */
public class MetaCampaignFile {

    private File file;
    private String campaignName;

    static public MetaCampaignFile[] getExistingCampaigns() {
        ArrayList<MetaCampaignFile> campaignArray = new ArrayList<MetaCampaignFile>();
        File campaignDir = new File(StaticConfiguration.CAMPAIGN_DIRECTORY);
        FileMask fileMask = new FileMask();
        fileMask.addExtension(StaticConfiguration.CAMPAIGN_FILE_EXTENSION);
        File[] fCampaignList = FileUtilities.listSortedFiles(campaignDir, fileMask);
        for (int i = 0; i < fCampaignList.length; i++) {
            // remove the extension
            String campaignName = fCampaignList[i].getName().substring(0, fCampaignList[i].getName().lastIndexOf("."));
            campaignArray.add(new MetaCampaignFile(campaignName));
        }
        return campaignArray.toArray(new MetaCampaignFile[0]);
    }

    public MetaCampaignFile(String fileName) {
        file = new File(StaticConfiguration.CAMPAIGN_DIRECTORY + File.separator + fileName + "."
              + StaticConfiguration.CAMPAIGN_FILE_EXTENSION);
        campaignName = fileName;
    }

    public String toString() {
        return getCampaignName();
    }

    public String getCampaignName() {
        return campaignName;
    }

    public String getFileName() {
        return file.getPath();
    }

    public File getFile() {
        return file;
    }

    public boolean renameFile(String campaign) {
        boolean ret = false;
        if (file == null) {
            return ret;
        }

        File newFile = new File(StaticConfiguration.CAMPAIGN_DIRECTORY +
              File.separator + campaign + "." + StaticConfiguration.CAMPAIGN_FILE_EXTENSION);
        ret = file.renameTo(newFile);
        if (ret) {
            file = newFile;
            campaignName = campaign;
        }

        return ret;
    }

    public boolean removeFile() {
        if (file == null) {
            return false;
        }
        if (FileUtilities.deleteFile(file)) {
            campaignName = "";
            return true;
        }

        return false;
    }

}
