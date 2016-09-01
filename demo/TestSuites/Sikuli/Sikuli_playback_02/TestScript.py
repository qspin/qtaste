# coding=utf-8

##
# Sikuli_playback_01.
# <p>
# This test will validate the hover verb of the sikuli API.
#
##

import os
import time

from qtaste import *

# IMAGE FILE PATH DEFINITION
IMAGE_DIRECTORY = os.getcwd() + "/TestSuites/Sikuli/playback_img"
DOCUMENT_TAB = IMAGE_DIRECTORY + "/document_panel_tab.png"
TEXTFIELD_COMP = IMAGE_DIRECTORY + "/textfield.png"
TEXTFIELD_TOOLTIP = IMAGE_DIRECTORY + "/textfield_tooltip.png"

sikuli = testAPI.getSikuli()
javagui = testAPI.getJavaGUI(INSTANCE_ID="Playback")

def select_tab_document():
    """
    @step      Through sikuli, we will simulate a click on the "document_panel" tab 
    @expected  The text field within the "document_panel" tab is displayed.
    """
    sikuli.click(DOCUMENT_TAB)
    sikuli.hover(TEXTFIELD_COMP)
    time.sleep(1.5)
    if not sikuli.exists(TEXTFIELD_TOOLTIP):
        testAPI.stopTest(testAPI.FAIL, "no tooltip displayed")
    pass

doStep(select_tab_document)
