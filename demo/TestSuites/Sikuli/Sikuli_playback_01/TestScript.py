# coding=utf-8

##
# Sikuli_playback_01.
# <p>
# This test will validate the exists and the click verbs of the sikuli API.
#
##

import os

from qtaste import *

# IMAGE FILE PATH DEFINITION
IMAGE_DIRECTORY = os.getcwd() + "/TestSuites/Sikuli/playback_img"
SELECTION_TAB = IMAGE_DIRECTORY + "/selection_panel_tab.png"
DOCUMENT_TAB = IMAGE_DIRECTORY + "/document_panel_tab.png"
TEXTFIELD_COMP = IMAGE_DIRECTORY + "/textfield.png"

sikuli = testAPI.getSikuli()
javagui = testAPI.getJavaGUI(INSTANCE_ID="Playback")

def select_tab_selection():
    """
    @step      Through sikuli, we will simulate a click on the "selection_panel" tab 
    @expected  The combobox within the "selection_panel" tab is displayed.
    """
    sikuli.click(SELECTION_TAB)
    if sikuli.exists(TEXTFIELD_COMP) or not javagui.isVisible("COMBO_BOX"):
        testAPI.stopTest(Status.FAIL, "no combo box displayed")
    pass

def select_tab_document():
    """
    @step      Through sikuli, we will simulate a click on the "document_panel" tab 
    @expected  The text field within the "document_panel" tab is displayed.
    """
    sikuli.click(DOCUMENT_TAB)
    if not sikuli.exists(TEXTFIELD_COMP):
        testAPI.stopTest(Status.FAIL, "no text field displayed")
    pass

doStep(select_tab_document)
doStep(select_tab_selection)
