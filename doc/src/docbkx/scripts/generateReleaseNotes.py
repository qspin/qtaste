#!/usr/bin/python

import sys
import string
import os
import argparse
import time
import collections
sys.path.append('../../../../tools/PyGithub')

import config
from github import Github
from github import GithubObject
from github import GithubException
from xml.etree.ElementTree import Element, SubElement, tostring

OPEN_STATE = "open"
CLOSED_STATE = "closed"
ALL_STATE = "all"

def isValidMilestoneVersion(aVersion, aMilestoneVersion):
    #decompose version into X.Y.Z
    qtVersion_X = 0
    qtVersion_Y = 0
    qtVersion_Z = 0
    try:
        # expected aVersion string as "X.Y.Z"
        qtVersion_X = int(aVersion[0])
        qtVersion_Y = int(aVersion[2])
        qtVersion_Z = int(aVersion[4])
    except Exception:
        pass

    #decompose Milestone version into X.Y.Z
    mlVersion_X = 0
    mlVersion_Y = 0
    mlVersion_Z = 0
    try:
        # expected milestoneVersion string as "X.Y.Z"
        mlVersion_X = int(aMilestoneVersion[0])
        mlVersion_Y = int(aMilestoneVersion[2])
        mlVersion_Z = int(aMilestoneVersion[4])
    except Exception:
        pass

    if(mlVersion_X == qtVersion_X and\
       (mlVersion_Y < qtVersion_Y or\
       (mlVersion_Y == qtVersion_Y and mlVersion_Z <= qtVersion_Z))):
        return True
    return False

def createMilestonesItems(aGithubRepo, aVersion, aState="closed"):

    tbodyStr = ""
    # Check for all milestones versions <= X.Y.Z
    milestone = None
    milestoneTitle = ""
    milestoneStates = ["open", "closed"]

    # Read all milestones and sort by version number
    milestones = aGithubRepo.get_milestones(state="all", direction="asc", sort="due_date")
    opennedMilestonesDict = {}
    closedMilestonesDict = {}
    for milestoneIt in milestones:
        if (milestoneIt.state == OPEN_STATE):
            opennedMilestonesDict[milestoneIt.title] = milestoneIt
        else:
            closedMilestonesDict[milestoneIt.title] = milestoneIt
    milestonesSortedList = []
    milestonesSortedList.append(collections.OrderedDict(sorted(opennedMilestonesDict.items(),reverse=True)).items())
    milestonesSortedList.append(collections.OrderedDict(sorted(closedMilestonesDict.items(),reverse=True)).items())

    for milestoneList in milestonesSortedList:
        for milestoneTitle, milestoneIt in milestoneList:
            if(isValidMilestoneVersion(aVersion, milestoneIt.title) == True):
                # Create XML output for the queried issues
                rootNewFeatureItems = Element('itemizedlist')
                rootNewFeatureItems.set('mark', "opencircle")
                rootBugItems = Element('itemizedlist')
                rootBugItems.set('mark', "opencircle")
                rootOtherItems = Element('itemizedlist')
                rootOtherItems.set('mark', "opencircle")

                milestone = milestoneIt
                milestoneTitle = milestoneIt.title

                # Get all closed issues for current milestone (if any)
                noneNewFeatureItemsWereFound = True
                noneBugItemsWereFound = True
                noneOtherItemsWereFound = True
                if(milestone != None):
                    for issue in aGithubRepo.get_issues(milestone=milestone,\
                                                        state=aState):

                        item = Element('listitem')
                        item.set('override', "bullet")
                        issueLink = Element('ulink')
                        issueLink.set('url', issue.html_url)
                        issueLink.text = " (#" + str(issue.number) + ")"
                        item.append(issueLink)
                        item.text = issue.title

                        isLabelFound = False
                        for label in issue.get_labels(): # May have several labels
                            if(label.name.endswith("new_feature")):
                                rootNewFeatureItems.append(item)
                                isLabelFound = True
                                noneNewFeatureItemsWereFound = False
                                break
                            elif(label.name.endswith("bug")):
                                rootBugItems.append(item)
                                isLabelFound = True
                                noneBugItemsWereFound = False
                                break
                        # End FOR
                        if(isLabelFound == False):
                            rootOtherItems.append(item)
                            noneOtherItemsWereFound = False
                    # End FOR
                # End IF

                # Append None Item if no other items were found
                item = Element('listitem')
                item.set('override', "bullet")
                item.text = "None"
                if(noneNewFeatureItemsWereFound == True):
                    rootNewFeatureItems.append(item)
                if(noneBugItemsWereFound == True):
                    rootBugItems.append(item)
                if(noneOtherItemsWereFound == True):
                    rootOtherItems.append(item)

                # Format body string
                tbodyStr += "<para><emphasis role='bold'> Version: </emphasis>" + milestoneTitle + "</para>\n"

                tbodyStr += "<para><emphasis role='bold'>New Features:</emphasis></para>\n"
                tbodyStr += tostring(rootNewFeatureItems) + "\n"

                tbodyStr += "<para><emphasis role='bold'>Resolved Issues:</emphasis></para>\n"
                tbodyStr += tostring(rootBugItems) + "\n"

                tbodyStr += "<para><emphasis role='bold'>Other Changes:</emphasis></para>\n"
                tbodyStr += tostring(rootOtherItems) + "\n"

                tbodyStr += "<para>________________________________________________________ </para>\n"

            # End IF
        # End FOR - - Milestone Iteration
    # End FOR - Milestone States

    return tbodyStr

def getIssues(aGithubRepo):
    return aGithubRepo.get_issues(state="all")

def createIssuesTableBody(aListOfIssues, aState):
    # create XML output for the queried issues
    root = Element('tbody')
    tbodyStr = ""
    # Iterates through Issues read from GitHub
    for issue in aListOfIssues:

        if (issue.state != aState):
            continue

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

        # Type
        issueType = Element('entry')
        strIssueTypes = ""
        for label in issue.get_labels():
            if(label.name.startswith("type")):
                strIssueTypes += label.name.split(":")[1] + " "
        if(strIssueTypes == ""):
            strIssueTypes = "Not defined"
        issueType.text = strIssueTypes
        row.append(issueType)

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
    print config.AppName, ": Generating Release Notes"
    print ""

    # Parsing input parameters
    parser = argparse.ArgumentParser(description='Generate Release Notes')
    parser.add_argument('-u', '--user', dest='userGithub', help='Github Username')
    parser.add_argument('-p', '--pass', dest='passwordGithub', help='Github Password')
    args = parser.parse_args()

    # Delete the release notes file (if any)
    if(os.path.exists(config.ReleaseNotesOutputFile)):
        try:
            os.remove(config.ReleaseNotesOutputFile)
        except OSError as e:
            print "Cannot generate Release Notes file: being used by the system"
            sys.exit(e)

    # Attempt to connect to Github repository
    try:
        # check if some Github credentials were given in order to have authenticated access
        # http://developer.github.com/v3/#rate-limiting
        # "Basic Authentication or OAuth, you can make up to 5,000 requests per hour.
        #  For unauthenticated requests, the rate limit allows you to make up to 60 requests per hour."
        if(len(sys.argv) > 1 and
            not args.userGithub == None and
            not args.passwordGithub == None):
            g = Github(args.userGithub, args.passwordGithub)
        else:
            print "******************************** WARNING *******************************************"
            print "No Github authentication provided!"
            print "Github repository will be queried without authentication."
            print "This could result in failing to retrieve all needed information due to rate limits."
            print "Please provide your Github credentials for authentication:"
            print " usage: generateReleaseNotes.py [-u <username>] [-p <password>]"
            print "*************************************************** ********************************"
            time.sleep(5)
            g = Github()
        repo = g.get_repo(config.GitHubRepositoryUrl)
        print "connecting to repository: " + repo.name + " ..."
    except GithubException as e:
        print "Could not connect to Github:"
        sys.exit(e)

    # Read XML file that will be formatted
    strFile = ""
    try:
        f = open(config.ReleaseNotesTemplate, 'rw')
    except IOError as e:
        print 'Cannot open', config.ReleaseNotesTemplate
        sys.exit(e)
    else:
        strFile = f.read()
        f.close()

    # Read App version (if available)
    appCurrentVersion = ""
    appVersionForRelease = ""
    try:
        fVersion = open(config.VersionFile, 'r')
    except IOError:
        print 'None', config.AppName, 'version found: cannot open ', config.VersionFile
    else:
        for line in fVersion:
            # Ignore lines startwith '#' char
            if(line.startswith('#')):
                continue
            # check for current version
            elif(line.startswith(config.VersionTag)):
                appCurrentVersion = line.split('=')[1]
                # release version only takes the first 5 characters: "X.Y.Z"
                appVersionForRelease = line.split('=')[1][:5]
            ## Add here more cases, if needed to retrieve more info
        fVersion.close()

    print "reading release notes info from repository ..."

    try: # Query Github repository
        # Read Milestones info (<= current version)
        tbodyStrMilestoneIssues = createMilestonesItems(repo, appVersionForRelease)

        # Load all issues
        listOfIssues = getIssues(repo)
        # Read Open Issues
        tbodyStrOpenIssues = createIssuesTableBody(listOfIssues, OPEN_STATE)
        # Read Closed Issues
        tbodyStrClosedIssues = createIssuesTableBody(listOfIssues, CLOSED_STATE)
    except GithubException as e:
        print "Could not connect to Github:"
        sys.exit(e)

    # Format template with input formatted strings
    strFile = strFile.format(AppVersion=appCurrentVersion,\
                             AppReleaseContent=tbodyStrMilestoneIssues,\
                             GithubOpenIssuesTableBody=tbodyStrOpenIssues,\
                             GithubClosedIssuesTableBody=tbodyStrClosedIssues)

    print "creating release notes docbook xml file ..."

    # Generate ReleaseNotes XML output file
    try:
        fOutXml = open(config.ReleaseNotesOutputFile, 'w')
    except IOError as e:
        print 'Cannot open', config.ReleaseNotesOutputFile
        sys.exit(e)
    else:
        fOutXml.write(strFile)
        fOutXml.close

    print ""
    print config.AppName, ": Finish"
    print "------------------------------------------------------------"


########################################################################
############################ MAIN ######################################
########################################################################
main()
