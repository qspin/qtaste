from controlscript import *
from com.qspin.qtaste.util import OS as _OS

import os

# set specific variables according to the OS
if (_OS.getType() == _OS.Type.WINDOWS):
    tempdir="%TEMP%"
    cmd1=['cmd', '/c', 'echo on start']
    cmd2=['cmd', '/c', 'echo on stop']
    cmd3=['cmd', '/c', 'copy COPYING* ' + tempdir]
    cmd4=['cmd', '/c', 'dir ' + tempdir + '\\COPYING']
    cmd5=['cmd', '/c', 'echo I am a file with spaces > "' + tempdir + '\\name with spaces.txt"']
    cmd6=['cmd', '/c', 'type "' + tempdir + '\\name with spaces.txt" & del "' + tempdir + '\\name with spaces.txt"']
    nativeProcessExe='notepad.exe'
else:
    tempdir="/tmp"
    cmd1=['bash', '-c', 'echo on start']
    cmd2=['bash', '-c', 'echo on stop']
    cmd3=['bash', '-c', 'cp COPYING* ' + tempdir]
    cmd4=['bash', '-c', 'ls ' + tempdir + '/COPYING']
    cmd5=['bash', '-c', 'echo "I am a file with spaces" > "' + tempdir + '/name with spaces.txt"']
    cmd6=['bash', '-c', 'cat "' + tempdir + '/name with spaces.txt" & rm "' + tempdir + '/name with spaces.txt"']
    nativeProcessExe='gedit -s'

# build the control actions list
controlActionList = [

    #--------------------------
    # Command class
    #--------------------------

    # test constructor parameters
    Command("on start command",
            startCommand=cmd1),
    Command("on stop command",
            stopCommand=cmd2),
    Command("start and stop command",
            startCommand=cmd3,
            stopCommand=cmd4
            ),
            
    # test path with spaces
    Command("path with spaces",
            startCommand=cmd5,
            stopCommand=cmd6),

    #--------------------------
    # NativeProcess class
    #--------------------------

    # test constructor parameters
    NativeProcess("without argument",
                  nativeProcessExe),
                  
    NativeProcess("with an argument",
                  nativeProcessExe,
                  "COPYING"),
                  
    NativeProcess("with an argument in demo working directory",
                  nativeProcessExe,
                  "COPYING",
                  qtasteRootDirectory + os.sep + 'demo'),
                  
    NativeProcess("with all options",
                  nativeProcessExe,
                  "COPYING",
                  qtasteRootDirectory + os.sep + 'demo',
                  5,
                  priority="belownormal",
                  outFilename=qtasteRootDirectory + os.sep + 'notepad.output.log'),

    # test with spaces
    # TODO
                  
    #--------------------------
    # ReplaceInFiles class
    #--------------------------

    # Replace in one file
    ReplaceInFiles("[Ll]icenses*",
                   "ABCDEF",
                   tempdir + os.sep + 'COPYING'),

    # Replace in several files
    ReplaceInFiles("[Ss]of..ares*",
                   "The new text",
                   [tempdir + os.sep + 'COPYING', tempdir + os.sep + 'COPYING.LESSER']),
    
    # Replace but does not find the string
    ReplaceInFiles("ASDEIZODDLELSSOZODLSQLMSLM",
                   "ABCDEF",
                   tempdir + os.sep + 'COPYING'),

    #--------------------------
    # ShellCommand class
    #--------------------------
    
    # default shell
    ShellCommand("echo I am a start shell command"),
    ShellCommand(stopCommand="echo I am a stop shell command"),
    ShellCommand("echo I am a start shell command ...",
                 "echo ... and a stop shell command"),
    #--------------------------
    # ServiceProcess class
    #--------------------------

    #--------------------------
    # JavaProcess class
    #--------------------------

    #--------------------------
    # Sleep class
    #--------------------------
    Sleep(2),
    Sleep(1, "I'm sleeping for 1 second"),
    
    #--------------------------
    # OnStart class
    #--------------------------
    OnStart(ShellCommand("echo I am a start command",
                         "echo I am a stop command")),
                             
    #--------------------------
    # OnStop class
    #--------------------------
    OnStop(ShellCommand("echo I am a start command",
                         "echo I am a stop command"))

    ]

# add specific OS test
if (_OS.getType() == _OS.Type.WINDOWS):

    # powershell
    controlActionList.extend([
        ShellCommand("echo I am a start shell command",
                     shell="powershell.exe"),
        ShellCommand(stopCommand="echo I am a stop shell command",
                     shell="powershell"),
        ShellCommand("echo I am a start shell command ...",
                     "echo ... and a stop shell command",
                     shell="powershell.exe")
    ])
                 
# TODO
# last start action should be a script to verify start sequence
# last stop should be a script to verify start sequence         

ControlScript(controlActionList)
