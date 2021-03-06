package userinterface;

// system imports
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Vector;
import java.util.Enumeration;

// project imports
import impresario.IModel;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import model.*;
import model.ColorCollection;

//==============================================================================
public class ColorCollectionView extends View
{
	protected TableView<ColorTableModel> tableOfColors;
	protected Button cancelButton;
	protected Button submitButton;
	protected Text actionText; 

	protected MessageView statusLog;


	//--------------------------------------------------------------------------
	public ColorCollectionView(IModel mct)
	{
		super(mct, "ColorCollectionView");

		// create a container for showing the contents
		VBox container = new VBox(10);
		container.setStyle("-fx-background-color: slategrey");
		container.setPadding(new Insets(15, 5, 5, 5));

		// create our GUI components, add them to this panel
		container.getChildren().add(createTitle());
		container.getChildren().add(createFormContent());

		// Error message area
		container.getChildren().add(createStatusLog("                                            "));

		getChildren().add(container);

		populateFields();
		tableOfColors.getSelectionModel().select(0);
	}

	//--------------------------------------------------------------------------
	protected void populateFields()
	{
		getEntryTableModelValues();
	}

	//--------------------------------------------------------------------------
	protected void getEntryTableModelValues()
	{

		ObservableList<ColorTableModel> tableData = FXCollections.observableArrayList();
		try
		{
			ColorCollection colorCollection = 
					(ColorCollection)myModel.getState("ColorList");

			Vector entryList = (Vector)colorCollection.getState("Colors");

			if (entryList.size() > 0)
			{
				Enumeration entries = entryList.elements();

				while (entries.hasMoreElements() == true)
				{
					ColorX nextColor = (ColorX)entries.nextElement();
					Vector<String> view = nextColor.getEntryListView();
					//ERROR: cannot find symbol nextColor

					// add this list entry to the list
					ColorTableModel nextTableRowData = new ColorTableModel(view);
					tableData.add(nextTableRowData);

				}
				if(entryList.size() == 1)
					actionText.setText(entryList.size()+" Color Found!");
				else 
					actionText.setText(entryList.size()+" Colors Found!");

				actionText.setFill(Color.LIGHTGREEN);
			}
			else
			{
				actionText.setText("No Colors Found!");
				actionText.setFill(Color.FIREBRICK);
			}
			tableOfColors.setItems(tableData);
		}	
		catch (Exception e) {//SQLException e) {
			// Need to handle this exception
		}
	}

	// Create the title container
	//-------------------------------------------------------------
	private Node createTitle()
	{
		VBox container = new VBox(10);
		container.setPadding(new Insets(1, 1, 1, 30));

		Text clientText = new Text("OFFICE OF CAREER SERVICES");
		clientText.setFont(Font.font("Copperplate", FontWeight.EXTRA_BOLD, 36));
                clientText.setEffect(new DropShadow());
		clientText.setTextAlignment(TextAlignment.CENTER);
		clientText.setFill(Color.WHITESMOKE);
		container.getChildren().add(clientText);

		Text titleText = new Text(" Professional Clothes Closet Management System ");
		titleText.setFont(Font.font("Copperplate", FontWeight.THIN, 28));
		titleText.setTextAlignment(TextAlignment.CENTER);
		titleText.setFill(Color.GOLD);
		container.getChildren().add(titleText);

		Text blankText = new Text("  ");
		blankText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		blankText.setWrappingWidth(350);
		blankText.setTextAlignment(TextAlignment.CENTER);
		blankText.setFill(Color.WHITE);
		container.getChildren().add(blankText);

		actionText = new Text(" ");//set later
		actionText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		actionText.setWrappingWidth(350);
		actionText.setTextAlignment(TextAlignment.CENTER);
		actionText.setFill(Color.BLACK);
		container.getChildren().add(actionText);
                container.setAlignment(Pos.CENTER);

		return container;
	}

	// Create the main form content
	//-------------------------------------------------------------
	private VBox createFormContent()
	{
		VBox vbox = new VBox(10);
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 25, 10, 0));

		tableOfColors = new TableView<ColorTableModel>();
		tableOfColors.setEffect(new DropShadow());
		tableOfColors.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-selection-bar: gold;");
		tableOfColors.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		TableColumn barcodePrefixColumn = new TableColumn("Barcode Prefix") ;
		barcodePrefixColumn.setMinWidth(50);
		barcodePrefixColumn.setCellValueFactory(
				new PropertyValueFactory<ColorTableModel, String>("barcodePrefix"));

		TableColumn descriptionColumn = new TableColumn("Description") ;
		descriptionColumn.setMinWidth(150);
		descriptionColumn.setCellValueFactory(
				new PropertyValueFactory<ColorTableModel, String>("description"));

		TableColumn alphaCodeColumn = new TableColumn("Alpha Code") ;
		alphaCodeColumn.setMinWidth(50);
		alphaCodeColumn.setCellValueFactory(
				new PropertyValueFactory<ColorTableModel, String>("alphaCode"));

		tableOfColors.getColumns().addAll(descriptionColumn, 
				barcodePrefixColumn, alphaCodeColumn);

		tableOfColors.setOnMousePressed((MouseEvent event) -> {
			if (event.isPrimaryButtonDown() && event.getClickCount() >=2 ){
				
                            if((boolean)myModel.getState("Alert"))
                                   displayErrorAlert();
                                else
                                    processColorSelected();
			}
		});
		ImageView icon = new ImageView(new Image("/images/check.png"));
		icon.setFitHeight(15);
		icon.setFitWidth(15);
		submitButton = new Button("Select",icon);
		submitButton.setFont(Font.font("Comic Sans", FontWeight.THIN, 14));
		submitButton.setOnAction((ActionEvent e) -> {
			clearErrorMessage();
			// do the inquiry
                        if((boolean)myModel.getState("Alert"))
                            displayErrorAlert();
                         else
                             processColorSelected();
		});
		submitButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
			submitButton.setEffect(new DropShadow());
		});
		submitButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
			submitButton.setEffect(null);
		});
		icon = new ImageView(new Image("/images/return.png"));
		icon.setFitHeight(15);
		icon.setFitWidth(15);
		cancelButton = new Button("Return", icon);
		cancelButton.setFont(Font.font("Comic Sans", FontWeight.THIN, 14));
		cancelButton.setOnAction((ActionEvent e) -> {
			clearErrorMessage();
			myModel.stateChangeRequest("CancelColorList", null);
		});
		cancelButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
			cancelButton.setEffect(new DropShadow());
		});
		cancelButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
			cancelButton.setEffect(null);
		});

		HBox btnContainer = new HBox(10);
		btnContainer.setAlignment(Pos.CENTER);
		btnContainer.getChildren().add(submitButton);
		btnContainer.getChildren().add(cancelButton);
                btnContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                    btnContainer.setStyle("-fx-background-color: GOLD");
		});
                btnContainer.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                    btnContainer.setStyle("-fx-background-color: SLATEGREY");
		});

		vbox.getChildren().add(grid);
		tableOfColors.setPrefHeight(250);
                tableOfColors.setMaxWidth(325);
		vbox.getChildren().add(tableOfColors);
		vbox.getChildren().add(btnContainer);
		vbox.setPadding(new Insets(10,10,10,10));
                vbox.setAlignment(Pos.CENTER);

		return vbox;
	}

	//--------------------------------------------------------------------------
	public void updateState(String key, Object value)
	{
	}
        
        private void displayErrorAlert(){
            clearErrorMessage();
            Alert alert = new Alert(Alert.AlertType.ERROR,"Barcode Prefix: "+tableOfColors.getSelectionModel().getSelectedItem().getBarcodePrefix()
                    +"\nDescription: "+tableOfColors.getSelectionModel().getSelectedItem().getDescription(), ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.setTitle("Remove Color");
            alert.setHeaderText("Are you sure want to remove this Color?");
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/BPT_LOGO_All-In-One_Color.png"));
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                processColorSelected();
                String val = (String)myModel.getState("TransactionError");
                if (val.startsWith("ERR") == true)
                {
                        statusLog.displayErrorMessage(val);
                }
                else
                {
                    displayMessage(val);
                    getEntryTableModelValues();
                }		
            }
        }

	//--------------------------------------------------------------------------
	protected void processColorSelected()
	{
		ColorTableModel selectedItem = tableOfColors.getSelectionModel().getSelectedItem();

		if(selectedItem != null)
		{
			String selectedBarcodePrefix = selectedItem.getBarcodePrefix();

			myModel.stateChangeRequest("ColorSelected", selectedBarcodePrefix);
		}
	}

	//--------------------------------------------------------------------------
	protected MessageView createStatusLog(String initialMessage)
	{
		statusLog = new MessageView(initialMessage);

		return statusLog;
	}
        
	/**
	 * Display info message
	 */
	//----------------------------------------------------------
	public void displayMessage(String message)
	{
		statusLog.displayMessage(message);
	}

	/**
	 * Clear error message
	 */
	//----------------------------------------------------------
	public void clearErrorMessage()
	{
		statusLog.clearErrorMessage();
	}


}
