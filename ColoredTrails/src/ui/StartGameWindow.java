package ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;


import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import static data.Boot.gameLoadFile;
import static data.Boot.playerGender;
import static data.Boot.playerID;
import static data.Boot.playerBirthDate;


public class StartGameWindow {

	protected Shell shell;
	private Text textID;
	private Text textGameLoadFile;

	
	
	
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
		textID.setText("066689720");
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
		
		Button btnPlay = new Button(shell, SWT.NONE);
		btnPlay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if( textID.getText().trim().isEmpty() || comboGender.getText().trim().isEmpty()) {
						MessageDialog.openError(shell, "Error", "Please enter ID and Gender");
					}
					else {
					playerID = textID.getText();
					playerGender = comboGender.getText();
					playerBirthDate = dateTime.toString();
					
					if(!textGameLoadFile.getText().trim().isEmpty()) {
						gameLoadFile = new String(textGameLoadFile.getText());
						
					}
					
					//shell.setVisible(false);
					
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
