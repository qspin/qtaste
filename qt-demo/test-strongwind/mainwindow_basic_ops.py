#!/usr/bin/env python
 
"""
Test accessibility of qtapp
"""
 
# imports
from strongwind import *
from qtapp import *
from sys import argv
from os import path
 
# declare the ''app_path'' variable (to be used later) and initialize it to ''None''
app_path = None

try:
  app_path = argv[1]
except IndexError:
  pass #expected
  
# open the qt application
try:
  app = launchQtApp(app_path)
except IOError, msg:
  print "ERROR:  %s" % msg
  exit(2)
 
# make sure we got the app back
if app is None:
  exit(4)
  
# just an alias to make things shorter, so we can type cbFrame.checkbox1.click()
# instead of app.gtkCheckButtonFrame.checkbox1.click()
cbFrame = app.mainWindowFrame
 
# perform a "click" action on checkbox1.  checkbox1 is defined in the
# application wrapper (gtkcheckbuttonframe.py)
cbFrame.checkbox.click()
# need a short delay between clicking the check box and asserting that the
# check box has been clicked.  this just gives the GUI time to update before
# we assert that it should be changed.
sleep(config.SHORT_DELAY)
# assert that checkbox1 has been clicked/checked.
cbFrame.assertChecked(cbFrame.checkbox)
 
# perform a "click" action a second time, this should uncheck the checkbox.
cbFrame.checkbox.click()
sleep(config.SHORT_DELAY)
# assert that checkbox1 has been clicked a second time, i.e., unchecked
cbFrame.assertUnchecked(cbFrame.checkbox)
 
# do the same thing to checkbox2
cbFrame.radio.click()
sleep(config.SHORT_DELAY)
cbFrame.assertChecked(cbFrame.radio)
 
cbFrame.radio.click()
sleep(config.SHORT_DELAY)
cbFrame.assertUnchecked(cbFrame.radio)
 
# call the application wrapper's quit method (defined in gtkcheckbuttonframe.py)
cbFrame.quit()

# tell the user where to find the log
print "INFO:  Log written to: %s" % config.OUTPUT_DIR
