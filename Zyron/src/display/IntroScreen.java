package display;

import java.awt.Color;
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
 * The Intro Screen class guides the user to proceed to the input screen by providing simple instructions
 * Uses static methods from the CustomizationTool class
 * Extends JFrame and implements ActionListener for button controls
 */
public class IntroScreen extends JFrame implements ActionListener {
	
	// image icon variables to be used for JComponents
	private ImageIcon beginIcon = new ImageIcon("utility/begin.png");
	private ImageIcon logoIcon = new ImageIcon("utility/logo.png");
	private ImageIcon headingIcon = new ImageIcon("utility/heading.png");
	private ImageIcon helpIcon = new ImageIcon("utility/help.png");
	private ImageIcon exitIcon = new ImageIcon("utility/exit.png");
	
	// JComponent declarations
	private JPanel panel = new JPanel();
	private JButton startButton  = new JButton(new ImageIcon(beginIcon.getImage().getScaledInstance(
			beginIcon.getIconWidth()/3, beginIcon.getIconHeight()/3, 0)));
	private JButton helpButton  = new JButton(new ImageIcon(helpIcon.getImage().getScaledInstance(
			helpIcon.getIconWidth()/3, helpIcon.getIconHeight()/3, 0)));
	private JButton exitButton  = new JButton(new ImageIcon(exitIcon.getImage().getScaledInstance(
			exitIcon.getIconWidth()/3, exitIcon.getIconHeight()/3, 0)));
	private JLabel logo = new JLabel(logoIcon);
	private JLabel heading = new JLabel(new ImageIcon(headingIcon.getImage().getScaledInstance(
			headingIcon.getIconWidth()/2, headingIcon.getIconHeight()/2, 0)));
	
	// constructor of intro screen calls other methods
	public IntroScreen() {
		
		addJComponents();
		CustomizationTool.addMenuBar(this);
		CustomizationTool.frameSetUp(this);
		CustomizationTool.setCustomCursor(this);
		CustomizationTool.playBackgroundMusic();
		
	}
	
	// method that adds all the JComponents to the frame
	private void addJComponents() {
		
		// set up the panel by using the static method in the Customization tool
		CustomizationTool.panelSetUp(this, panel);

		// set the location and size of this JComponent
		startButton.setBounds(CustomizationTool.programWidth/2 - startButton.getIcon().getIconWidth()/2, 430, 
				startButton.getIcon().getIconWidth(), startButton.getIcon().getIconHeight());
		
		// enable button action on this frame for this JComponent
		startButton.addActionListener(this);
		
		// add this JComponent to the panel
		panel.add(startButton);
		
		helpButton.setBounds(CustomizationTool.programWidth/2 - helpButton.getIcon().getIconWidth()/2, 500, 
		helpButton.getIcon().getIconWidth(), helpButton.getIcon().getIconHeight());
		helpButton.addActionListener(this);
		panel.add(helpButton);
		
		exitButton.setBounds(CustomizationTool.programWidth/2 - exitButton.getIcon().getIconWidth()/2, 570, 
		exitButton.getIcon().getIconWidth(), exitButton.getIcon().getIconHeight());
		exitButton.addActionListener(this);
		panel.add(exitButton);
				
		heading.setBounds(CustomizationTool.programWidth/2 - heading.getIcon().getIconWidth()/2, 300,
				heading.getIcon().getIconWidth(), heading.getIcon().getIconHeight());
		panel.add(heading);
		
		logo.setBounds(CustomizationTool.programWidth/2 - logo.getIcon().getIconWidth()/2, 150,
				logo.getIcon().getIconWidth(), logo.getIcon().getIconHeight());
		panel.add(logo);
		
	}
	
	// Override method from ActionListner class, used for JComponent action detections
	@Override
	public void actionPerformed(ActionEvent event) {
		
		// if start button is pressed, this statement will execute
		if(event.getSource() == startButton) {
			
			// if the global audio player is enabled, play the button sound effect
			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");
			
			// create a input screen and close the current frame
			new InputScreen();
			this.dispose();
			
		}
		
		else if(event.getSource() == helpButton) {
			
			if(CustomizationTool.audioPlaying)
				AudioPlayer.playAudio("utility/button.wav");
			
			// opens a new help screen
			new HelpScreen();
			
		}
		
		else if(event.getSource() == exitButton) {
			
			// quit the program
			System.exit(1);
			
		}
		
	}

}
