# encoding= utf-8

#    Copyright 2007-2009 QSpin - www.qspin.be
#
#    This file is part of QTaste framework.
#
#    QTaste is free software: you can redistribute it and/or modify
#    it under the terms of the GNU Lesser General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    QTaste is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU Lesser General Public License for more details.
#
#    You should have received a copy of the GNU Lesser General Public License
#    along with QTaste. If not, see <http://www.gnu.org/licenses/>.

##
# TestBugzilla - add a new record in the bugzilla database.
# <p>
# Script requests to add defects using the bugzilla web interface and the specified web browser and checks that the information are introduced correctly in the database.
# @preparation The bugzilla server has to be UP
# @data BROWSER [String] The selenium browser identifier
# @data BUGZILLA_LOGIN [String]  The bugzilla login ID
# @data BUGZILLA_PASSWORD [String] The bugzilla password
# @data SHORT_DESCRIPTION [String] The short description of the bug
# @data LONG_DESCRIPTION [String] The long description of the bug
# @data ASSIGNEE [String] The person that will be assigned to the bug
##

from qtaste import *

selenium = testAPI.getSelenium(INSTANCE_ID='BugzillaApp')
bugzilla = testAPI.getBugzilla(INSTANCE_ID='BugzillaServer')

defectId = 0
bugzillaLogin = testData.getValue('BUGZILLA_LOGIN')
bugzillaPassword = testData.getValue('BUGZILLA_PASSWORD')
shortDescription = testData.getValue("SHORT_DESCRIPTION")
longDescription = testData.getValue("LONG_DESCRIPTION")
assignee = testData.getValue("ASSIGNEE")

def connectToBugzilla():
    """
    @step      Connection to Bugzilla website
    @expected  Check that we are connected to the Buzilla website
    """
    logger.info("Log into bugzilla using %s/%s" %(bugzillaLogin, bugzillaPassword))
    selenium.openBrowser(testData.getValue("BROWSER"))
    selenium.windowMaximize()
    selenium.windowFocus()
    selenium.open("/cgi-bin/bugzilla3/index.cgi")
    # log out if bugzilla is already logged in
    if selenium.isElementPresent("link=Log out"):
        logger.info("Warning: Bugzilla was already logged in")
        disconnectFromBugzilla()
        selenium.click("link=Log in again.")
        selenium.waitForPageToLoad("30000")
    selenium.type("Bugzilla_login", bugzillaLogin)
    selenium.type("Bugzilla_password", bugzillaPassword)
    selenium.click("log_in")
    selenium.waitForPageToLoad("30000")

def createRecord():
    """
    @step      Create a new defect using the web interface
    @expected  The defect has been created using the web interface
    """
    global defectId
    selenium.click("link=New")
    selenium.waitForPageToLoad("30000")
    selenium.click("link=QSpin development environment")
    selenium.waitForPageToLoad("30000")
    selenium.select("component", "label=Bugzilla")
    selenium.type("assigned_to", assignee)
    selenium.type("short_desc", shortDescription)
    selenium.type("comment", longDescription)
    selenium.click("commit")
    selenium.waitForPageToLoad("30000")
    defectId = int(selenium.getText("//div[2]/dl/dt/a").replace('Bug ', ''))
    logger.info("defectId: %d" %defectId)

def checkDatabaseRecord():
    """
    @step      Retrieve the defect from the database and check the content
    @expected  The content correspond to the data introduced with the web interface
    """
    bugzilla.checkDatabase(defectId, shortDescription, longDescription, assignee)

def disconnectFromBugzilla():
    """
    @step      Disconnect from bugzilla
    @expected  The session should be closed
    """
    selenium.click("link=Log out")
    selenium.waitForPageToLoad("30000")
    selenium.closeBrowser()

doStep(connectToBugzilla)
doStep(createRecord)
doStep(checkDatabaseRecord)
doStep(disconnectFromBugzilla)
