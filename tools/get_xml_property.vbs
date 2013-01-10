Rem Get property value from an xml file
Rem Usage: get_xml_property <XML_filename> <property_path>
Rem Result: print the process id of each matching process
Rem Due to a limitation in wscript, you cannot use double-quotes inside arguments, so use 2 single-quote to represent 1 double-quotes

If WScript.Arguments.Count <> 2 Then
	WScript.Echo "Usage: get_xml_property.vbs <XML_filename> <property_path>"
	WScript.Quit(1)
End If

XMLFileName = WScript.Arguments.Item(0)
PropertyPath = WScript.Arguments.Item(1)

Set XMLObj = CreateObject("Microsoft.XMLDOM")
XMLObj.async = False
If XMLObj.Load(XMLFileName) Then
	Set PropertyNode = XMLObj.documentElement.selectSingleNode(PropertyPath)
	If PropertyNode is Nothing Then
		WScript.Echo "Couldn't find property " & PropertyPath & " in XML file " & XMLFileName
		WScript.Quit(1)
	Else
		WScript.Echo PropertyNode.text
	End If
Else
	WScript.Echo "Could not load XML file " & XMLFileName & VbCrLf & "Reason: " & XMLObj.parseError.reason
	WScript.Quit(1)
End If
