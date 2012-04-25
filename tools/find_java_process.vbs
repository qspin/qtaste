Rem Find java process based on the command-line arguments
Rem Usage: find_java_process.vbs <command_line_arguments>
Rem Result: print the process id of each matching process
Rem Due to a limitation in wscript, you cannot use double-quotes inside arguments, so use 2 single-quote to represent 1 double-quotes

Set objArgs = WScript.Arguments
CommandLineArguments = Replace(Replace(objArgs(0), "''", """"), "\", "\\")

Set WMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
Set Processes = WMIService.ExecQuery("Select ProcessId from Win32_Process where Name='java.exe' and CommandLine like '%" & CommandLineArguments & "'")
If Processes.Count = 0 Then
    WScript.Quit(1)
End If
For each Process in Processes
   WScript.Echo Process.ProcessId
Next
