package view;

/**
 * Defines the view class responsible for updating the screen with message from Controller.
 * @author ZiXian92
 */
public class MessageView {
	/**
	 * Prints the given message to the console.
	 * @param message The message to be printed. Cannot be null or empty string.
	 */
	public void updateView(String message){
		assert message!=null && !message.isEmpty();
		System.out.println(message);
	}
}
