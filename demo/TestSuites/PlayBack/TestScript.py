##
# Demonstration of the playback agent.
# <p>
# A short demonstration to show how to control a swing interface with the playback agent..
#
##

from qtaste import *

import time

# component(s) alias(es)
unamedCombo = "parent12_child7"

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getPlayback()

def step1():
	"""
	@step      Execute some operations on java GUI
	@expected  No verification done
	"""
	time.sleep(2)
	javaguiMI.selectTab("TABBED_PANE", 0)
	
	time.sleep(3)
	javaguiMI.setText("TEXT_FIELD", "it' a demo")

	time.sleep(3)
	javaguiMI.setText("FORMATTED_TEXT_FIELD", "1.5%")

	time.sleep(3)
	javaguiMI.setText("PASSWORD_FIELD", "password")

	time.sleep(3)
	javaguiMI.setText("TEXT_AREA", "It's really a demonstration don't be afraid")
	time.sleep(3)
	javaguiMI.setText("TEXT_AREA", "It's not an evil software!!")
	time.sleep(3)
	javaguiMI.setText("TEXT_AREA", "It's working with named component and unamed components too...\ncfr the UNAMED COMPONENT tab!")
	
	time.sleep(3)
	javaguiMI.setText("TEXT_AREA", "just for fun\n\nor not")
	
	time.sleep(3)
	javaguiMI.selectTab("TABBED_PANE", 4)
	
	time.sleep(3)
	javaguiMI.selectValue(unamedCombo, "elmt_04")
	
	time.sleep(2);

doStep(step1)
