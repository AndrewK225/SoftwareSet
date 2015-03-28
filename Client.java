import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class Client {
	// Connect status constants
	final static int DISCONNECTED = 0;
	final static int BEGIN_CONNECT = 1;
	final static int CONNECTED = 2;

	// Various GUI components and info
	public static JFrame mainFrame = null;
	public static JTextArea chatText = null;
	public static JTextField chatLine = null;
	public static JLabel statusBar = null;
	public static JTextField ipField = null;
	public static JTextField portField = null;
	public static JButton connectButton = null;
	public static JButton disconnectButton = null;
	public static Socket clientSocket = null;

	// Connection info
	public static String hostIP = "localhost";
	public static int port = 5129;
	public static int connectionStatus = DISCONNECTED;
	public static boolean isHost = true;

	private static JPanel initOptionsPane() {
		JPanel pane = null;
		ActionAdapter buttonListener = null;

		// Create an options pane
		JPanel optionsPane = new JPanel(new GridLayout(4, 1));

		// IP address input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Host IP:"));
		ipField = new JTextField(10); ipField.setText(hostIP);
		ipField.setEditable(true);
		pane.add(ipField);
		optionsPane.add(pane);

		// Port input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Port:"));
		portField = new JTextField(10); portField.setEditable(true);
		portField.setText((new Integer(port)).toString());
		pane.add(portField);
		optionsPane.add(pane);

		// Host/guest option
		pane = new JPanel(new GridLayout(1, 2));
		optionsPane.add(pane);

		// Connect/disconnect buttons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				// Request a connection initiation
				if (e.getActionCommand().equals("connect")) {
					connectButton.setEnabled(false);
					disconnectButton.setEnabled(true);
					connectionStatus = BEGIN_CONNECT;
					ipField.setEnabled(false);
					portField.setEnabled(false);
					
					String hostAddr = ipField.getText();
					int port = Integer.parseInt(portField.getText());
					
					try {
						clientSocket = new Socket();
						clientSocket.setTcpNoDelay(true);
						clientSocket.connect(new InetSocketAddress(hostAddr, port));
					} catch (UnknownHostException err_unknown) {
						System.out.println("Unknown Host Error: " + err_unknown.getMessage());
						throw new RuntimeException(err_unknown);
					} catch (IOException err_io) {
						System.out.println("IO Error: " + err_io.getMessage());
						throw new RuntimeException(err_io);
					}
					finally {
						try { 
							clientSocket.close();
						} catch (IOException close_err) {
							System.out.println("Error closing socket: " + close_err.getMessage());
						}
						finally {
							System.out.println("Closed socket.");
						}
					}
					
					chatLine.setEnabled(true);
					
					
					statusBar.setText("Online");
					mainFrame.repaint();
				}
				// Disconnect
				else {
					connectButton.setEnabled(true);
					disconnectButton.setEnabled(false);
					connectionStatus = DISCONNECTED;
					ipField.setEnabled(true);
					portField.setEnabled(true);
					chatLine.setText("");
					chatLine.setEnabled(false);
					statusBar.setText("Offline");
					mainFrame.repaint();
				}
			}
		};
		connectButton = new JButton("Connect");
		connectButton.setMnemonic(KeyEvent.VK_C);
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(buttonListener);
		connectButton.setSize(40, 20);
		connectButton.setEnabled(true);
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setMnemonic(KeyEvent.VK_D);
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(buttonListener);
		disconnectButton.setEnabled(false);
		buttonPane.add(connectButton);
		buttonPane.add(disconnectButton);
		optionsPane.add(buttonPane);

		return optionsPane;
	}

	private static void initGUI() {
		// Set up the status bar
		statusBar = new JLabel();
		statusBar.setText("Offline");

		// Set up the options pane
		JPanel optionsPane = initOptionsPane();

		// Set up the chat pane
		JPanel chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		JScrollPane chatTextPane = new JScrollPane(chatText,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLine = new JTextField();
		chatLine.setEnabled(false);
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(200, 200));

		// Set up the main pane
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);

		// Set up the main frame
		mainFrame = new JFrame("Set Game Client");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setPreferredSize(new Dimension(800, 600));
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

	public static void main(String args[]) {
		initGUI();
	}
}

// Action adapter for easy event-listener coding
class ActionAdapter implements ActionListener {
	public void actionPerformed(ActionEvent e) {}
}
