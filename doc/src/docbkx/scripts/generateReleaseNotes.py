#!/usr/bin/python

import sys
import string
sys.path.append('../../../../tools/PyGithub')

from github import Github
from github import GithubObject
from github import GithubException
from xml.etree.ElementTree import Element, SubElement, tostring


class QTasteIssue:
    def __init__(self, aId, aLink, aTitle, aAssignTo, aAssigneeLink, aUpdateDate, aMilestoneTitle):
        self.id              = aId
        self.link            = aLink
        self.title           = aTitle
        self.assignTo        = aAssignTo
        self.assigneeLink    = aAssigneeLink
        self.updateDate      = aUpdateDate
        self.milestoneTitle  = aMilestoneTitle


def createIssuesItems(aGithubRepo, aQtasteVersion, aState, aLabelName=""):
    # Create XML output for the queried issues
    root = Element('itemizedlist')
    root.set('mark', "opencircle")
    tbodyStr = ""

    # Check if there is such milestone
    milestone = None
    for milestoneIt in aGithubRepo.get_milestones():
        if(milestoneIt.title.startswith(aQtasteVersion)):
            milestone = milestoneIt
            break

    # Get all issues for current milestone (if any) which are closed and
    # labels as "type:new_feature"
    noneIssuesWereFound = True
    if(milestone != None):
        for issue in aGithubRepo.get_issues(milestone=milestone,\
                                            state=aState):

            isIssueToBePrinted = False
            if(aLabelName == ""): # Query issues wich are neither features nor bugs
                isIssueToBePrinted = True
                for label in issue.get_labels():
                    if(label.name.endswith("new_feature") or\
                       label.name.endswith("bug")):
                        isIssueToBePrinted =  False
                        break
            else:
                for label in issue.get_labels():
                    if(label.name == aLabelName):
                        isIssueToBePrinted = True
                        break

            if(isIssueToBePrinted):
                item = Element('listitem')
                item.set('override', "bullet")
                issueLink = Element('ulink')
                issueLink.set('url', issue.html_url)
                issueLink.text = " (#" + str(issue.number) + ")"
                item.append(issueLink)
                item.text = issue.title
                root.append(item)

                #item.text = issue.title

                noneIssuesWereFound = False


        # End FOR
    if(noneIssuesWereFound):
        item = Element('listitem')
        item.set('override', "bullet")
        item.text = "None"
        root.append(item)

    return tostring(root)


def createNewFeaturesSection(aGithubRepo, aQtasteVersion):
    return createIssuesItems(aGithubRepo, aQtasteVersion,\
                             "closed", "type:new_feature")

def createBugFixesSection(aGithubRepo, aQtasteVersion):
    return createIssuesItems(aGithubRepo, aQtasteVersion,\
                             "closed", "type:bug")

def createChangesSection(aGithubRepo, aQtasteVersion):
    return createIssuesItems(aGithubRepo, aQtasteVersion, "closed")

def createIssuesTableBody(aGithubRepo, aState):
    # create XML output for the queried issues
    root = Element('tbody')
    tbodyStr = ""
    # Iterates through Issues read from GitHub
    for issue in aGithubRepo.get_issues(state=aState):
        assignTo = "None"
        if(issue.assignee != None):
            assignTo = issue.assignee.login

        # Add row and append to root
        row = Element('row')
        root.append(row)

        # Add entries for every issue field
        # ID
        issueId = Element('entry')
        issueLink = Element('ulink')
        issueLink.set('url', issue.html_url)
        issueLink.text = "#" + str(issue.number)
        issueId.append(issueLink)
        row.append(issueId)

        # Title
        title = Element('entry')
        title.text = issue.title
        row.append(title)

        # Assign
        assignee = Element('entry')
        if(assignTo != "None"):
            assigneeLink = Element('ulink')
            assigneeLink.set('url', issue.assignee.html_url)
            assigneeLink.text = assignTo
            assignee.append(assigneeLink)
        else:
            assignee.text = assignTo
        row.append(assignee)

        # Updated
        updated = Element('entry')
        updated.text = str(issue.updated_at)
        row.append(updated)

        # Milestone
        milestone = Element('entry')
        if(issue.milestone != None):
            milestone.text = issue.milestone.title
        else:
            milestone.text = "None"
        row.append(milestone)

        # Generates a string representation of root XML element
        tbodyStr = tostring(root)

    return tbodyStr


def main():
    print "------------------------------------------------------------"
    print "QTaste: Generating Release Notes"
    print ""

    try: # Connect to Github repository
        g = Github()
        repo = g.get_repo("qspin/qtaste")
        print "connecting to repository: " + repo.name + " ..."
    except GithubException as e:
        print "Could not connect to Github:"
        sys.exit(e)

    # Read XML file that will be formatted
    releaseNotesTemplateFilePath = "./qtaste_release_notes_template.xml"
    strFile = ""
    try:
        f = open(releaseNotesTemplateFilePath, 'rw')
    except IOError as e:
        print 'Cannot open', releaseNotesTemplateFilePath
        sys.exit(e)
    else:
        strFile = f.read()
        f.close()

    # Read QTaste version (if available)
    qtasteVersionCurrent = ""
    qtasteVersionForRelease = ""
    qtasteVersionFilePath = "../../../../Version.txt"
    try:
        fVersion = open(qtasteVersionFilePath, 'r')
    except IOError:
        print 'None QTaste version found: cannot open ', qtasteVersionFilePath
    else:
        for line in fVersion:
            # Ignore lines startwith '#' char
            if(line.startswith('#')):
                continue
            # check for qtaste-version
            elif(line.startswith("qtaste-version")):
                qtasteVersionCurrent = line.split('=')[1]
                # release version only takes the first 5 characters: "X.Y.Z"
                qtasteVersionForRelease = line.split('=')[1][:5]
            ## Add here more cases, if needed to retrieve more info
        fVersion.close()



    print "reading release notes info from repository ..."

    # Read New Features for current QTasteVersion <=> Milestone
    strNewFeaturesIssues = createNewFeaturesSection(repo, qtasteVersionForRelease)
    # Read Bug Fixes for current QTasteVersion <=> Milestone
    strBugFixesIssues = createBugFixesSection(repo, qtasteVersionForRelease)
    # Read Other Changes for current QTasteVersion <=> Milestone
    strChangesIssues = createChangesSection(repo, qtasteVersionForRelease)
    # Read Open Issues
    tbodyStrOpenIssues = createIssuesTableBody(repo, "open")
    # Read Closed Issues
    tbodyStrClosedIssues = createIssuesTableBody(repo, "closed")

    # Add the tbody with Remaining issues
    strFile = strFile.format(QTasteVersion=qtasteVersionCurrent,\
                             QTasteNewFeatures=strNewFeaturesIssues,\
                             QTasteFixedIssues=strBugFixesIssues,\
                             QTasteChanges=strChangesIssues,\
                             GithubOpenIssuesTableBody=tbodyStrOpenIssues,\
                             GithubClosedIssuesTableBody=tbodyStrClosedIssues)

    print "creating release notes file -> qtaste_release_notes.xml ..."

    # Generate ReleaseNotes XML output file
    releaseNotesOutputFilePath = "../qtaste_release_notes.xml"
    try:
        fOutXml = open(releaseNotesOutputFilePath, 'w')
    except IOError as e:
        print 'Cannot open', releaseNotesOutputFilePath
        sys.exit(e)
    else:
        fOutXml.write(strFile)
        fOutXml.close

    print ""
    print "QTaste: Finish"
    print "------------------------------------------------------------"


########################################################################
############################ MAIN ######################################
########################################################################
main()
