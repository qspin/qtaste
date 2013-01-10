Rem Find  process based on the process name and command-line arguments
Rem Usage: find_process.vbs <process_name> <command_line_arguments>
Rem Result: print the process id of each matching process
Rem Due to a limitation in wscript, you cannot use double-quotes inside arguments, so use 2 single-quote to represent 1 double-quotes

If WScript.Arguments.Count <> 2 Then
    WScript.Echo "Usage: find_process.vbs <process_name> <command_line_arguments>"
    WScript.Quit(1)
End If

ProcessName = Replace(Replace(WScript.Arguments.Item(0), "''", """"), "\", "\\")
CommandLineArguments = Replace(Replace(WScript.Arguments.Item(1), "''", """"), "\", "\\")
Set WMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
Set Processes = WMIService.ExecQuery("Select ProcessId from Win32_Process where Name='" & ProcessName & "' and CommandLine like '%" & CommandLineArguments & "'")
If Processes.Count = 0 Then
    WScript.Quit(1)
End If
For each Process in Processes
   WScript.Echo Process.ProcessId
Next
