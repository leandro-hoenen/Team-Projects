package geographyProject;

import geographyProject.RegionHyrarchy.Country;
import geographyProject.RegionHyrarchy.GovernedRegion.FormOfGovernment;
import geographyProject.RegionHyrarchy.State;

import java.util.ArrayList;

import geographyProject.RegionHyrarchy.City;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Tab;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;


public class GeoController {
	private GeoModel model;
	private GeoView view;

	private Tab currentTab = new Tab ();

	private String lastSelectedCountry;
	private String lastSelectedState;
	private String lastSelectedCity;
	private String currentSelectedItem;
	
	private double minElevationState;
	private double maxElevationState;
	private double minElevationCity;
	private double maxElevationCity;
	
	private ArrayList<String> searchedItems = new ArrayList<>();
	
	public GeoController(GeoModel model, GeoView view) {
		this.model = model;
		this.view = view;
		
		// Set selected tab to country when launching the first time
		currentTab = view.tabCountry;
	
		// Load from a file
		model.loadGeo();
		
		updateView(currentTab);
		
		topControlsEvents();
		leftControlsEvents();
	}
	
	private void topControlsEvents() {
		// Button events
		view.btnCreate.setOnMouseClicked(this::create);
		view.btnEdit.setOnMouseClicked(this::edit);
		view.btnSave.setOnMouseClicked(this::save);
		view.btnDelete.setOnMouseClicked(this::delete);
		
		// Bindings
		view.btnEdit.disableProperty().bind(Bindings.isEmpty(view.itemList.getSelectionModel().getSelectedItems()));
		view.btnCreate.disableProperty().bind(view.btnEdit.selectedProperty());
		view.btnDelete.disableProperty().bind(view.btnEdit.selectedProperty());
		
		view.btnSave.setDisable(true);
	}

	private void leftControlsEvents () {
		
		disableTabs();
				
		// Track the current tab selection
		view.tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
						
			currentTab = newValue;
			disableTabs();
			
			updateView(currentTab);
			
			defaultView();
		});

		// Track the current item selection
		view.itemList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
			currentSelectedItem = newValue;
			unblockTabs(currentSelectedItem);
			// Update Country, State, City View
			if(model.countries.size()>0 && lastSelectedCountry != null && currentTab == view.tabCountry) {
				updateCountryView();
			} else if (model.states.size()>0 && lastSelectedState != null && currentTab == view.tabState) {
				updateStateView();
			} else if(model.cities.size()>0 && lastSelectedCity != null && currentTab == view.tabCity) {
				updateCityView();
			} 
		});
		
		// Tracks Min and Max Elevation values in State and City then calculates the average
		view.centerRoot.tfMinElevationCity.textProperty().addListener((observable, oldValue, newValue) -> {
			try{
				if (newValue != null && currentSelectedItem != null && !newValue.equals("")) {
					minElevationCity = Double.parseDouble(newValue);
					showAvgElevation(minElevationCity, maxElevationCity);
				} else {
					view.centerRoot.tfAvgElevationCity.clear();
				}
			}
			catch(Exception e) {
				view.centerRoot.tfAvgElevationCity.clear();
			}
		});
		view.centerRoot.tfMaxElevationCity.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				if (newValue != null && currentSelectedItem != null && !newValue.equals("")) {
						maxElevationCity = Double.parseDouble(newValue);
						showAvgElevation(minElevationCity, maxElevationCity);
				} else {
					view.centerRoot.tfAvgElevationCity.clear();
				}
			}
			catch(Exception e) {
				view.centerRoot.tfAvgElevationCity.clear();
			}
			
		});
		view.centerRoot.tfMinElevationState.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				if (newValue != null && currentSelectedItem != null && !newValue.equals("")) {
					minElevationState = Double.parseDouble(newValue);
					showAvgElevation(minElevationState, maxElevationState);
				} else {
					view.centerRoot.tfAvgElevationState.clear();
				}
			}
			catch (Exception e) {
				view.centerRoot.tfAvgElevationState.clear();
			}
		});
		view.centerRoot.tfMaxElevationState.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				if (newValue != null && currentSelectedItem != null && !newValue.equals("")) {
					maxElevationState = Double.parseDouble(newValue);
					showAvgElevation(minElevationState, maxElevationState);
				} else {
					view.centerRoot.tfAvgElevationState.clear();
				}
			}
			catch (Exception e) {
				view.centerRoot.tfAvgElevationState.clear();
			}
		});
		
		view.tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals("")) {
				view.items.clear();
				searchedItems.clear();
				
				if (currentTab == view.tabCountry) {
					searchedItems = model.getSearchedCountries(newValue);					
				}else if (currentTab == view.tabState) {
					searchedItems = model.getSearchedStates(newValue, lastSelectedCountry);
				}else if (currentTab == view.tabCity) {
					searchedItems = model.getSearchedCities(newValue, lastSelectedState);
				}
				
				for (String searchedItem : searchedItems) {
					view.items.add(searchedItem);
				}

			}else if (newValue.equals("")) {
				updateView(currentTab);
			}
		});
	}
 
	private void showAvgElevation(double minElevationValue, double maxElevationValue) {
		double avgElevation;
		if (currentTab == view.tabState) {
			if (minElevationValue > 0 && maxElevationValue > 0) {
				avgElevation = (minElevationValue + maxElevationValue) / 2;
				view.centerRoot.tfAvgElevationState.setText(Double.toString(avgElevation));
			}
		} else if (currentTab == view.tabCity) {
			if (minElevationValue > 0 && maxElevationValue > 0) {
				avgElevation = (minElevationValue + maxElevationValue) / 2;
				view.centerRoot.tfAvgElevationCity.setText(Double.toString(avgElevation));
			}
		}
	}
	
	private void unblockTabs (String currentSelectedItem) {
		// Unblock tabs if an item is selected
		 if (currentTab == view.tabCountry)
		{
			lastSelectedCountry = currentSelectedItem;
			if(view.itemList.getSelectionModel().isEmpty() == false && lastSelectedCountry != null ) {
				view.tabState.setDisable(false);
			}
		} 
		else if (currentTab == view.tabState) {
			lastSelectedState = currentSelectedItem;
			view.tabCountry.setDisable(false);
			if(view.itemList.getSelectionModel().isEmpty() == false && lastSelectedState != null ) {
				view.tabCity.setDisable(false);
			}
		} 
		else if (currentTab == view.tabCity) {
			lastSelectedCity = currentSelectedItem;
			view.tabCountry.setDisable(false);
			view.tabState.setDisable(false);
		}
	}
	
	private void create(MouseEvent e) {
		// Country, state or city
		String entry = view.tfEnterZone.getText();
		boolean doubleEntry;
		// Create Country
		if(view.tabPane.getSelectionModel().getSelectedItem() == view.tabCountry) {
			doubleEntry = model.doubleCheckerCountry(entry);
			if (entry.length() > 0 && entry.charAt(0) != ' ' && entry.charAt(entry.length() - 1) != ' ' && doubleEntry == false) {
				model.addCountry(entry); 
				model.saveGeo();
				updateView(view.tabPane.getSelectionModel().getSelectedItem());
			}else if (doubleEntry == true) {
				view.alertDoubleEntry.setContentText(view.doubleCountry);
				view.alertDoubleEntry.showAndWait();
			} else {
				view.alertEntry.showAndWait();
			}
		}
		// Create State
		else if(view.tabPane.getSelectionModel().getSelectedItem() == view.tabState) {
			doubleEntry = model.doubleCheckerState(entry, lastSelectedCountry);
			if (entry.length() > 0 && entry.charAt(0) != ' ' && entry.charAt(entry.length() - 1) != ' ' && doubleEntry == false) {
				model.addState(entry, lastSelectedCountry); 
				model.saveGeo();
				updateView(view.tabPane.getSelectionModel().getSelectedItem());
			}else if (doubleEntry == true) {
				view.alertDoubleEntry.setContentText(view.doubleState);
				view.alertDoubleEntry.showAndWait();
			} else {
				view.alertEntry.showAndWait();
			}
		}
		// Create City
		else if(view.tabPane.getSelectionModel().getSelectedItem() == view.tabCity) {
			doubleEntry = model.doubleCheckerCity(entry, lastSelectedState);
			if (entry.length() > 0 && entry.charAt(0) != ' ' && entry.charAt(entry.length() - 1) != ' ' && doubleEntry == false) {
				model.addCity(entry, lastSelectedState); 
				model.saveGeo();
				updateView(view.tabPane.getSelectionModel().getSelectedItem());
			}else if (doubleEntry == true) {
				view.alertDoubleEntry.setContentText(view.doubleCity);
				view.alertDoubleEntry.showAndWait();
			} else {
				view.alertEntry.showAndWait();
			}
		}
		// tfEnterZone is empty after every entry
		view.tfEnterZone.setText("");
	}

	private void edit(MouseEvent e) {
		if (view.btnEdit.isSelected() == true) {
			setCenterEditable();
			view.btnSave.setDisable(false);
		}else {
			setCenterNotEditable();
			view.btnSave.setDisable(true);
		}

	}
	
	private void save(MouseEvent e) {
		
		String[] userInput;
		String itemName = currentSelectedItem;
		FormOfGovernment formOfGovernment = view.centerRoot.cbFormOfGovernment.getSelectionModel().getSelectedItem();
		int indexCounter = 0;
		
		try {
			if (view.tabCountry.isSelected()) {
				userInput = getCountryData(indexCounter);
				model.saveCountryData(itemName, userInput, formOfGovernment);
			}else if(view.tabState.isSelected()) {
				userInput = getStateData(indexCounter);
				model.saveStateData(itemName, lastSelectedCountry, userInput);
			}else if(view.tabCity.isSelected()) {
				userInput = getCityData(indexCounter);
				model.saveCityData(itemName, lastSelectedState, userInput);		
			}
			// Enable all tabs and item selection again
			view.itemList.setMouseTransparent(false);
			view.itemList.setFocusTraversable(true);
			unblockTabs(currentSelectedItem);
			view.btnSave.setDisable(true);
			view.btnEdit.setSelected(false);
			// Save to a file
			model.saveGeo();
		} 
		catch(Exception e1) {
			  view.alertEntryCenter.showAndWait();
			  setCenterEditable();
		}
	}
	
	private void delete(MouseEvent e) {
		if (currentSelectedItem != null) {
			if (view.tabCountry.isSelected())
				model.deleteCountry(currentSelectedItem);
			if (view.tabState.isSelected())
				model.deleteState(currentSelectedItem, lastSelectedCountry);
			if (view.tabCity.isSelected())
				model.deleteCity(currentSelectedItem, lastSelectedState);
			
			updateView(currentTab);
			model.saveGeo();
			defaultView();
		}
	}
	
	public String[] getCountryData(int indexCounter) {
		String inputDataContainer[] = new String[view.centerRoot.controlsCountry.length - 1];
		
		for (int i = 0; i < view.centerRoot.controlsCountry.length; i++) {
			if (i != 2) {
				inputDataContainer[indexCounter] = ((TextInputControl) view.centerRoot.controlsCountry[i]).getText();
				indexCounter++;
			}
			view.centerRoot.controlsCountry[i].setDisable(true);
		}
		return inputDataContainer;
	}
	
	public String[] getStateData(int indexCounter) {
		String inputDataContainer[] = new String[view.centerRoot.controlsState.length - 1];
		
		for (int i = 0; i < view.centerRoot.controlsState.length; i++) {
			if (i != 4) {
				inputDataContainer[indexCounter] = ((TextInputControl) view.centerRoot.controlsState[i]).getText();
				indexCounter++;
			}
			view.centerRoot.controlsState[i].setDisable(true);
		}
		return inputDataContainer;
	}
	
	public String[] getCityData(int indexCounter) {
		String inputDataContainer[] = new String[view.centerRoot.controlsCity.length - 1];
		
		for (int i = 0; i < view.centerRoot.controlsCity.length; i++) {
			if (i != 4) {
				inputDataContainer[indexCounter] = ((TextInputControl) view.centerRoot.controlsCity[i]).getText();
				indexCounter++;
			}
			view.centerRoot.controlsCity[i].setDisable(true);
		}
		return inputDataContainer;
	}
	

	private void updateView (Tab currentTab) {
		view.items.clear();
		// Updates the list of items
		if (currentTab == view.tabCountry) {
			for (int i = 0; i < model.countries.size(); i++) {
				Country country = model.countries.get(i);
				String countryText = country.getName();
				view.items.add(countryText);
			}
		} else if (currentTab == view.tabState) {
			for(int i = 0; i < model.states.size(); i++) {
				State state = model.states.get(i);
				if (lastSelectedCountry.equals(state.getCountry()))
				{
					String stateText = state.getName();
					view.items.add(stateText);
				}
			}
		} else if (currentTab == view.tabCity) {
			for(int i = 0; i < model.cities.size(); i++) {
				City city = model.cities.get(i);
				if (lastSelectedState.equals(city.getState()))
				{
					String cityText = city.getName();
					view.items.add(cityText);
				}
			}
		} 
		view.showCenterView(currentTab);		
	}
	
	// Updates Center View
	private void updateCountryView () {
		Country currentCountry = model.getCountry(currentSelectedItem);
		view.centerRoot.tfPopulationCountry.setText(Long.toString(currentCountry.getPopulation()));
		view.centerRoot.tfAreaCountry.setText(Integer.toString(currentCountry.getArea()));
		view.centerRoot.cbFormOfGovernment.setValue(currentCountry.getFormOfGovernment());
		view.centerRoot.tfLanguagesCountry.setText(currentCountry.getLanguages());
		view.centerRoot.tfCurrency.setText(currentCountry.getCurrency());
		view.centerRoot.tfPhoneCode.setText(currentCountry.getPhoneCode());
		view.centerRoot.tfCapitalCityCountry.setText(currentCountry.getCapitalCity());
		view.centerRoot.taHistoryCountry.setText(currentCountry.getHistory());
	}
	
	private void updateStateView () {
		State currentState = model.getState(currentSelectedItem, lastSelectedCountry);
		view.centerRoot.tfPopulationState.setText(Long.toString(currentState.getPopulation()));
		view.centerRoot.tfAreaState.setText(Long.toString(currentState.getArea()));
		view.centerRoot.tfMaxElevationState.setText(Double.toString(currentState.getMaxElevation()));
		view.centerRoot.tfMinElevationState.setText(Double.toString(currentState.getMinElevation()));
		view.centerRoot.tfAvgElevationState.setText(Double.toString(currentState.getAvgElevation()));
		view.centerRoot.tfLanguageState.setText(currentState.getLanguages());
		view.centerRoot.tfCapitalCityState.setText(currentState.getCapitalCity());
		view.centerRoot.taHistoryState.setText(currentState.getHistory());
	}
	
	private void updateCityView () {
		City currentCity = model.getCity(currentSelectedItem, lastSelectedState);
		view.centerRoot.tfPopulationCity.setText(Long.toString(currentCity.getPopulation()));
		view.centerRoot.tfAreaCity.setText(Integer.toString(currentCity.getArea()));
		view.centerRoot.tfMaxElevationCity.setText(Double.toString(currentCity.getMaxElevation()));
		view.centerRoot.tfMinElevationCity.setText(Double.toString(currentCity.getMinElevation()));
		view.centerRoot.tfAvgElevationCity.setText(Double.toString(currentCity.getAvgElevation()));
		view.centerRoot.tfLanguageCity.setText(currentCity.getLanguages());
		view.centerRoot.tfZipCode.setText(Long.toString(currentCity.getZipCode()));
		view.centerRoot.tfMayor.setText(currentCity.getMayor());
		view.centerRoot.taHistoryCity.setText(currentCity.getHistory());
	}
	
	private void defaultView() {
		if (currentTab == view.tabCountry) {
			for (int i = 0; i < view.centerRoot.controlsCountry.length; i++) {
				if (i != 2)
					((TextInputControl) view.centerRoot.controlsCountry[i]).clear();
			}
			view.centerRoot.cbFormOfGovernment.getSelectionModel().clearSelection();
		}else if (currentTab == view.tabState) {
			for (int i = 0; i < view.centerRoot.controlsState.length; i++) {
					((TextInputControl) view.centerRoot.controlsState[i]).clear();;
			}
		}else if (currentTab == view.tabCity) {
			for (int i = 0; i < view.centerRoot.controlsCity.length; i++) {
				((TextInputControl) view.centerRoot.controlsCity[i]).clear();;
			}
		}		
	}
	
	private void disableTabs () {
		// Disable Tabs
		if (currentTab == view.tabCountry) {
			view.tabState.setDisable(true);
			view.tabCity.setDisable(true);
		} else if (currentTab == view.tabState) {
			view.tabCity.setDisable(true);
		}
	}
	
	private void setCenterEditable() {
		if (view.itemList.getSelectionModel().getSelectedItem() != null && view.tabPane.getSelectionModel().getSelectedItem() == view.tabCountry) {
			for (int i = 0; i < view.centerRoot.controlsCountry.length; i++) {
				view.centerRoot.controlsCountry[i].setDisable(false);
			}
			// Disable item selection and tabs switching when editing
			view.tabState.setDisable(true);
			view.tabCity.setDisable(true);
		}
		else if(view.itemList.getSelectionModel().getSelectedItem() != null && view.tabPane.getSelectionModel().getSelectedItem() == view.tabState) {
			for (int i = 0; i < view.centerRoot.controlsState.length; i++) {
				view.centerRoot.controlsState[i].setDisable(false);
			}
			view.tabCountry.setDisable(true);
			view.tabCity.setDisable(true);
		}
		else if (view.itemList.getSelectionModel().getSelectedItem() != null && view.tabPane.getSelectionModel().getSelectedItem() == view.tabCity) {
			for (int i = 0; i < view.centerRoot.controlsCity.length; i++) {
				view.centerRoot.controlsCity[i].setDisable(false);
			}
			view.tabCountry.setDisable(true);
			view.tabState.setDisable(true);
		}
		view.itemList.setMouseTransparent(true);
		view.itemList.setFocusTraversable(false);
	}
	
	private void setCenterNotEditable() {
		if (view.tabPane.getSelectionModel().getSelectedItem() == view.tabCountry) {
			for (int i = 0; i < view.centerRoot.controlsCountry.length; i++) {
				view.centerRoot.controlsCountry[i].setDisable(true);
			}
			// Disable item selection and tabs switching when editing
			view.tabState.setDisable(false);
			view.tabCity.setDisable(false);
		}
		else if(view.tabPane.getSelectionModel().getSelectedItem() == view.tabState) {
			for (int i = 0; i < view.centerRoot.controlsState.length; i++) {
				view.centerRoot.controlsState[i].setDisable(true);
			}
			view.tabCountry.setDisable(false);
			view.tabCity.setDisable(false);
		}
		else if (view.tabPane.getSelectionModel().getSelectedItem() == view.tabCity) {
			for (int i = 0; i < view.centerRoot.controlsCity.length; i++) {
				view.centerRoot.controlsCity[i].setDisable(true);
			}
			view.tabCountry.setDisable(false);
			view.tabState.setDisable(false);
		}
		view.itemList.setMouseTransparent(false);
		view.itemList.setFocusTraversable(true);
	}
}
