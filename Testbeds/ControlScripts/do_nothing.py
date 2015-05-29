from controlscript import *

print "This is a simple control script. It just does nothing and exits successfully."
print "Start parameter is %s, additional parameters are %s" % (start, arguments)

class DoNothing(ControlAction):
	""" Control script action for exiting with error 1 on stop """
	def __init__(self):
		ControlAction.__init__(self, "Do nothing")

	def start(self):
		print "Do nothing on start"
		print
		return True

	def stop(self):
		print "Do nothing on stop"
		print


ControlScript([
    DoNothing()
])
