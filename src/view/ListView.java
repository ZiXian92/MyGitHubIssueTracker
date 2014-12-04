package view;

/**
 * Defines the class that is responsible for updating the view with the list of projects.
 * @author ZiXian92
 */
public class ListView {
    public void updateView(String[] projectList){
	int size = projectList.length;
	for(int i=0; i<size; i++){
	    System.out.println((i+1)+". "+projectList[i]);
	}
    }
}
