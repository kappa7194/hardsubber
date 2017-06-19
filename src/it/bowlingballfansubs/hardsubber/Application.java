package it.bowlingballfansubs.hardsubber;

import java.awt.Container;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

public class Application implements Runnable {

	@Override
	public void run() {
		final Properties properties = new Properties();

		try (FileInputStream propertiesFile = new FileInputStream("Hardsubber.properties")) {
			try {
				properties.load(propertiesFile);
			} catch (IOException exception) {
				exception.printStackTrace();
				return;
			}
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
			return;
		} catch (IOException exception) {
			exception.printStackTrace();
			return;
		}

		final String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();

		try {
			UIManager.setLookAndFeel(systemLookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exception) {
			exception.printStackTrace();
		}

		final JFrame application = new JFrame();
		final MigLayout layout = new MigLayout("", "[][grow][]", "[][][][][][grow]");
		final JLabel sourceFileLabel = new JLabel();
		final JLabel subtitlesFileLabel = new JLabel();
		final JLabel fontsDirectoryLabel = new JLabel();
		final JLabel destinationFileLabel = new JLabel();
		final JTextField sourceFileField = new JTextField();
		final JTextField subtitlesFileField = new JTextField();
		final JTextField fontsDirectoryField = new JTextField();
		final JTextField destinationFileField = new JTextField();
		final JButton sourceFileButton = new JButton();
		final JButton subtitlesFileButton = new JButton();
		final JButton fontsDirectoryButton = new JButton();
		final JButton destinationFileButton = new JButton();
		final JButton convertButton = new JButton();
		final JTextArea logArea = new JTextArea();
		final JScrollPane logScroll = new JScrollPane(logArea);

		sourceFileLabel.setText("Source file:");
		subtitlesFileLabel.setText("Subtitles file:");
		fontsDirectoryLabel.setText("Fonts directory:");
		destinationFileLabel.setText("Destination file:");

		sourceFileButton.setText("Select");
		subtitlesFileButton.setText("Select");
		fontsDirectoryButton.setText("Select");
		destinationFileButton.setText("Select");
		convertButton.setText("Convert");

		logArea.setAutoscrolls(true);
		logArea.setEditable(false);

		logScroll.setBounds(10, 60, 780, 500);
		logScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		Runnable check = () -> {
			convertButton.setEnabled(!sourceFileField.getText().equals("") && !destinationFileField.getText().equals(""));
		};

		sourceFileField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent event) {
				check.run();
			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				check.run();
			}

			@Override
			public void changedUpdate(DocumentEvent event) {
				check.run();
			}

		});

		subtitlesFileField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent event) {
				check.run();
			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				check.run();
			}

			@Override
			public void changedUpdate(DocumentEvent event) {
				check.run();
			}

		});

		destinationFileField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent event) {
				check.run();
			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				check.run();
			}

			@Override
			public void changedUpdate(DocumentEvent event) {
				check.run();
			}

		});

		sourceFileButton.addActionListener(event -> {
			final JFileChooser chooser = new JFileChooser();

			if (chooser.showOpenDialog(application) == JFileChooser.APPROVE_OPTION) {
				sourceFileField.setText(chooser.getSelectedFile().getAbsolutePath());
			}

			check.run();
		});

		subtitlesFileButton.addActionListener(event -> {
			final JFileChooser chooser = new JFileChooser();

			if (chooser.showOpenDialog(application) == JFileChooser.APPROVE_OPTION) {
				subtitlesFileField.setText(chooser.getSelectedFile().getAbsolutePath());
			}

			check.run();
		});

		fontsDirectoryButton.addActionListener(event -> {
			final JFileChooser chooser = new JFileChooser();

			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (chooser.showOpenDialog(application) == JFileChooser.APPROVE_OPTION) {
				fontsDirectoryField.setText(chooser.getSelectedFile().getAbsolutePath());
			}

			check.run();
		});

		destinationFileButton.addActionListener(event -> {
			final JFileChooser chooser = new JFileChooser();

			if (chooser.showSaveDialog(application) == JFileChooser.APPROVE_OPTION) {
				destinationFileField.setText(chooser.getSelectedFile().getAbsolutePath());
			}

			check.run();
		});

		convertButton.addActionListener(event -> {
			convertButton.setVisible(false);

			SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

				@Override
				protected Void doInBackground() throws Exception {
					final String subtitlesFilePath = subtitlesFileField.getText();
					final List<String> commands = new ArrayList<String>();
					final ProcessBuilder builder = new ProcessBuilder();
					final Process process;

					commands.add(properties.getProperty("ffmpeg"));
					commands.add("-y");
					commands.add("-i");
					commands.add(sourceFileField.getText());

					if (!subtitlesFilePath.equals("")) {
						final String fontsDirectoryPath = fontsDirectoryField.getText();

						commands.add("-filter:v");

						if (!fontsDirectoryPath.equals("")) {
							commands.add("ass=filename='" + subtitlesFilePath + "':fontsdir='" + fontsDirectoryPath + "':shaping=complex");
						} else {
							commands.add("ass=filename='" + subtitlesFilePath + "':shaping=complex");
						}
					}

					commands.add("-codec:v");
					commands.add("libx264");
					commands.add("-preset");
					commands.add(properties.getProperty("preset"));
					commands.add("-tune");
					commands.add(properties.getProperty("tune"));
					commands.add("-crf");
					commands.add(properties.getProperty("video_quality"));
					commands.add("-codec:a");
					commands.add("aac");
					commands.add("-q:a");
					commands.add(properties.getProperty("audio_quality"));
					commands.add(destinationFileField.getText());

					builder.command(commands);

					try {
						process = builder.start();
					} catch (IOException exception) {
						this.publish("An error occurred while starting the process.");
						exception.printStackTrace();
						return null;
					}

					try (InputStreamReader streamReader = new InputStreamReader(process.getErrorStream())) {
						try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
							bufferedReader.lines().forEach(this::publish);
						}
					}

					try {
						process.waitFor();
					} catch (InterruptedException exception) {
						this.publish("An error occurred while waiting for the process to complete.");
						exception.printStackTrace();
						return null;
					}

					return null;
				}

				@Override
				protected void done() {
					convertButton.setVisible(true);
				}

				@Override
				protected void process(List<String> chunks) {
					for (String chunk : chunks) {
						logArea.append(chunk + "\n");
					}
				}

			};

			worker.execute();
		});

		final Container contentPane = application.getContentPane();

		contentPane.setLayout(layout);
		contentPane.add(sourceFileLabel, "cell 0 0, grow");
		contentPane.add(sourceFileField, "cell 1 0, grow");
		contentPane.add(sourceFileButton, "cell 2 0, grow");
		contentPane.add(subtitlesFileLabel, "cell 0 1, grow");
		contentPane.add(subtitlesFileField, "cell 1 1, grow");
		contentPane.add(subtitlesFileButton, "cell 2 1, grow");
		contentPane.add(fontsDirectoryLabel, "cell 0 2, grow");
		contentPane.add(fontsDirectoryField, "cell 1 2, grow");
		contentPane.add(fontsDirectoryButton, "cell 2 2, grow");
		contentPane.add(destinationFileLabel, "cell 0 3, grow");
		contentPane.add(destinationFileField, "cell 1 3, grow");
		contentPane.add(destinationFileButton, "cell 2 3, grow");
		contentPane.add(convertButton, "cell 2 4, grow");
		contentPane.add(logScroll, "cell 0 5 3 1, grow");

		application.setBounds(0, 0, 640, 480);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		application.setLocationRelativeTo(null);
		application.setResizable(false);
		application.setTitle("Hardsubber");
		application.setVisible(true);

		check.run();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Application());
	}

}
