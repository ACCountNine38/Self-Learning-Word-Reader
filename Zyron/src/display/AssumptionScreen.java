package display;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

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
 * Assumption Screen displays up to 10 top guesses for the word inputed
 * Uses static methods from the CustomizationTool class that applies to all screens
 * Uses a customized comparator to sort the <String, Double> hash map
 * Extends JFrame and implements ActionListener for button controls
 */
public class AssumptionScreen extends JFrame implements ActionListener {

	// image icons used in this frame
	private ImageIcon correctIcon = new ImageIcon("utility/correct.png");
	private ImageIcon guessIcon = new ImageIcon("utility/guess.png");
	private ImageIcon neitherIcon = new ImageIcon("utility/neither.png");
	
	// JComponents used in this frame
	private JPanel panel = new JPanel();
	private JLabel[] assumptionLabels = new JLabel[10];
	private JLabel guessLabel = new JLabel(new ImageIcon(guessIcon.getImage().getScaledInstance(
			guessIcon.getIconWidth()/3, guessIcon.getIconHeight()/3, 0)));
	private JButton[] assumptionButtons = new JButton[10];
	private JButton neitherButton = new JButton(new ImageIcon(neitherIcon.getImage().getScaledInstance(
			neitherIcon.getIconWidth()/3, neitherIcon.getIconHeight()/3, 0)));
	
	// lists to store character informations
	private HashMap<String, Double> topChoices = new HashMap<String, Double>();
	private ArrayList<HashMap<Character, Double>> characterHierarchy = new ArrayList<HashMap<Character, Double>>();
	
	// input screen object used to get passed information
	private InputScreen inputScreen;
	
	// constructor taking in 3 parameters, initializes variables, and calls other methods
	public AssumptionScreen(InputScreen inputScreen, ArrayList<HashMap<Character, Double>> characterHierarchy) {

		this.inputScreen = inputScreen;
		this.characterHierarchy = characterHierarchy;

		getTopChoices();
		addJComponents();
		CustomizationTool.addMenuBar(this);
		CustomizationTool.frameSetUp(this);
		CustomizationTool.setCustomCursor(this);
		
	}

	// method that adds other JComponents to the frame
	private void addJComponents() {

		// set up the panel by using the static method in the Customization tool
		CustomizationTool.panelSetUp(this, panel);

		// set the location of this JComponent and add it to the panel
		guessLabel.setBounds(CustomizationTool.programWidth/2 - guessLabel.getIcon().getIconWidth()/2, 40, 
				guessLabel.getIcon().getIconWidth(), guessLabel.getIcon().getIconHeight());
		panel.add(guessLabel);
		
		neitherButton.setBounds(CustomizationTool.programWidth/2 - neitherButton.getIcon().getIconWidth()/2, CustomizationTool.programHeight - 90, 
				neitherButton.getIcon().getIconWidth(), neitherButton.getIcon().getIconHeight());
		// enable button action on this frame for this JComponent
		neitherButton.addActionListener(this);
		panel.add(neitherButton);
		
		int count = 0;

		// place all 10 top choices to the screen, if it exists
		for (HashMap.Entry<String, Double> currentIndex : topChoices.entrySet()) {

			String text = currentIndex.getKey();
			assumptionLabels[count] = new JLabel(text);
			
			// set the font of this current JComponent and center text on the label
			assumptionLabels[count].setFont(new Font("Book Antiqua", Font.ITALIC, 24));
			assumptionLabels[count].setHorizontalAlignment(SwingConstants.CENTER);
			assumptionLabels[count].setBackground(Color.WHITE);
			
			// the optimal(first) option is highlighted
			if(count == 0) 
				assumptionLabels[count].setBackground(Color.YELLOW);
			
			// allows the customized layer to be seen and place it
			assumptionLabels[count].setOpaque(true);
			assumptionLabels[count].setBounds(CustomizationTool.programWidth/2 - 250, 55*count + 100, 200, 50);

			assumptionButtons[count] = new JButton(new ImageIcon(correctIcon.getImage().getScaledInstance(
					correctIcon.getIconWidth()/3, correctIcon.getIconHeight()/3, 0)));
			assumptionButtons[count].addActionListener(this);
			assumptionButtons[count].setBounds(CustomizationTool.programWidth/2 + 50, 55*count + 100, 200, 50);
			
			panel.add(assumptionLabels[count]);
			panel.add(assumptionButtons[count]);

			count++;

			// there can be max 10 similar words being displayed
			if (count == 10)
				break;

		}

	}
	
	// method that iterates through each word in the dictionary and give it points
	private void getTopChoices() {
		
		// try and catch to see if the dictionary file exist
		try {
			
			// load the file using a scanner to read it
			Scanner dictionary = new Scanner(new File("utility/dictionary.txt"));
			
			// iterate through each word in the dictionary
			while(dictionary.hasNext()) {
				
				String word = dictionary.nextLine();
				
				// word is only a match to the input image if the character count is the same
				if(word.length() == characterHierarchy.size()) {
					
					double characterPoints = 0;
					
					// calculate points for that word using each element in the characterHierarchy hash maps
					for(int i = 0; i < characterHierarchy.size(); i++) 
			
						// loop through the map to calculates points for each character
						for (Entry<Character, Double> hierarchyEntry : characterHierarchy.get(i).entrySet()) 
							if(word.charAt(i) == hierarchyEntry.getKey())
								
								characterPoints += hierarchyEntry.getValue();
						
					// add the word and the points to the top choices map
					topChoices.put(word, characterPoints);
					
				}

			}
			
		} catch (FileNotFoundException error) {
			
			System.out.println("File not found");
			
		}
		
		// sort the top choices map in ascending order
		topChoices = sortByValue(topChoices);
		
	}

	// Override method from ActionListner class, used for JComponent action detections
	@Override
	public void actionPerformed(ActionEvent event) {

		// looping through all the top choices
		for(int i = 0; i < assumptionButtons.length; i++) {
			
			if(event.getSource() == assumptionButtons[i]) {
				
				if(CustomizationTool.audioPlaying)
					AudioPlayer.playAudio("utility/button.wav");
				
				// opens the result screen and closes this screen if user clicks it
				new ResultScreen(inputScreen, assumptionLabels[i].getText());
				this.dispose();
				
			}
			
		}
		
		if(event.getSource() == neitherButton) {
			
			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");
			
			// opens the question screen and closes this frame
			new QuestionScreen(inputScreen, characterHierarchy.size());
			this.dispose();
			
		}
		
	}
	
	// method to sort map by values 
    public static HashMap<String, Double> sortByValue(HashMap<String, Double> unorderedMap) 
    { 
        // Create a sortList from elements of HashMap 
        LinkedList<Map.Entry<String, Double>> sortList = 
               new LinkedList<Map.Entry<String, Double> >(unorderedMap.entrySet()); 
  
        // Sort the sortList 
        Collections.sort(sortList, new Comparator<Map.Entry<String, Double>>() { 
            public int compare(Map.Entry<String, Double> value1, Map.Entry<String, Double> value2) { 
            	
                return (value1.getValue()).compareTo(value2.getValue()); 
                
            } 
        }); 
        
        // reverse the sorted list
        Collections.reverse(sortList);
          
        // put data from sortList to a new hash map
        HashMap<String, Double> orderedMap = new LinkedHashMap<String, Double>(); 
        
        // iterate though the sorted list and put it inside the new map
        for (Map.Entry<String, Double> pair : sortList) { 
            orderedMap.put(pair.getKey(), pair.getValue()); 
        } 

        return orderedMap; 
        
    }

}
