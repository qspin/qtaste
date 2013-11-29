import sys
from controlscript import *

print "This is a simple control script. It just does nothing and exits with error 1 on start."
print "Start parameter is %s, additional parameters are %s" % (start, arguments)


class StartError(ControlAction):
	""" Control script action for exiting with error 1 on start """
	def __init__(self):
		ControlAction.__init__(self, "Do nothing but exit with error 1 on start")

	def start(self):
		print "Exit with error 1 on start"
		sys.exit(1)
		print

	def stop(self):
		print "Do nothing on stop"
		print


ControlScript([
    StartError()
])