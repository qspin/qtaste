Attribute VB_Name = "ModuleMain"
Sub ExportToCSV()
Attribute ExportToCSV.VB_ProcData.VB_Invoke_Func = "s\n14"
    Dim csvWorkBook As Workbook
    
    ' Don't do anything if file is not "TestData.xls"
    If Application.ActiveWorkbook.Name <> "TestData.xls" Then
      ' Save the current workbook
      Application.ActiveWorkbook.Save
      Exit Sub
    End If
    
    Reset_lastcell
    
    NumericToString
    
    sCurrentFileName = Application.ActiveWorkbook.FullName
    sCSVFileName = Replace(sCurrentFileName, ".xls", ".csv", 1, -1, vbTextCompare)
    'sCopyFileName = Replace(sCurrentFileName, ".xls", "_temp.xls", 1, -1, vbTextCompare)
    
    ' Save the current workbook
    Application.ActiveWorkbook.Save
    
    'Create a copy workbook
    'ThisWorkbook.SaveCopyAs (sCopyFileName)
    'Set csvWorkBook = Application.Workbooks.Open(sCopyFileName)
    
    'csvWorkBook.SaveAs sCSVFileName, xlCSV
    CreateCSV (sCSVFileName)
    'csvWorkBook.Close
    'Kill sCopyFileName
End Sub

Sub Reset_lastcell()
  'David McRitchie,  http://www.mvps.org/dmcritchie/excel/lastcell.htm
   Dim x As Long  'Attempt to fix the lastcell on the current worksheet
   x = Application.ActiveWorkbook.ActiveSheet.UsedRange.Rows.Count 'see J-Walkenbach tip 73
End Sub

Sub NumericToString()

Dim UsedRange As Range
Dim currentSheet As Worksheet
Dim cell As Range


Set currentSheet = Application.ActiveWorkbook.ActiveSheet
Set UsedRange = currentSheet.Range("A1", currentSheet.Cells.SpecialCells(xlCellTypeLastCell))

For Each cell In UsedRange.Cells
    On Error GoTo ErrorHandler
    Dim dCellContent As Double
    Dim sCellContent As String
    
    dCellContent = cell.Cells(1, 1)
    sCellContent = cell.Cells(1, 1)
    sReplacedContent = Replace(sCellContent, ",", ".", 1, -1, vbTextCompare)
    cell.NumberFormat = "@"
    cell.Cells(1, 1) = sReplacedContent
    

TypeMismatch:
    Next cell
ErrorHandler:
    If Err = 13 Then        'Type Mismatch
        Resume TypeMismatch
    End If
End Sub

Sub CreateCSV(fileName As String)
    Dim rCell As Range
    Dim rRow As Range
    Dim sOutput As String
    Dim sFname As String, lFnum As Long
    
    
    'Open a text file to write
    sFname = fileName
    lFnum = FreeFile
    
    Open sFname For Output As lFnum
    
    'Loop through the rows
    For Each rRow In Application.ActiveSheet.UsedRange.Rows
        'Loop through the cells in the rows
        For Each rCell In rRow.Cells
            sOutput = sOutput & rCell.Value & ";"
        Next rCell
        'remove the last comma
        sOutput = Left(sOutput, Len(sOutput) - 1)
        
        'write to the file and reinitialize the variables
        Print #lFnum, sOutput
        sOutput = ""
    Next rRow
    
    'Close the file
    Close lFnum
End Sub
