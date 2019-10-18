package display;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import utils.AudioPlayer;
import utils.CustomizationTool;

/*
 * Author: Alan Sun
 * 
 * Question Screen displays up to 10 top guesses for the word inputed
 * Uses static methods from the CustomizationTool class that applies to all screens
 * Uses a customized comparator to sort the <String, Double> hash map
 * Extends JFrame and implements ActionListener for button controls
 */
public class QuestionScreen extends JFrame implements ActionListener {

	// image icons used in this frame
	private ImageIcon submitIcon = new ImageIcon("utility/submit.png");
	private ImageIcon fixLabelIcon = new ImageIcon("utility/write.png");

	// JComponents used in this frame
	private JPanel panel = new JPanel();
	private JTextPane correctWord = new JTextPane();
	private JButton submitButton = new JButton(new ImageIcon(
			submitIcon.getImage().getScaledInstance(submitIcon.getIconWidth() / 3, submitIcon.getIconHeight() / 3, 0)));
	private JLabel fixLabel = new JLabel(new ImageIcon(fixLabelIcon.getImage()
			.getScaledInstance(fixLabelIcon.getIconWidth() / 3, fixLabelIcon.getIconHeight() / 3, 0)));

	// size variables that are used a lot in this frame
	private int textAreaWidth = 800;
	private int textAreaHeight = 180;
	
	// variables that store informations about characters inputed
	private int suspectedWordLength;
	private ArrayList<Character> availableCharacters = new ArrayList<Character>();

	// input screen object used to get passed information
	private InputScreen inputScreen;
	
	// scanner used to read the dictionary text file
	private Scanner dictionary;

	// constructor taking in 2 parameters, initializes variables, and calls other methods
	public QuestionScreen(InputScreen inputScreen, int suspectedWordLength) {

		this.inputScreen = inputScreen;
		this.suspectedWordLength = suspectedWordLength;

		addJComponents();
		CustomizationTool.addMenuBar(this);
		CustomizationTool.frameSetUp(this);
		CustomizationTool.setCustomCursor(this);
		fillAvaliableCharacters();

	}

	// method that adds other JComponents to the frame
	private void addJComponents() {

		// set up the panel by using the static method in the Customization tool
		CustomizationTool.panelSetUp(this, panel);

		// places this component and set the size and font
		correctWord.setBounds(CustomizationTool.programWidth / 2 - textAreaWidth / 2,
				CustomizationTool.programHeight / 2 - textAreaHeight / 2, textAreaWidth, textAreaHeight);
		correctWord.setFont(new Font("impact", Font.BOLD, 150));

		// centers the text horizontally on the text pane using StyledDocument libraries 
		StyledDocument styledDoc = correctWord.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		styledDoc.setParagraphAttributes(0, styledDoc.getLength(), center, false);
		
		// add this component to the panel
		panel.add(correctWord);

		submitButton.setBounds(CustomizationTool.programWidth / 2 - submitButton.getIcon().getIconWidth() / 2,
				CustomizationTool.programHeight - 200, submitButton.getIcon().getIconWidth(),
				submitButton.getIcon().getIconHeight());
		
		// enable button action on this frame for this JComponent
		submitButton.addActionListener(this);
		panel.add(submitButton);

		fixLabel.setBounds(CustomizationTool.programWidth / 2 - fixLabel.getIcon().getIconWidth() / 2, 100,
				fixLabel.getIcon().getIconWidth(), fixLabel.getIcon().getIconHeight());
		panel.add(fixLabel);

	}

	// method that adds all the characters that can be included in the text for error detection
	private void fillAvaliableCharacters() {

		for (int i = 0; i < 26; i++) {

			availableCharacters.add((char) (97 + i));

		}

	}

	// Override method from ActionListner class, used for JComponent action detections
	@Override
	public void actionPerformed(ActionEvent event) {

		// execute if submit button is pressed
		if (event.getSource() == submitButton) {
			
			boolean successCondition = true;
			
			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");

			// loop through each character in the text inputed from the text pane
			for (int i = 0; i < correctWord.getText().length(); i++) {

				// input fails if a character inputed is not included in the available ones
				if (!availableCharacters.contains(correctWord.getText().charAt(i))) {

					successCondition = false;
					break;

				}

			}

			// if text length is different than the one detected, input fails and output fail message
			// length have to be the same in order to correctly store input characters in directory
			if (correctWord.getText().length() != suspectedWordLength || !successCondition) {

				JOptionPane.showMessageDialog(null,
						"Please validate your input\n" + "input must be in lowercase characters from 'a' to 'z'\n"
								+ "the word also have to be the same length as the input detected, which is "
								+ suspectedWordLength + " characters" + "you have " + correctWord.getText().length()
								+ " characters" + "\n\n" + "click 'ok' to continue...",
						"INVALID INPUT", JOptionPane.WARNING_MESSAGE);

			} else {
				
				// try and catch to see if dictionary file exist when loading from scanner
				try {
					
					boolean found = false;
					
					dictionary = new Scanner(new File("utility/dictionary.txt"));
					
					// loop though every word in the dictionary to see if the word inputed is a real word
					while(dictionary.hasNext()) {
	
						if(dictionary.nextLine().equals(correctWord.getText())) {
							
							found = true;
							break;
							
						}
						
					}
					
					// if word is not found, then add it to the dictionary using a print writer
					if(!found) {
						
						PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("utility/dictionary.txt", true)));

						printWriter.println(correctWord.getText());
						
		                printWriter.close();
						
					}
						
					
				} 
				
				catch (IOException error) {
					
					System.out.println("No file found when adding a word to dictionary");
					
				}

				// opens the result screen and closes this 
				new ResultScreen(inputScreen, correctWord.getText());
				this.dispose();

			}

		}

	}

}
