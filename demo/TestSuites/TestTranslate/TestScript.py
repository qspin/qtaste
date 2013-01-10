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
# TestTranslate.
# <p>
# Perform a translation of words defined in testdata and check the result of the translation using multiple browser.
# This demo required the "demo_web" testbed configuration
#
##

from qtaste import *
import time
import string

translate = testAPI.getSelenium(INSTANCE_ID='TranslateApp')

def connectToWeb():
	"""
	@step      Connection to Yahoo babel fish text translation website
	@expected  Check that we are on the Yahoo babel fish website
	"""
	translate.openBrowser(testData.getValue("BROWSER"))
	translate.setTimeout("50000")
	translate.open("?hl=fr")
	translate.waitForPageToLoad("15000")
	title = translate.getTitle()
	expected = "Google"+chr(160)+"Traduction"
	if title != expected:
		testAPI.stopTest(Status.FAIL, "Title window name is not as expected. It's '" + title + "' and expects '"+expected+"'" )

def checkTranslation():
	"""
	@step      Translate a word specified in the testdata
	@expected  Check that the translation is correct
	"""
	# we can access component using different method (component id, xpath or dom)
	translate.type("id=source", testData.getValue("WORD"))
	time.sleep(2);
	translations = translate.getText("id=result_box")
	expectedTranslation = testData.getValue("TRANSLATION")
	translate.closeBrowser()
	found = False
	for translation in translations.split("\n"):
		if translation == expectedTranslation:
			found = True
			break
	if ( found == False ):
		testAPI.stopTest(Status.FAIL, "Expected to get " + expectedTranslation + " but got " + translations)

doStep(connectToWeb)
doStep(checkTranslation)
