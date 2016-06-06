# encoding= utf-8

##
# Sikuli_01.
# <p>
# Small demonstration of a test using Selenium.<br/>
# During this test, a Firefox session is launched on UBUNTU, a google search about qtaste is done.
# The test will try to go on the qtaste website and then close the opened tab.
#
##

from qtaste import *
import os, time

IMAGE_DIRECTORY = os.getcwd() + "/TestSuites/Sikuli/images"
logger.info("directory : " + IMAGE_DIRECTORY)

sikuli = testAPI.getSikuli(INSTANCE_ID="Sikuli")

def isFirefoxInstalled():
    """
    @step      Check if Mozilla Firefox is installed on the machine
    @expected  The Mozilla Firefox icon is displayed.
    """
    if not sikuli.exist(IMAGE_DIRECTORY + "/MozillaFirefox.png"):
        testAPI.stopTest(Status.FAIL, "Mozilla Firefox is required for this test!")
    
def clickOnIcon():
    """
    @step      Start Mozilla Firefox by clicking on the icon.
    @expected  The search bar is displayed.
    """
    sikuli.click(IMAGE_DIRECTORY + "/MozillaFirefox.png")
    sikuli.wait(IMAGE_DIRECTORY + "/url_bar.png", 10)

def searchForQtasteSite():
    """
    @step      Search for qtaste on Google.
    @expected  the Google initial search page is no more displayed.
    """
    sikuli.click(IMAGE_DIRECTORY + "/url_bar.png")
    sikuli.type(IMAGE_DIRECTORY + "/url_bar.png", "qtaste \n")
    sikuli.waitVanish(IMAGE_DIRECTORY + "/search_field.png")

def goToQtasteSite():
    """
    @step      Click on the QTaste link within the search result.
    @expected  The QTaste page is displayed.
    """
    sikuli.click(IMAGE_DIRECTORY + "/qtaste_link.png")
    time.sleep(10)
    sikuli.waitVanish(IMAGE_DIRECTORY + "/qtaste_link.png")
    sikuli.wait(IMAGE_DIRECTORY + "/qtaste_logo.png")

def closeTab():
    """
    @step      Close the tab by clicking on the little cross within the tab.
    @expected  The QTaste web page is no more displayed.
    """
    sikuli.click(IMAGE_DIRECTORY + "/close_tab.png")
    sikuli.waitVanish(IMAGE_DIRECTORY + "/qtaste_logo.png")

doStep(isFirefoxInstalled)
doStep(clickOnIcon)
doStep(searchForQtasteSite)
doStep(goToQtasteSite)
doStep(closeTab)
