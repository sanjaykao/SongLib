// Sanjay Kao (sjk231) 
// Virginia Cheng (vc365)

package view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SongLibController {
	@FXML
	 ListView<String> listView;
	 
	 @FXML 
	 private TextArea details;
	 
	 @FXML
	 private TextField enteredName;
	 
	 @FXML
	 private TextField enteredArtist;
	 
	 @FXML
	 private TextField enteredAlbum;
	 
	 @FXML 
	 private TextField enteredYear;
	 
	 private ObservableList<String> obsList;

	   	public void initialize() {
	        // TODO
	    } 
	    
	    public void start(Stage mainStage) {
	    	obsList = FXCollections.observableArrayList();
	    	obsList = getList();

			listView.setItems(obsList); 

			// select the first item
			listView.getSelectionModel().select(0);
			
			File temp = new File("songs.txt");
			boolean exists = temp.exists();
			if(exists && temp.length() > 0) {
				getSelected();
			}

			// set listener for the items
			listView
			.getSelectionModel()
			.selectedIndexProperty()
			.addListener(
					(obs, oldVal, newVal) -> 
					getSelected());
	    }

	    @FXML
	    private void addSong(ActionEvent event) {
	    	if(!(enteredName.getText().trim().isEmpty() || enteredArtist.getText().trim().isEmpty())) {
	    		String name = enteredName.getText().toLowerCase();
	    		String artist = enteredArtist.getText().toLowerCase();
	    		if(exists(name, artist)) {
	    			setWarning("Cannot add song", "Song already exists in library!");
	    		}else if(!checkInput(name) || !checkInput(artist) || (!enteredAlbum.getText().isEmpty() && !checkInput(enteredAlbum.getText()))){
	    			setWarning("Cannot add song", "Please use only allowed characters.");
	    		}else if(!enteredYear.getText().trim().isEmpty() && !checkYear(enteredYear.getText())){
	    			setWarning("Cannot add song", "Please enter a valid year");
	    		}else {
	    			Alert alert = new Alert(AlertType.CONFIRMATION);
		    		alert.setTitle("Confirm changes");
		    		alert.setContentText("Are you sure you want to make these changes?");
		    		
		    		Optional<ButtonType> result = alert.showAndWait();
		    		if(result.get() == ButtonType.OK) {
		    			String input = enteredName.getText() + "`" + enteredArtist.getText() + "`";
		    			if(enteredAlbum.getText().trim().isEmpty()) {
		    				input += "`";
		    			}else {
		    				input += enteredAlbum.getText() + "`";
		    			}
		    			if(enteredYear.getText().trim().isEmpty()) {
		    				input += "";
		    			}else {
		    				input += enteredYear.getText();
		    			}
			    		try(BufferedWriter file = new BufferedWriter(new FileWriter("songs.txt", true))){
			    			file.write(input);
			    			file.newLine();
			    			file.close();
			    		} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    		
			    		obsList = getList();
			    		listView.setItems(obsList);
			    		int ind = getIndex(name, artist);
			    		listView.getSelectionModel().select(ind);
			    		
			    		enteredName.clear();
			    		enteredArtist.clear();
			    		enteredAlbum.clear();
			    		enteredYear.clear();
		    		}
	    		}
	    	}else {
	    		setWarning("Cannot add song", "Name and Artist are required fields!");
	    	}
	        
	    }
	    
	    @FXML
	    private void editSong(ActionEvent event) {
	    	String name = "";
	    	String artist = "";
	    	int index = 0;
	    	Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setTitle("Confirm changes");
    		alert.setContentText("Are you sure you want to edit this song?");
    		
    		Optional<ButtonType> result = alert.showAndWait();
    		if(result.get() == ButtonType.OK) {
    			String item = listView.getSelectionModel().getSelectedItem();
    	    	if(item != null) {
    	    		index = listView.getSelectionModel().getSelectedIndex();
    				name = item.substring(0, item.indexOf('-') - 1);
    				artist = item.substring(item.indexOf("-") + 2);
    				
    				try {
    					Scanner s = new Scanner(new File("songs.txt"));
    					while(s.hasNext()) {
    						String song = s.nextLine();
    						String[] detail = song.split("`");
    						if(detail[0].equals(name) && detail[1].equals(artist)) {
    							enteredName.setText(detail[0]);
    							enteredArtist.setText(detail[1]);
    							if(detail.length > 2) {
    								if(detail.length == 3) {
    									if(detail[2] != null) {
    										enteredAlbum.setText(detail[2]);
    									}
    								}else {
    									if(detail[2] != null) {
    										enteredAlbum.setText(detail[2]);
    									}
    									if(detail[3] != null) {
    										enteredYear.setText(detail[3]);
    									}
    								}
    							}
    						}
    					}
    					s.close();
    				}catch (FileNotFoundException ex) {
    					//System.err.println(ex);
    				}
    				int deleted = delete(name, artist, index);
    	    	}else {
    	    		setWarning("No song selected", "Please select a song or add more songs to the library.");
    	    	}
    		}		
	    }
	    
	    @FXML
	    private void deleteSong(ActionEvent event) {
	    	String item = listView.getSelectionModel().getSelectedItem();
	    	if(item != null) {
	    		Alert alert = new Alert(AlertType.CONFIRMATION);
	    		alert.setTitle("Confirm changes");
	    		alert.setContentText("Are you sure you want to make these changes?");
	    		
	    		Optional<ButtonType> result = alert.showAndWait();
	    		if(result.get() == ButtonType.OK) {
	    			int index = listView.getSelectionModel().getSelectedIndex();
	    			String name = item.substring(0, item.indexOf('-') - 1);
	    			String artist = item.substring(item.indexOf("-") + 2);
	    			int deleted = delete(name, artist, index);
	    			
	    			obsList = getList();
	            	listView.setItems(obsList);
	            	listView.getSelectionModel().select(deleted);
	    			
	    			File file = new File("songs.txt");
	    			if(file.length() == 0) {
	    				details.clear();
	    			}
	    		}
	    	}else {
	    		setWarning("No song selected", "Please select a song or add more songs to the library.");
	    	}
	    }
	    
	    private void getSelected() {                
			String item = listView.getSelectionModel().getSelectedItem();
			String name = "";
			String artist = "";
			try {
				name = item.substring(0, item.indexOf('-') - 1);
				artist = item.substring(item.indexOf('-') + 2);
			}catch(NullPointerException e) {
				//System.err.println(e);
			}
			details.clear();

			try {
				String content = "Song details:\nName: ";
				Scanner s = new Scanner(new File("songs.txt"));
				while(s.hasNext()) {
					String song = s.nextLine();
					String[] detail = song.split("`");
					if(detail[0].equals(name) && detail[1].equals(artist)) {
						content += detail[0] + "\nArtist(s): " + detail[1];
						if(detail.length == 3) {
							if(detail[2] != null) {
								content += "\nAlbum: " + detail[2] + "\nYear:";
							}
						}else if(detail.length == 4) {
							if(detail[2] != null) {
								content += "\nAlbum: " + detail[2];
							}
							if(detail[3] != null) {
								content += "\nYear: " + detail[3];
							}
						}else {
							content += "\nAlbum:\nYear:";
						}
						break;
					}
				}
				details.setText(content);
				s.close();
			} catch (FileNotFoundException ex) {
				//System.err.println(ex);
			}
		}
	    
	    @SuppressWarnings("unchecked")
		private void sortSongs() {
	    	ArrayList<String> temp = new ArrayList<String>();
	    	try {
	    		Scanner s = new Scanner(new File("songs.txt"));
	    		while(s.hasNext()) {
	    			String song = s.nextLine();
	    			temp.add(song);
	    		}
	    		s.close();
	    	}catch(FileNotFoundException ex) {
	    		//System.err.println(ex);
	    	}
	    	//Collections.sort(temp, String.CASE_INSENSITIVE_ORDER);
	    	Collections.sort(temp, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					// TODO Auto-generated method stub
					int n1 = s1.indexOf("`");
					int n2 = s2.indexOf("`");
					String tempn1 = s1.substring(0, n1).toLowerCase();
					String tempn2 = s2.substring(0, n2).toLowerCase();
					String tempa1 = s1.substring(n1 + 1).toLowerCase();
					String tempa2 = s2.substring(n2 + 1).toLowerCase();
					
					if(tempn1.compareTo(tempn2) == 0) {
						return tempa1.compareTo(tempa2);
					}else {
						return tempn1.compareTo(tempn2);
					}
				}
	    		
	    	});
	    	try(BufferedWriter file = new BufferedWriter(new FileWriter("songs.txt", false))){
	    		for(String entry: temp) {
	    			file.write(entry);
	    			file.newLine();
	    		}
	    		file.close();
   		} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
	    }
	    
	    private ObservableList<String> getList(){
	    	ObservableList<String> temp = FXCollections.observableArrayList();
	    	sortSongs();
	    	try {
	    		Scanner s = new Scanner(new File("songs.txt"));
	    		while(s.hasNext()) {
	    			String song = s.nextLine();
	    			String[] detail = song.split("`");
	    			String content = detail[0] + " - " + detail[1];
	    			temp.add(content);
	    		}
	    		s.close();
	    	}catch(FileNotFoundException ex) {
	    		//System.err.println(ex);
	    	}
	    	return temp;
	    }
	    
	    private int getIndex(String name, String artist) {
	    	int index = 0;
	    	try {
		    	Scanner s = new Scanner(new File("songs.txt"));
		    	while(s.hasNext()) {
		    		String song = s.nextLine();
		   			String[] detail = song.split("`");
		   			if(detail[0].toLowerCase().equals(name) && detail[1].toLowerCase().equals(artist)) {
		   				return index;
		   			}
	    			index++;
	    		}		    		
		    	s.close();
		    }catch(FileNotFoundException ex) {
		    	//System.err.println(ex);
		    }
	    	return index;
	    }
	    
	   private boolean exists(String name, String artist) {
	    	try {
	    		Scanner s = new Scanner(new File("songs.txt"));
	    		while(s.hasNext()) {
	    			String song = s.nextLine();
	    			String[] detail = song.split("`");
	    			if(detail[0].toLowerCase().equals(name) && detail[1].toLowerCase().equals(artist)) {
	    				return true;
	    			}
	    		}
	    		s.close();
	    	}catch(FileNotFoundException ex) {
	    		//System.err.println(ex);
	    	}
	    	return false;
	    }
	   
	   private int delete(String name, String artist, int index) {
			ArrayList<String> temp = new ArrayList<String>();
		
			try {
				Scanner s = new Scanner(new File("songs.txt"));
				while(s.hasNext()) {
					String song = s.nextLine();
					String[] detail = song.split("`");
					if(detail[0].equals(name) && detail[1].equals(artist)) {
						continue;
					}else {
						temp.add(song);
					}
				}
				s.close();
			}catch (FileNotFoundException ex) {
				//System.err.println(ex);
			}
			
			if((index > 0) && (index == temp.size())) {
				index--;
			}
		
			try(BufferedWriter file = new BufferedWriter(new FileWriter("songs.txt", false))){
				for(String entry: temp) {
					file.write(entry);
					file.newLine();
				}
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return index;
	   }
	   
	   private boolean checkInput(String string) {
		   for(int i = 0; i < string.length(); i++) {
			   if(string.charAt(i) == '\\' || string.charAt(i) == '|'){
				   return false;
			   }	 
		   }
		   return true;
	   }
	   
	   private boolean checkYear(String year) {
		   for(int i = 0; i < year.length(); i++) {
			   if(!Character.isDigit(year.charAt(i))) {
				   return false;
			   }
		   }
		   return true;
	   }
	   
	   private void setWarning(String title, String content) {
		   Alert alert = new Alert(AlertType.WARNING);
		   alert.setTitle(title);
		   alert.setContentText(content);
		   alert.showAndWait();
	   }
}
