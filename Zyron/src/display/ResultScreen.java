package display;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
public class ResultScreen extends JFrame implements ActionListener {
	
	// variable that keeps track of the selected word
	private String selectedWord;
	
	// image icons used in this frame
	private ImageIcon saveIcon = new ImageIcon("utility/save.png");
	private ImageIcon disposeIcon = new ImageIcon("utility/dispose.png");
	private ImageIcon exportIcon = new ImageIcon("utility/export.png");

	// JComponents used in this frame
	private JPanel panel = new JPanel();
	private JLabel selectedCharacterLabel;
	private JLabel exportLabel = new JLabel(new ImageIcon(exportIcon.getImage().getScaledInstance(
			exportIcon.getIconWidth()/3, exportIcon.getIconHeight()/3, 0)));
	private JLabel wordLabel;
	private JButton saveButton;
	private JButton disposeButton;
	
	// input screen object used to get passed information
	private InputScreen inputScreen;

	// constructor of result screen takes in input screen, correct char variable, and calls other methods
	public ResultScreen(InputScreen inputScreen, String selectedWord) {

		this.selectedWord = selectedWord;
		this.inputScreen = inputScreen;

		addJComponents();
		CustomizationTool.addMenuBar(this);
		CustomizationTool.frameSetUp(this);
		CustomizationTool.setCustomCursor(this);

	}

	// method that adds other JComponents to the frame
	private void addJComponents() {

		// set up the panel by using the static method in the Customization tool
		CustomizationTool.panelSetUp(this, panel);

		// create a new JLabel, set a custom font, center on screen, set foreground color, and set location and size
		selectedCharacterLabel = new JLabel(new ImageIcon(inputScreen.getInputImage()));
		selectedCharacterLabel.setFont(new Font("Book Antiqua", Font.ITALIC | Font.BOLD, 300));
		selectedCharacterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		selectedCharacterLabel.setForeground(Color.WHITE);
		selectedCharacterLabel.setOpaque(true);
		selectedCharacterLabel.setBounds(CustomizationTool.programWidth / 2 - CustomizationTool.selectButtonDimension / 2,
				CustomizationTool.programHeight / 2 - CustomizationTool.selectButtonDimension / 2, CustomizationTool.selectButtonDimension,
				CustomizationTool.selectButtonDimension);
		
		// add this component to the panel
		panel.add(selectedCharacterLabel);
		
		wordLabel = new JLabel("text: " + selectedWord);
		wordLabel.setBounds(0, 115, CustomizationTool.programWidth, 50);
		wordLabel.setFont(new Font("Book Antiqua", Font.ITALIC, 18));
		wordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		wordLabel.setForeground(Color.WHITE);
		wordLabel.setBackground(Color.BLACK);
		wordLabel.setOpaque(true);
		panel.add(wordLabel);
		
		exportLabel.setBounds(CustomizationTool.programWidth/2 - exportLabel.getIcon().getIconWidth()/2, 75, 
				exportLabel.getIcon().getIconWidth(), exportLabel.getIcon().getIconHeight());
		panel.add(exportLabel);

		saveButton = new JButton(new ImageIcon(saveIcon.getImage().getScaledInstance(
				saveIcon.getIconWidth()/3, saveIcon.getIconHeight()/3, 0)));
		saveButton.setBounds(CustomizationTool.programWidth/2 - saveButton.getIcon().getIconWidth() - 100, 620, 
				saveButton.getIcon().getIconWidth(), saveButton.getIcon().getIconHeight());
		
		// enable button action on this frame for this JComponent
		saveButton.addActionListener(this);
		panel.add(saveButton);
		
		disposeButton = new JButton(new ImageIcon(disposeIcon.getImage().getScaledInstance(
				disposeIcon.getIconWidth()/3, disposeIcon.getIconHeight()/3, 0)));
		disposeButton.setBounds(CustomizationTool.programWidth/2 + 30, 625, 
				disposeButton.getIcon().getIconWidth(), disposeButton.getIcon().getIconHeight());
		disposeButton.addActionListener(this);
		panel.add(disposeButton);
		
	}

	// Override method from ActionListner class, used for JComponent action detections
	@Override
	public void actionPerformed(ActionEvent event) {

		// button that saves the input image and returns to input screen
		if(event.getSource() == saveButton) {
			
			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");
			
			// loop through every character for the inputed word
			for(int i = 0; i < inputScreen.getWordList().size(); i++) {
			
				// save that character at that character's directory 
				CustomizationTool.exportFile(inputScreen.getWordList().get(i), 
						"images/"+ selectedWord.charAt(i) + "/" + selectedWord.charAt(i) + "-" + trackImage(selectedWord.charAt(i)) + ".jpg");
			
			}
			
			// opens the input screen and closes this frame and the input screen taken in
			new InputScreen();
			inputScreen.dispose();
			this.dispose();
			
		}
		
		// button that discards the input image and returns to input screen
		else if(event.getSource() == disposeButton) {
			
			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");
			
			// opens the input screen and closes this frame and the input screen taken in
			new InputScreen();
			inputScreen.dispose();
			this.dispose();
			
		}
		
	}
	
	// method that tracks what is the next number to save for character in parameter to avoid files with duplicate names
	private int trackImage(char characterIndex) {

		int newOutputIndex = 0;
		
		// location to save the character
		File tempFolder = new File("images/" + characterIndex);
		
		// test if the location exist
		if (tempFolder.isDirectory()) { // make sure it's a directory

			// for every file in the directory of the character passed in, find the last file number and return it
			for (File tempFile : tempFolder.listFiles(CustomizationTool.imageFilter)) {
				
				String inputFileLocation = tempFile.getAbsolutePath();

				newOutputIndex = Math.max(newOutputIndex, (int)(inputFileLocation.charAt(inputFileLocation.length() - 5))-48);

			}
			
		}
		
		return newOutputIndex+1;
		
	}

}