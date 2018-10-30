package coloredtrails.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import coloredtrails.client.HumanClient;
import coloredtrails.common.Util;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;



public class StartGameWindow {

	protected Shell shell;
	private Text textID;
	private Text textGameLoadFile;
	
	private Text textServerHostname;
	private Text textServerPort;

	
	
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			StartGameWindow window = new StartGameWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		
		
        shell.addListener(SWT.Close, new Listener() 
        { 
           @Override 
           public void handleEvent(Event event) 
           { 
              System.out.println("Child Shell handling Close event, about to dispose this Shell"); 
              shell.dispose(); 
           } 
        }); 
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 494);
		shell.setText("Colored Trails");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(24, 45, 82, 19);
		lblNewLabel.setText("ID");
		
		textID = new Text(shell, SWT.BORDER);
		textID.setText("1");
		textID.setBounds(206, 33, 195, 31);
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setBounds(24, 108, 82, 19);
		lblNewLabel_1.setText("Gender");
		
		Combo comboGender = new Combo(shell, SWT.NONE);
		comboGender.setItems(new String[] {"", "Female", "Male", "Other"});
		comboGender.setBounds(204, 96, 197, 31);
		comboGender.select(1);
		
		DateTime dateTime = new DateTime(shell, SWT.BORDER);
		dateTime.setBounds(162, 169, 239, 41);
		
		Label lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setBounds(24, 178, 82, 19);
		lblNewLabel_2.setText("Birth Date");
		
		textGameLoadFile = new Text(shell, SWT.BORDER);
		textGameLoadFile.setText("1");
		textGameLoadFile.setBounds(206, 247, 195, 31);
		
		Label lblNewLabel_3 = new Label(shell, SWT.NONE);
		lblNewLabel_3.setBounds(24, 259, 132, 19);
		lblNewLabel_3.setText("Game Number ");
		
		// Hostname 
		textServerHostname = new Text(shell, SWT.BORDER);
		textServerHostname.setText(HumanClient.SERVER_HOST);
		textServerHostname.setBounds(206, 280, 195, 31);
		
		Label lblNewLabel_4 = new Label(shell, SWT.NONE);
		lblNewLabel_4.setBounds(24, 290, 132, 19);
		lblNewLabel_4.setText("Server Hostname ");
		
		//Port
		textServerPort = new Text(shell, SWT.BORDER);
		textServerPort.setText(Integer.toString(HumanClient.SERVER_PORT));
		textServerPort.setBounds(206, 320, 195, 31);
		
		Label lblNewLabel_5 = new Label(shell, SWT.NONE);
		lblNewLabel_5.setBounds(24, 329, 132, 19);
		lblNewLabel_5.setText("Server Port ");
		
		Button btnPlay = new Button(shell, SWT.NONE);
		btnPlay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if( textID.getText().trim().isEmpty() || comboGender.getText().trim().isEmpty()) {
						MessageDialog.openError(shell, "Error", "Please enter ID and Gender");
					}
					else if ( textServerHostname.getText().trim().isEmpty() || textServerHostname.getText().trim().isEmpty() || !Util.isInteger(textServerPort.getText().trim())) {
						MessageDialog.openError(shell, "Error", "Please enter Valid Host and Port");
						
					}
					else {
					String playerID = textID.getText();
					String playerGender = comboGender.getText();
					String playerBirthDate = dateTime.toString();
					String gameLoadFile;
					
					if(!textGameLoadFile.getText().trim().isEmpty()) {
						gameLoadFile = new String(textGameLoadFile.getText());
						
					}
					
					String hostName = textServerHostname.getText();
					int port = Integer.parseInt(textServerPort.getText());
					
					//shell.setVisible(false);
					HumanClient client = new HumanClient(hostName,port,playerID);
					client.start();
					
					shell.dispose();
					}
				}catch(Exception excep) {
						MessageDialog.openError(shell, "Error", "Wrong value entered.");
				
				}
				
				
				
			}
		});
		btnPlay.setBounds(163, 361, 108, 31);
		btnPlay.setText("Play");
		
		

	}
	

	
	
}
