package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import display.HelpScreen;
import display.IntroScreen;

/*
 * Author: Alan Sun
 * 
 * Customization Tool class includes all the static methods that will be used in one or more classes
 * Contains methods that resizes and crops a buffered image
 * Able to set up a frame, panel and create a menu bar for that frame
 * Class can save file and load file from a directory
 * Controls music and sound effects
 */
public class CustomizationTool {

	// size variables 
	public static int programWidth = 850;
	public static int programHeight = 750;
	public static int selectButtonDimension = 400;
	public static int totalPixels = (int) Math.pow(selectButtonDimension, 2);

	// sound control variables
	public static boolean musicPlaying = true;
	public static boolean audioPlaying = true;
	
	// minimal color value to be considered dark
	public static int darkValue = 50;

	// available extensions to load from
	public static final String[] validExtensions = new String[] {"jpg"};

	// method that sets up a frame
	public static void frameSetUp(JFrame frame) {

		// set the size and center the frame on the screen
		frame.setSize(programWidth, programHeight);
		frame.setLocationRelativeTo(null);
		
		// allows the frame to close when program closes
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// give the program the title, disable auto layout tool and allow the frame to be seen
		frame.setTitle("Zyron");
		frame.setLayout(null);
		frame.setVisible(true);

	}

	// method that sets up a panel
	public static void panelSetUp(JFrame frame, JPanel panel) {

		// disable auto layout, set background color and set the size and location
		panel.setLayout(null);
		panel.setBackground(Color.black);
		panel.setBounds(0, 0, CustomizationTool.programWidth, CustomizationTool.programHeight);
		
		// add the panel to the frame
		frame.add(panel);

	}

	// method that detects if the inputed file is accepted by matching the filter
	public static final FilenameFilter imageFilter = new FilenameFilter() {

		// method that loops though all the available extensions and returns true if file has the extension
		@Override
		public boolean accept(final File dir, final String name) {

			for (final String ext : validExtensions) {

				if (name.endsWith("." + ext)) {

					return (true);

				}

			}

			return (false);

		}
	};

	// method that resizes a buffered image given a dimension
	public static BufferedImage resize(BufferedImage inputImage, int width, int height) {

		// creates output image
		BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

		// scales the input image to the output image
		Graphics2D g2d = outputImage.createGraphics();
		g2d.drawImage(inputImage, 0, 0, width, height, null);
		g2d.dispose();

		return outputImage;

	}

	// method that crops a buffered image so that the text touches all 4 sides of the image
	public static BufferedImage cropToFit(BufferedImage inputImage) {

		// initial starting positions
		int startX = 0;
		int startY = 0;
		int endX = 0;
		int endY = 0;

		// initial conditions
		boolean startXFound = false;
		boolean startYFound = false;
		boolean endXFound = false;
		boolean endYFound = false;

		// loop though every pixel of the image
		for (int i = 0; i < inputImage.getHeight(); i++) {
			for (int j = 0; j < inputImage.getWidth(); j++) {

				// loop through a row or column and get the RGB color or that pixel
				Color topPixel = new Color(inputImage.getRGB(j, i));
				Color leftPixel = new Color(inputImage.getRGB(i, j));
				Color bottomPixel = new Color(inputImage.getRGB(j, inputImage.getHeight() - 1 - i));
				Color rightPixel = new Color(inputImage.getRGB(inputImage.getWidth() - 1 - i, j));

				// if any of the pixels found are dark enough, then crop start/end a that row/column
				if (topPixel.getBlue() < darkValue && topPixel.getGreen() < darkValue && topPixel.getRed() < darkValue && !startYFound) {
					startY = i;
					startYFound = true;
				}
				if (leftPixel.getBlue() < darkValue && leftPixel.getGreen() < darkValue && leftPixel.getRed() < darkValue && !startXFound) {
					startX = i;
					startXFound = true;
				}
				if (bottomPixel.getBlue() < darkValue && bottomPixel.getGreen() < darkValue && bottomPixel.getRed() < darkValue
						&& !endYFound) {
					endY = inputImage.getHeight() - i;
					endYFound = true;
				}
				if (rightPixel.getBlue() < darkValue && rightPixel.getGreen() < darkValue && rightPixel.getRed() < darkValue && !endXFound) {
					endX = inputImage.getWidth() - i;
					endXFound = true;
				}

			}
		}

		// return a cropped image or the parent image
		return inputImage.getSubimage(startX, startY, endX - startX, endY - startY);

	}

	// method that converts a scaled image to a 2D boolean array
	public static boolean[][] to2DBoolean(BufferedImage inputIcon) {

		boolean[][] inputPixels = new boolean[inputIcon.getWidth()][inputIcon.getHeight()];

		// loop though the buffered image and get the color at each pixel
		for (int i = 0; i < inputIcon.getWidth(); i++) {
			for (int j = 0; j < inputIcon.getWidth(); j++) {

				Color colorPixel = new Color(inputIcon.getRGB(i, j));

				// if pixel is not transparent and darker than the accepted value, then set index as true in the 2D array
				if (colorPixel.getBlue() < darkValue && colorPixel.getGreen() < darkValue && colorPixel.getRed() < darkValue 
						&& colorPixel.getAlpha() != 0)

					inputPixels[i][j] = true;
			
				else

					inputPixels[i][j] = false;

			}

		}

		return inputPixels;

	}

	// method that saves a buffered image to a directory
	public static void exportFile(BufferedImage outputImage, String directory) {
		
		//load a file from the directory in the directory and see if it exist
		File outputFile = new File(directory);

		try {

			// if the directory exist, save the output image as a jpg
			ImageIO.write(outputImage, "jpg", outputFile);

		} catch (IOException error) {

			System.out.println("no directory found with given name");

		}

	}

	// method that loads a word and detect where to crop it into characters
	public static ArrayList<BufferedImage> loadWord(BufferedImage wordImage) {

		ArrayList<BufferedImage> wordList = new ArrayList<BufferedImage>();

		// variables to keep track of pixel count of current and previous column
		boolean previousWhiteColumn = true;
		int whitePixelCount = 0;

		// traverse through the buffered image by pixels and find out the color
		for (int i = 0; i < wordImage.getWidth(); i++) {
			for (int j = 0; j < wordImage.getHeight(); j++) {

				Color colorPixel = new Color(wordImage.getRGB(i, j));

				// if pixel is transparent or not dark enough, it is considered as a white pixel
				if ((colorPixel.getBlue() > darkValue && colorPixel.getGreen() > darkValue && colorPixel.getRed() > darkValue)
						|| colorPixel.getAlpha() == 0) {

					whitePixelCount++;

				}

				else {

					// if the pixel is dark enough, then this column is no longer considered white
					previousWhiteColumn = false;
					break;

				}

			}

			// if the entire column is white, it is where to crop a character out
			if (whitePixelCount == wordImage.getHeight() && !previousWhiteColumn) {

				previousWhiteColumn = true;
				wordList.add(wordImage.getSubimage(0, 0, i, wordImage.getHeight()));
				wordImage = wordImage.getSubimage(i, 0, wordImage.getWidth() - i, wordImage.getHeight());
				i = 0;

			}

			whitePixelCount = 0;

		}

		return wordList;

	}

	// method that adds all the menu items
	public static void addMenuBar(JFrame frame) {

		// create a new JMenuBar for the frame that stores different menus 
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		// create a new control menu and add it to the menu bar 
		JMenu controlMenu = new JMenu("Control");
		menuBar.add(controlMenu);

		// creating a menu item
		JMenuItem restartOption = new JMenuItem("Return to Menu");

		// add an action listener for this menu item
		restartOption.addActionListener(new ActionListener() {

			// method that executes after menu item is clicked
			@Override
			public void actionPerformed(ActionEvent e) {

				if (audioPlaying)
					AudioPlayer.playAudio("utility/button.wav");

				MusicPlayer.stopMusic();

				// opens a new intro screen and closes this frame
				new IntroScreen();
				frame.dispose();

			}
		});

		// add this menu item to the control menu
		controlMenu.add(restartOption);

		// creating the exit option under the control menu
		JMenuItem exitOption = new JMenuItem("Exit Program");

		exitOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (CustomizationTool.audioPlaying)
					AudioPlayer.playAudio("utility/button.wav");

				// exits the program
				System.exit(1);

			}
		});

		controlMenu.add(exitOption);

		// the help menu will include all the help related menu items
		JMenu helpMenu = new JMenu("Help");

		menuBar.add(helpMenu);

		// the description menu item will specify the screen descriptions and controls
		JMenuItem descriptionOption = new JMenuItem("Description");
		
		descriptionOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				new HelpScreen();

			}

		});

		helpMenu.add(descriptionOption);

		// this drop down menu contains all the sound functions
		JMenu audioMenu = new JMenu("Audio");

		menuBar.add(audioMenu);

		// this menu item allows the user to disable music
		JMenuItem disableMusicOption = new JMenuItem("Disable Background Music");
		disableMusicOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// stops the music from playing
				MusicPlayer.stopMusic();

			}

		});

		// add controlOption to the help menu
		audioMenu.add(disableMusicOption);

		// this menu item allows the user to play a random Music music
		JMenuItem enableMusicOption = new JMenuItem("Enable Background Music");
		enableMusicOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// stops the music, if there are any
				if (musicPlaying)
					MusicPlayer.stopMusic();

				playBackgroundMusic();

				musicPlaying = true;

			}

		});

		audioMenu.add(enableMusicOption);

		// this menu item allows the user to play a random Music music
		JMenuItem disableSFXOption = new JMenuItem("Disable Sound Effect");
		disableSFXOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// disabling all sounds by turning sound playing into false
				audioPlaying = false;

			}

		});

		audioMenu.add(disableSFXOption);

		// this menu item allows the user to play a random Music music
		JMenuItem enableSFXOption = new JMenuItem("Enable Sound Effect");
		enableSFXOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// enable sound effects to play for this screen
				AudioPlayer.playAudio("utility/button.wav");
				audioPlaying = true;

			}

		});

		// add controlOption to the help menu
		audioMenu.add(enableSFXOption);

	}

	// this method plays the background music for the report screen
	public static void playBackgroundMusic() {

		// plays the selected music using the music player
		MusicPlayer.playMusic("utility/chat.wav");

	}
	
	// method that changes the cursor icon using a java Toolkit
	public static void setCustomCursor(JFrame frame) {

		Toolkit toolkit = Toolkit.getDefaultToolkit();

		// load an image using ToolKit
		Image mouse = toolkit.getImage("utility/cursor.png");

		// set the cursor icon giving a new image, point, and name
		frame.setCursor(toolkit.createCustomCursor(mouse, new Point(0, 0), "Custom Cursor"));

	}

}
