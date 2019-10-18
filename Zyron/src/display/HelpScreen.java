package display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.AudioPlayer;
import utils.CustomizationTool;

/*
 * Author: Alan Sun
 * 
 * Help Screen displays the help information for the program
 * Uses static methods from the CustomizationTool class that applies to all screens
 * Extends JFrame and implements ActionListener for button controls
 */
public class HelpScreen extends JFrame implements ActionListener {
	
	// image icons used in this frame
	private ImageIcon[] helpScreens = new ImageIcon[6];
	private ImageIcon nextIcon = new ImageIcon("utility/next.png");
	private ImageIcon backIcon = new ImageIcon("utility/back.png");
	
	// index tracking the current help screen
	private int currentHelpIndex = 0;
	
	// JComponents used in this frame
	private JPanel panel = new JPanel();
	private JLabel screenLabel = new JLabel(new ImageIcon("utility/help0.png"));
	private JButton nextButton = new JButton(new ImageIcon(nextIcon.getImage().getScaledInstance(
			nextIcon.getIconWidth()/3, nextIcon.getIconHeight()/3, 0)));
	private JButton backButton = new JButton(new ImageIcon(backIcon.getImage().getScaledInstance(
			backIcon.getIconWidth()/3, backIcon.getIconHeight()/3, 0)));
	
	// constructor of help screen calls other methods
	public HelpScreen() {
		
		addHelpScreens();
		addJComponents();
		CustomizationTool.addMenuBar(this);
		CustomizationTool.frameSetUp(this);
		CustomizationTool.setCustomCursor(this);
		
	}
	
	// method that adds other JComponents to the frame
	private void addJComponents() {
		
		CustomizationTool.panelSetUp(this, panel);
		
		// set the location and size of this JComponent, enable button action, and add it to the panel
		nextButton.setBounds(CustomizationTool.programWidth - 170, CustomizationTool.programHeight - 100, 
				nextButton.getIcon().getIconWidth(), nextButton.getIcon().getIconHeight());
		nextButton.addActionListener(this);
		panel.add(nextButton);
		
		backButton.setBounds(50, CustomizationTool.programHeight - 100, 
				backButton.getIcon().getIconWidth(), backButton.getIcon().getIconHeight());
		backButton.addActionListener(this);
		panel.add(backButton);
		
		screenLabel.setBounds(0, -30, CustomizationTool.programWidth, CustomizationTool.programHeight);
		panel.add(screenLabel);
		
		
		
	}

	// method that fills the help screen array with all the help screen images
	private void addHelpScreens() {
		
		for(int i = 0; i < 6; i++) {
			
			helpScreens[i] = new ImageIcon("utility/help" + i + ".png");
			
		}
		
	}
	
	// Override method from ActionListner class, used for JComponent action detections
	@Override
	public void actionPerformed(ActionEvent event) {

		// test which button is being pressed
		if(event.getSource() == nextButton) {
			
			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");
			
			// goes to the next help image by incrementing the index
			currentHelpIndex++;
			
			// test if the last help image has been reached
			if(currentHelpIndex < helpScreens.length) {
				
				screenLabel.setIcon(helpScreens[currentHelpIndex]);

				// repaint the panel just in case image didn't update
				panel.repaint();
				
			}
			
			else {
				
				// if all informations have been read, then close this frame
				this.dispose();
				
			}
			
		}
		
		else if(event.getSource() == backButton) {
			
			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");
		     
			// goes to previous help image by incrementing the index
			currentHelpIndex--;
			
			// if player goes before the first screen, help screen quits
			if(currentHelpIndex >= 0) {
				
				screenLabel.setIcon(helpScreens[currentHelpIndex]);

				panel.repaint();
				
			}
			
			else {
				
				this.dispose();
				
			}
			
		}
		
	}

}
