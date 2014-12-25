package controller;

/**
 * Defines the Observer interface for the Observer Pattern.
 * @author ZiXian92
 */
public interface Observer {
    /**
     * Updates the status of the currently selected project.
     * @param repo The name of the currently selected project or null if no project is selected.
     */
    void updateSelectedRepository(String repo);
    
    /**
     * Updates the status of the currently selected issue.
     * @param issueName The name of the currently selected issue or null if no project/issue is selected.
     */
    void updateSelectedIssue(String issueName);
}
