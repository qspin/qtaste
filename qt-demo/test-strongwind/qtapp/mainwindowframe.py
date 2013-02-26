from strongwind import *

# class to represent the main window.
class MainWindowFrame(accessibles.Frame):
	def __init__(self, accessible):
		super(MainWindowFrame, self).__init__(accessible)
        self.radio = self.findCheckBox("radio")
        self.checkbox = self.findCheckBox("checkbox")
        
    def assertChecked(self, accessible):
        """Raise exception if the accessible does not match the given result"""
        # log our expected results
        procedurelogger.expectedResult('%s is %s.' % (accessible, "checked"))
 
        # Methods that returns true if the accessible is checked (i.e., has
        # the "checked" state.  Otherwise, it returns false.
        def resultMatches():
            return accessible.checked
 
        # Assert that resultMatches returns true eventually.  retryUntilTrue is
        # a Strongwind function that tries RETRY_TIMES times every
        # RETRY_INTERVAL seconds.  RETRY_TIMES = 20 and RETRY_INTERVAL = 0.5 
        # by default as defined by Strongwind's config.py file.
        assert retryUntilTrue(resultMatches)
 
    def assertUnchecked(self, accessible):
        """Raise exception if the accessible does not match the given result"""
        # log our expected results
        procedurelogger.expectedResult('%s is %s.' % (accessible, "unchecked"))
 
        # Methods that returns false if the accessible is checked (i.e., has
        # the "checked" state.  Otherwise, it returns true.
        def resultMatches():
            return not accessible.checked
 
        assert retryUntilTrue(resultMatches)
        
    def assertClosed(self):
        """ raise exception if the application is still open"""
        # close the main window (frame)
        super(MainWindowFrame, self).assertClosed()
 
        # if the main window closes, the entire app should close.  
        # assert that this is true 
        self.app.assertClosed()        

