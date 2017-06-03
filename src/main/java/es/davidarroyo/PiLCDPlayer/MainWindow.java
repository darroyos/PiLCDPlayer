package es.davidarroyo.PiLCDPlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.*;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 655909294400809589L;
	
	private static MainWindow mainWindow = null;
	
	/**
	 * @return The unique instance of the window (Singleton pattern)
	 */
	public static MainWindow getMainWindow() {
		if (mainWindow == null) {
			mainWindow = new MainWindow();
		}
		
		return mainWindow;
	}
	
	private MainWindow() {
		super("PiLCDPlayer by David Arroyo");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			System.out.println("Error with the system look and feel configuration");
		}
		
		
		/*
		 * MAIN PANEL
		 -------------------------------------*/
		JPanel container = new JPanel(new BorderLayout());
		
		this.setContentPane(container);
		
		JPanel main = new JPanel();
		main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		BoxLayout mainLayout = new BoxLayout(main, BoxLayout.Y_AXIS);
		main.setLayout(mainLayout);
		
		JPanel logo = new JPanel();
		logo.add(new JLabel(Utils.createImage("images/logo.png")));
		main.add(logo);
		
		main.add(new VideoSearch());
		
		main.add(new Configuration());
		
		JPanel playPanel = new JPanel();
		JButton play = new JButton("Play video!");
		play.setIcon(Utils.createImage("images/play.png", 32, 32));
		playPanel.add(play);
		
		main.add(playPanel);
		
		container.add(main, BorderLayout.NORTH);
		
		/*
		 * FOOTER CREDITS
		 -------------------------------------*/
		JPanel credits = new JPanel(new FlowLayout());
		credits.setBackground(Color.BLACK);
		
		JLabel creditsLabel1 = new JLabel("Made with ");
		creditsLabel1.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		creditsLabel1.setForeground(Color.WHITE);
		credits.add(creditsLabel1);
		credits.add(new JLabel(Utils.createImage("images/love.png", 20, 18)));
		JLabel creditsLabel2 = new JLabel(" by David Arroyo");
		creditsLabel2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		creditsLabel2.setForeground(Color.WHITE);
		credits.add(creditsLabel2);
		
		container.add(credits, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(null);  // *** this will center your app ***
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private class VideoSearch extends JPanel {
		
		private static final long serialVersionUID = -2320232571570583484L;

		public VideoSearch() {
			String videoSearchTitle = "Video search";
			this.setBorder(BorderFactory.createTitledBorder(videoSearchTitle));
			
			JPanel searchBoxPanel = new JPanel(new FlowLayout());
			
			JLabel searchLabel = new JLabel("Video title: ");
			searchLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
			searchBoxPanel.add(searchLabel);
			
			// Search text field
			JTextField searchField = new JTextField(20);
			searchField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
			searchBoxPanel.add(searchField);
			
			// Search icon
			JLabel searchIcon = new JLabel(Utils.createImage("images/search_icon.png", 42, 42));
			searchIcon.setToolTipText("Type the video you're looking for!");
			searchBoxPanel.add(searchIcon);
			
			this.add(searchBoxPanel);
		}
	}

	private class Configuration extends JPanel {
		
		private static final long serialVersionUID = -6774253623210482170L;

		public Configuration() {
			String configurationTitle = "Player configuration";
			this.setBorder(BorderFactory.createTitledBorder(configurationTitle));
			
			BoxLayout confLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
			this.setLayout(confLayout);
			
			// LCD ENABLE/DISABLE
			// --------------------------------------------------------
			JPanel enableLCDTitle = new JPanel();
			JLabel enableLabel = new JLabel("Enable/disable LCD display");
			enableLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
			enableLCDTitle.add(enableLabel);
			this.add(enableLCDTitle);
			
			JPanel enableButtons = new JPanel(new FlowLayout());
			
			JRadioButton enable = new JRadioButton("Enable");
			JRadioButton disable = new JRadioButton("Disable");
			disable.setSelected(true);
			
			ButtonGroup group = new ButtonGroup();
			group.add(enable);
			group.add(disable);
			
			enableButtons.add(enable);
			enableButtons.add(disable);
			
			this.add(enableButtons);
			
			this.add(new JSeparator());
			
			// I2C BUS TEXT FIELD
			// --------------------------------------------------------
					
			JPanel busFieldPanel = new JPanel(new FlowLayout());
			
			JLabel busLabel = new JLabel("I2C Bus: ");
			busLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
			busFieldPanel.add(busLabel);
			
			JTextField busField = new JTextField(5);
			busField.setText("1");
			busField.setToolTipText("Default value for most devices (newer ones)");
			busFieldPanel.add(busField);
			this.add(busFieldPanel);
			
			JPanel busHelpTitle = new JPanel();
			JLabel busHelpLabel = new JLabel("0: original Pi, 1: Rev 2 and newers");
			busHelpLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
			busHelpTitle.add(busHelpLabel);
			this.add(busHelpTitle);
			
			this.add(new JSeparator());
			
			// LCD ADDRESS
			// --------------------------------------------------------	
			JPanel addrFieldPanel = new JPanel(new FlowLayout());
			
			JLabel addrLabel = new JLabel("LCD Address: ");
			addrLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
			addrFieldPanel.add(addrLabel);
			
			JTextField addrField = new JTextField(15);
			addrField.setText("0x27");
			addrField.setToolTipText("Default value for most devices");
			addrFieldPanel.add(addrField);
			this.add(addrFieldPanel);
		}
	}
}
