MyGitHubIssueTracker
====================
<p>Welcome to MyGitHubIssueTracker, a desktop application for managing GitHub issues without having to go to GitHub website. This document is meant to help you get started with MyGitHubIssueTracker quickly.</p>
<h2>Guide for Users</h2>
<h3>System Requirements</h3>
<ul>
<li>Java Runtime Environment(JRE) 7 and above</li>
<li>Internet connection</li>
</ul>
<h3>Launching the Program</h3>
<ol>
<li>Download the JAR file from the repository and save it to a location you wish to launch the program from.</li>
<li>Open the Command Prompt(for Windows) or Terminal(for MAC OS and Linux) and navigate to the location where the JAR file was saved.</li>
<li>Make the JAR file executable.</li>
<li>From the Command Prompt or Terminal, execute <b>java -jar MyGitHubIssueTracker.jar</b>, assuming you saved the JAR file as MyGitHubIssueTracker.jar.</li>
</ol>
<h3>Listing Repositories</h3>
<p>Enter <b>list</b> or <b>ls</b></p>
<h3>Selecting a Repository</h3>
<p>Enter the index associated with the repository in the list of repositories when the screen displays the repository list.</p>
<h3>Selecting an Issue</h3>
<p>Enter the index associated with the issue in the selected repository's list of issues after selecting a repository.</p>
<h3>Go Up a Level</h3>
<p>Enter <b>back</b> or <b>b</b>. Highest level is the list of repositories. Deselects currently selected repository/issue, whichever is of lower level(Issue&lt;Repository&lt;Repository List).</p>
<h3>Adding an Issue</h3>
<p>Select the repository you wish to add the new issue into. Enter <b>add &lt;issue_title&gt;</b>. Enter the body content and assignee as prompted. Note: There is a bug causing the assignee information to be dropped at the time of writing.</p>
<h3>Closing an Issue</h3>
<p>Enter <b>close</b> to close the currently selected issue. Enter <b>close </b>, followed by the issue's index number to close an issue in the currently selected repository.</p>
<h3>Exiting the Program</h3>
<p>Enter <b>exit</b>.</p>
<h2>Guide for Developers</h2>
<p>The link to the developer's guide webpage will be posted here once the guide is completed.</p>
