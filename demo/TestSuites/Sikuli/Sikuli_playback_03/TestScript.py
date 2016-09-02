# coding=utf-8

##
# Sikuli_playback_03.
# <p>
# This test will validate the findAll verb and the Area object.
#
##

import os

from qtaste import *

# IMAGE FILE PATH DEFINITION
IMAGE_DIRECTORY = os.getcwd() + "/TestSuites/Sikuli/playback_img"
DOCUMENT_TAB = IMAGE_DIRECTORY + "/document_panel_tab.png"
TEXTFIELD_COMPS = IMAGE_DIRECTORY + "/textfields.png"

sikuli = testAPI.getSikuli()
javagui = testAPI.getJavaGUI(INSTANCE_ID="Playback")


def select_tab_document():
    """
    @step      Through sikuli, we will simulate a click on the "document_panel" tab 
    @expected  The text field within the "document_panel" tab is displayed.
    """
    javagui.selectTab("TABBED_PANE", -1)
    sikuli.find(DOCUMENT_TAB).click()
    if not javagui.isVisible("TEXT_FIELD"):
        testAPI.stopTest(Status.FAIL, "no text field displayed")
    pass

def write_in_text_fields():
    """
    @step Finds the text fiels, the formatted text field and the password field and write "123" inside.
    @expected All fields contain "123"
    """
    text = "azerty"
    for area in sikuli.findAll(TEXTFIELD_COMPS):
        # click on the field to set the focus inside
        area.click()
        area.write(text)

    if javagui.getText("TEXT_FIELD") != text:
        testAPI.stopTest(Status.FAIL, "failed to write inside the text field")
    if javagui.getText("FORMATTED_TEXT_FIELD") != "":
        testAPI.stopTest(Status.FAIL, "failed to write inside the formatted text field")
    if javagui.getText("PASSWORD_FIELD") != text:
        testAPI.stopTest(Status.FAIL, "failed to write inside the password field")

doStep(select_tab_document)
doStep(write_in_text_fields)
