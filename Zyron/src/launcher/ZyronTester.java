package launcher;

import display.IntroScreen;

/*
 * Author: Alan Sun
 * Date: June 9. 2019
 * Course: ICS3U1
 * Major Skills: HashMaps, ArrayLists, 2D arrays, 3D list structures (array list of 2D arrays), Customized Comparators, 
 * 	Image Filters, Reading Images, Exporting Images, Resizing and Cropping Buffered Images
 * Areas of concerns: input image may not be read correctly due to lack of patterns stored in directory to be compared
 * 
 * Program Description: Zyron, a digital text converter that convert images that contains a word into digital text. 
 * 	The program uses greedy algorithms to resize and break down the input image into buffered sub-images of each
 * 	individual characters. All the informations about the text are stored in two 3D list structures - an 
 * 	ArrayList of 2D boolean arrays and an ArrayList of HashMaps. Zyron then processes each resized image by traversing
 * 	through the program's directory to compare all the pre-stored patterns with the sub-images.
 *  Then the program displays up to 10 assumptions for input text by searching though a dictionary file. If one of 
 *  the guess is correct, the user may choose to store the file he inputed; which the program breaks down into characters
 *  and store it in its directory and improves its ability to recognize characters in future conversions!
 */
public class ZyronTester {

	// main method executes when program runs, starts program by opening a new intro screen
	public static void main(String[] args) {
		
		new IntroScreen();

	}

}
