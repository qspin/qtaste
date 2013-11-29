import sys
from controlscript import *

print "This is a simple control script. It just does nothing and exits with error 1 on stop."
print "Start parameter is %s, additional parameters are %s" % (start, arguments)


class StopError(ControlAction):
	""" Control script action for exiting with error 1 on stop """
	def __init__(self):
		ControlAction.__init__(self, "Do nothing but exit with error 1 on stop")

	def start(self):
		print "Do nothing on start"
		print

	def stop(self):
		print "Exit with error 1 on stop"
		sys.exit(1)
		print


ControlScript([
    StopError()
])