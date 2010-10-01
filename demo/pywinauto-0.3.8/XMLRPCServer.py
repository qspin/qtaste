import xmlrpclib
from SimpleXMLRPCServer import SimpleXMLRPCServer
from pywinauto import application

latestSessionID = 0
sessionList = {}

def startApplication(applicationName):
    global latestSessionID, sessionList
    sessionID = latestSessionID
    latestSessionID+=1
    print ('starting %s application with sessionID=%d\n' %(applicationName, sessionID))
    app = application.Application.start(applicationName)
    sessionList[sessionID] = app
    return sessionID

def stopApplication(sessionID):
    global sessionList
    #Todo: review this!
    print ('stopping application with sessionID=%d\n' %(sessionID))
    getApplicationContext(sessionID).Kill_()
    sessionList[sessionID] = None
    return sessionID

def execute(sessionID, execute):
    global sessionList
    print ('executing text %s sessionID %d\n' %(execute, sessionID)) 
    command = "getApplicationContext(sessionID)." + execute    
    eval(command)    
    exec(command)
    print ('execute returns\n')
    return 0

def executeWithoutEval(sessionID, execute):
    global sessionList
    print ('executing text %s sessionID %d\n' %(execute, sessionID))
    #print ('Executing command %s of window %s sessionID %d\n' %(execute, sessionID))
    command = "getApplicationContext(sessionID)." + execute
    print ('executing %s on %d ID\n' %(command, sessionID))        
    exec(command)
    print ('execute returns\n')
    return 0

def selectMenu(sessionID, windowName, menu):
    #global sessionList
    print ('selecting menu %s of window %s sessionID %d\n' %(menu, windowName, sessionID))
    getApplicationContext(sessionID)[windowName].MenuSelect(menu)
    return 0

def pressButton(sessionID, windowName, name):
    print ('pressing button %s of window %s sessionID %d\n' %(name, windowName, sessionID))
    getApplicationContext(sessionID)[windowName][name].Click()
    return 0

def getText(sessionID, windowName, name):
    print ('getting text %s of window %s sessionID %d\n' %(name, windowName, sessionID))
    # Todo: Check what parameters should be returned!
    print getApplicationContext(sessionID)[windowName][name].Texts()
    return getApplicationContext(sessionID)[windowName][name].Texts()[0]

def setText(sessionID, windowName, name, value):
    print ('setting text %s of window %s sessionID %d\n' %(name, windowName, sessionID))
    getApplicationContext(sessionID)[windowName][name].SetEditText(value)
    return 0

def listElements(sessionID):
    print ('listing of elements sessionID %d\n' %(sessionID))
    getApplicationContext(sessionID).top_window_()._ctrl_identifiers()
    getApplicationContext(sessionID).top_window_().PrintControlIdentifiers()
    return 0

def selectTreeViewItem(sessionID, windowName, treeviewName, item):
    print ('Selecting the item %s of window %s sessionID %d\n' %(item, windowName, sessionID))
    getApplicationContext(sessionID)[windowName][treeviewName].Select(item)
    return 0

def getApplicationContext(sessionID):
    global sessionList
    return sessionList[sessionID]

server = SimpleXMLRPCServer(("localhost", 8080))
print "Listening on port 8080..."
server.register_function(startApplication)
server.register_function(stopApplication)
server.register_function(execute)
server.register_function(executeWithoutEval)
server.register_function(selectMenu)
server.register_function(pressButton)
server.register_function(getText)
server.register_function(setText)
server.register_function(listElements)
server.register_function(selectTreeViewItem)
server.serve_forever()