package display;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import utils.AudioPlayer;
import utils.CustomizationTool;

/*
 * Author: Alan Sun
 * 
 * Input Screen class allows the user to input a jpg file with written text to be converted to digital text
 * Uses multidimensional arrays and hash maps to perform pattern recognition
 * Uses static methods from the CustomizationTool class that applies to all screens
 * Uses a customized comparator to sort the <Character, Double> hash map
 * Extends JFrame and implements ActionListener for button controls
 */
public class InputScreen extends JFrame implements ActionListener {
	
	// pattern directory array is used to store all the folders from 'a' to 'z'
	private static final File[] patternDirectory = new File[26];
	
	// variable that tracks what percentage of files are converted
	private int percentageCharacterConverted;
	
	// variables and lists used to store and process images for pattern detection
	private File inputFile;
	private BufferedImage inputImage;
	private ArrayList<BufferedImage> wordList = new ArrayList<BufferedImage>();
	private ArrayList<HashMap<Character, Double>> characterHierarchy = new ArrayList<HashMap<Character, Double>>();
	private ArrayList<boolean[][]> inputPixels = new ArrayList<boolean[][]>();

	// image icon variables to be used for JComponents
	private ImageIcon convertIcon = new ImageIcon("utility/convert.png");
	private ImageIcon inputIcon = new ImageIcon("utility/input.png");
	private ImageIcon loadingIcon = new ImageIcon("utility/loading.gif");
	private ImageIcon loadingTitleIcon = new ImageIcon("utility/loadingIcon.png");
	
	// JComponents
	private JPanel panel = new JPanel();
	private JButton selectButton = new JButton("click to choose file");
	private JButton convertButton = new JButton(new ImageIcon(convertIcon.getImage().getScaledInstance(
			convertIcon.getIconWidth()/3, convertIcon.getIconHeight()/3, 0)));
	private JLabel inputLabel = new JLabel(new ImageIcon(inputIcon.getImage().getScaledInstance(
			inputIcon.getIconWidth()/3, inputIcon.getIconHeight()/3, 0)));
	private JLabel loadingLabel = new JLabel(new ImageIcon(loadingIcon.getImage().getScaledInstance(
			CustomizationTool.programWidth, CustomizationTool.programHeight, 0)));
	private JLabel loadingPercentageLabel = new JLabel();

	// constructor of input screen calls other methods
	public InputScreen() {

		// other methods being called
		addJComponents();
		CustomizationTool.addMenuBar(this);
		CustomizationTool.frameSetUp(this);
		CustomizationTool.setCustomCursor(this);
		
	}

	// method that adds all the JComponents to the frame
	private void addJComponents() {

		// set up the panel by using the static method in the Customization tool
		CustomizationTool.panelSetUp(this, panel);
		
		// set the location and size of this JComponent
		selectButton.setBounds(CustomizationTool.programWidth / 2 - CustomizationTool.selectButtonDimension / 2,
				CustomizationTool.programHeight / 2 - CustomizationTool.selectButtonDimension / 2, CustomizationTool.selectButtonDimension,
				CustomizationTool.selectButtonDimension);
		
		// enable button action on this frame for this JComponent
		selectButton.addActionListener(this);
		
		// add this JComponent to the panels
		panel.add(selectButton);

		convertButton.setBounds(CustomizationTool.programWidth/2 - convertButton.getIcon().getIconWidth()/2, 620, 
				convertButton.getIcon().getIconWidth(), convertButton.getIcon().getIconHeight());

		convertButton.addActionListener(this);
		panel.add(convertButton);
		
		inputLabel.setBounds(CustomizationTool.programWidth/2 - inputLabel.getIcon().getIconWidth()/2, 75, 
				inputLabel.getIcon().getIconWidth(), inputLabel.getIcon().getIconHeight());
		panel.add(inputLabel);
		
		loadingPercentageLabel.setBounds(CustomizationTool.programWidth / 2 - 250, CustomizationTool.programHeight - 150, 500, 50);
		
		// set a custom font, layout, and size for the buttons
		loadingPercentageLabel.setFont(new Font("impact", Font.BOLD | Font.ITALIC, 36));
		loadingPercentageLabel.setForeground(Color.WHITE);
		loadingPercentageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel.add(loadingPercentageLabel);

	}

	// Override method from ActionListner class, used for JComponent action detections
	@Override
	public void actionPerformed(ActionEvent event) {

		// if structures detecting which button is pressed
		if (event.getSource() == selectButton) {

			// if audio is enabled play sound effect
			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");
			
			// attach a file chooser to the button with file filters
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("jpg files", 
					"jpg");
			chooser.setFileFilter(fileFilter);
			
			// disable select options for other file types besides jpg
			chooser.setAcceptAllFileFilterUsed(false);
			
			// open the file chooser and store the file type as an integer
			int selectedFile = chooser.showOpenDialog(this);

			// check if file is approved by the filter
			if (selectedFile == JFileChooser.APPROVE_OPTION) {

				if(CustomizationTool.audioPlaying)
					AudioPlayer.playAudio("utility/button.wav");
				
				inputFile = chooser.getSelectedFile();

				// try and catch to see if file being read in exist
				try {

					BufferedImage loadedImage = ImageIO.read(inputFile);
					
					// the input image is a resized version of the loaded image to fit the button size
					inputImage = CustomizationTool.resize(loadedImage, CustomizationTool.selectButtonDimension, CustomizationTool.selectButtonDimension);
					
					// break the inputed word into characters
					wordList = CustomizationTool.loadWord(loadedImage);
					
					// loop through each character to crop the character from the file and resize it
					for(int i = 0; i < wordList.size(); i++) {
						
						wordList.set(i, CustomizationTool.resize(CustomizationTool.cropToFit(CustomizationTool.resize(wordList.get(i), 
								CustomizationTool.selectButtonDimension, CustomizationTool.selectButtonDimension)), 
								CustomizationTool.selectButtonDimension, CustomizationTool.selectButtonDimension));

					}
					
					selectButton.setIcon(new ImageIcon(inputImage));
					
					// repaint the panel just in case image change didn't update
					panel.repaint();
					
					// fill the base variables in the lists for the input image
					fillMatchingPixels();

				} catch (IOException error) {

					System.out.println("input file does not exsist");

				}

			} else {
				
			    // display message dialogue for invalid input
				JOptionPane.showMessageDialog(null,
						"Please validate your input\n"
						+ "input must be a .jpg file\n\n"
								+ "click 'ok' to continue...",
						"INVALID INPUT", JOptionPane.WARNING_MESSAGE);
				
			}

		}

		else if (event.getSource() == convertButton) {

			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");
			
			// test if input file exist
			if (inputFile != null) {
				
				inputLabel.setIcon(new ImageIcon(loadingTitleIcon.getImage().getScaledInstance(
						loadingTitleIcon.getIconWidth()/3, loadingTitleIcon.getIconHeight()/3, 0)));
				
				selectButton.setBounds(0, 0, 0, 0);
				
				convertButton.setBounds(0, 0, 0, 0);

				loadingLabel.setBounds(0, 0, loadingLabel.getIcon().getIconWidth(), loadingLabel.getIcon().getIconHeight());
				panel.add(loadingLabel);
				
				// repaint the panel just in case image change didn't update
				panel.repaint();
				
				// delay 500 milliseconds for the gif to load during conversion
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {

						startConversion();

					}
				}, 500);
				
			} 
			
			else
				
				// display message dialogue for invalid input
				JOptionPane.showMessageDialog(null,
						"Please validate your input\n" + "input must be in lowercase characters from 'a' to 'z'\n"
								+ "Please select a proper file in jpg in the box above\n\n" 
								+ "click 'ok' to continue...",
						"INVALID INPUT", JOptionPane.WARNING_MESSAGE);

		}

	}

	// method that creates a hash map and a 2D boolean pattern array for each character inputed
	private void fillMatchingPixels() {

		// traverse through all characters in the text and 
		for(int i = 0; i < wordList.size(); i++) {
			
			characterHierarchy.add(new HashMap<Character, Double>());
			
			for (int j = 0; j < 26; j++)
				
				characterHierarchy.get(i).put((char) (97 + j), 0.0);
			
			inputPixels.add(CustomizationTool.to2DBoolean(wordList.get(i)));
			
		}

	}

	// method that converts each character of the inputed text to a suspected word and opens a new assumption screen
	private void startConversion() {
		
		// for loop looping though every character in the inputed text to detect patterns 
		for(int i = 0; i < characterHierarchy.size(); i++) {
			
			// calculates what percentage of text is converted and set the label to that number
			percentageCharacterConverted = (int)Math.round((double)(i+1)/characterHierarchy.size() * 100);
			
			// display different text at different conversion percentage
			if(percentageCharacterConverted != 100)
				
				loadingPercentageLabel.setText(percentageCharacterConverted + "% characters converted");
			
			else
				
				loadingPercentageLabel.setText(percentageCharacterConverted + "% finishing...");
			
			// store a percentage match map for the current character
			characterHierarchy.set(i, fillMap(characterHierarchy.get(i), inputPixels.get(i)));
			
		}
		
		//opens the assumption screen and closes this frame
		new AssumptionScreen(this, characterHierarchy);
		this.dispose();
		
	}
	
	// method that fills a hash map with keys from 'a' to 'z' and values with the max percentage pixel matched
	private HashMap<Character, Double> fillMap(HashMap<Character, Double> characterHierarchy, boolean[][] inputPixels) {
		
		// creating a for loop to compare all 26 characters in the alphabet with the input image
		for (int i = 0; i < 26; i++) {

			char currentCharacter = (char) (97 + i);
			patternDirectory[i] = new File("images/" + currentCharacter);

			// initialize a variable that keeps track of the current character matching count
			int numPixelMatchCount = 0;

			// makes sure the the directory being accessed is available
			if (patternDirectory[i].isDirectory()) { 

				// loop through every existing file within the directory
				for (File file : patternDirectory[i].listFiles(CustomizationTool.imageFilter)) {

					// try and catch to ensure that the image being loaded exist
					try {

						BufferedImage loadedImage = ImageIO.read(file);

						// resize the current image to a square to be displayed on the selecct button
						BufferedImage resizedImage = CustomizationTool.resize(loadedImage, CustomizationTool.selectButtonDimension, CustomizationTool.selectButtonDimension);

						// crop the image and scale it to make all the sides touch the wall
						BufferedImage croppedImage = CustomizationTool.resize(CustomizationTool.cropToFit(resizedImage),
								CustomizationTool.selectButtonDimension, CustomizationTool.selectButtonDimension);
						
						// convert to a boolean array to be compared with
						boolean orderedMapPixels[][] = CustomizationTool.to2DBoolean(croppedImage);

						// nested for loop to count the pixels matched
						for (int x = 0; x < orderedMapPixels.length; x++)
							for (int y = 0; y < orderedMapPixels[i].length; y++)
								if (inputPixels[x][y] == orderedMapPixels[x][y])

									numPixelMatchCount++;

						double percentageMatch = (double) numPixelMatchCount / CustomizationTool.totalPixels * 100;
						
						// if the new match percentage is higher, replace the old one
						if (characterHierarchy.get(currentCharacter) < percentageMatch) 
							characterHierarchy.put(currentCharacter, percentageMatch);

						numPixelMatchCount = 0;

					} catch (IOException error) {

						System.out.println("error in loading in buffered image");

					}

				}

			}

		}
		
		return characterHierarchy = sortByValue(characterHierarchy);
		
	}
	
	// method that sorts a hash map with character as key and double as value in descending order
    private HashMap<Character, Double> sortByValue(HashMap<Character, Double> unorderedMap) { 
        
    	// Create a sortList from elements of HashMap 
        LinkedList<Map.Entry<Character, Double>> sortList = 
               new LinkedList<Map.Entry<Character, Double> >(unorderedMap.entrySet()); 
  
        // Sort the sortList 
        Collections.sort(sortList, new Comparator<Map.Entry<Character, Double> >() { 
            public int compare(Map.Entry<Character, Double> value1, Map.Entry<Character, Double> value2) { 
            	
                return (value1.getValue()).compareTo(value2.getValue()); 
                
            } 
        }); 
        
        // reverse the sorted list
        Collections.reverse(sortList);
          
        // put data from sortList to a new hash map
        HashMap<Character, Double> orderedMap = new LinkedHashMap<Character, Double>(); 
        
        // loop through the sorted map to put all the keys and values into a new map to be returned
        for (Map.Entry<Character, Double> pair : sortList) { 
            orderedMap.put(pair.getKey(), pair.getValue()); 
        } 

        return orderedMap; 
        
    }

    //getters and setters
	public BufferedImage getInputImage() {
		return inputImage;
	}

	public void setInputImage(BufferedImage inputImage) {
		this.inputImage = inputImage;
	}

	public ArrayList<BufferedImage> getWordList() {
		return wordList;
	}

	public void setWordList(ArrayList<BufferedImage> wordList) {
		this.wordList = wordList;
	}

}
