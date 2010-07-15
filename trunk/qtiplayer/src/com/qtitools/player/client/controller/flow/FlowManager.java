package com.qtitools.player.client.controller.flow;

import com.qtitools.player.client.controller.communication.FlowOptions;
import com.qtitools.player.client.controller.communication.ItemActivityOptions;
import com.qtitools.player.client.controller.communication.PageItemsDisplayMode;
import com.qtitools.player.client.controller.communication.PageReference;
import com.qtitools.player.client.controller.communication.PageType;
import com.qtitools.player.client.controller.flow.navigation.NavigationIncidentType;
import com.qtitools.player.client.controller.flow.navigation.NavigationSocket;
import com.qtitools.player.client.controller.flow.navigation.NavigationView;
import com.qtitools.player.client.controller.flow.navigation.NavigationViewSocket;

public class FlowManager implements NavigationSocket {

	public FlowManager(FlowEventsListener fel){
		flowListener = fel;
		navigationView = new NavigationView(this);
		flowOptions = new FlowOptions();
		isCheck = false;
	}
	
	private NavigationView navigationView;
	private FlowEventsListener flowListener;
	
	private int currentPageIndex;
	private PageType currentPageType;
	private int itemsCount;
	private FlowOptions flowOptions;
	private ItemActivityOptions activityOptions;
	private boolean isCheck;

	public void init(int _itemsCount){
		itemsCount = _itemsCount;
		activityOptions = new ItemActivityOptions();
		currentPageIndex = 0;
		if (flowOptions.showToC)
			currentPageType = PageType.TOC;
		else 
			currentPageType = PageType.TEST;
		navigationView.init(_itemsCount);
	}
	
	public void startFlow(){
		flowListener.onNavigatePageSwitched();
		updateNavigation();
	}
	
	public void setFlowOptions(FlowOptions o){
		flowOptions = o;
	}
	
	public FlowOptions getFlowOptions(){
		return flowOptions;
	}
	
	@Override
	public void gotoPage(int index) {
		if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ALL){
			if (index == 0){
				onPageChange();
				currentPageType = PageType.TEST;
				currentPageIndex = index;
				flowListener.onNavigatePageSwitched();
				updateNavigation();
			}
		} else if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ONE){
			if (index >= 0  &&  index < itemsCount){
				onPageChange();
				currentPageType = PageType.TEST;
				currentPageIndex = index;
				flowListener.onNavigatePageSwitched();
				updateNavigation();
			}
		}
		
	}

	@Override
	public void gotoSummary() {
		if (currentPageType != PageType.SUMMARY  &&  flowOptions.showSummary){
			onPageChange();
			activityOptions.previewMode = false;
			currentPageType = PageType.SUMMARY;
			currentPageIndex = 0;
			flowListener.onNavigatePageSwitched();
			updateNavigation();
		}
		
	}

	@Override
	public void gotoToc() {
		if (currentPageType != PageType.TOC  &&  flowOptions.showToC){
			onPageChange();
			currentPageType = PageType.TOC;
			currentPageIndex = 0;
			flowListener.onNavigatePageSwitched();
			updateNavigation();
		}
		
	}

	@Override
	public void gotoTest() {
		if (currentPageType != PageType.TEST){
			if (itemsCount > 0){
				onPageChange();
				gotoPage(0);
			}
		}
		
	}

	@Override
	public void nextPage() {
		if (currentPageType == PageType.SUMMARY){

		} else if (currentPageType == PageType.TOC){
			if (0 < itemsCount){
				gotoPage(0);
			}
		} else if (currentPageType == PageType.TEST){
			
			if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ALL){
				
			} else if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ONE){
				if (currentPageIndex+1 >= 0  &&  currentPageIndex+1 < itemsCount){
					gotoPage(currentPageIndex+1);
				}
			}
		}
		
		
	}

	@Override
	public void previousPage() {
		if (currentPageType == PageType.SUMMARY){
			
		} else if (currentPageType == PageType.TOC){
			
		} else if (currentPageType == PageType.TEST){
			
			if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ALL){
				
			} else if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ONE){
				if (currentPageIndex-1 >= 0  &&  currentPageIndex+1 < itemsCount){
					gotoPage(currentPageIndex-1);
				}
			}
		}
		
	}

	@Override
	public void gotoFirstPage() {
		if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ALL){
			if (0 < itemsCount){
				gotoPage(0);
			}
		} else if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ONE){
			if (0 < itemsCount){
				gotoPage(0);
			}
		}
	}

	@Override
	public void gotoLastPage() {
		if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ALL){
			if (0 < itemsCount){
				gotoPage(0);
			}
		} else if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ONE){
			if (itemsCount > 0){
				gotoPage(itemsCount-1);
			}
		}
		
	}

	@Override
	public void checkPage() {
		if (isCheck == false){
			isCheck = true;
			flowListener.onNavigationIncident(NavigationIncidentType.CHECK);
			updateNavigation();
		}
	}

	@Override
	public void continuePage() {
		if (isCheck == true){
			isCheck = false;
			flowListener.onNavigationIncident(NavigationIncidentType.CONTINUE);
			updateNavigation();
		}

	}

	@Override
	public void resetPage() {
		isCheck = false;
		flowListener.onNavigationIncident(NavigationIncidentType.RESET);
		updateNavigation();
	}

	@Override
	public void preview() {
		onPageChange();
		activityOptions.previewMode = true;
		gotoLastPage();
	}
	
	public void onPageChange(){
		isCheck = false;
	}
	
	public void updateNavigation(){
		navigationView.updateButtons(currentPageType, currentPageIndex, 
				(flowOptions.itemsDisplayMode == PageItemsDisplayMode.ONE)?itemsCount:1, flowOptions, isCheck, activityOptions);
	}
	
	public NavigationViewSocket getNavigationViewSocket(){
		return navigationView;
	}
	
	public PageReference getPageReference(){
		int[] currentPageItemsIndices = null;
		if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ONE){
			currentPageItemsIndices = new int[1];
			currentPageItemsIndices[0] = currentPageIndex;
		} else if (flowOptions.itemsDisplayMode == PageItemsDisplayMode.ALL){
			currentPageItemsIndices = new int[itemsCount];
			for (int i = 0 ; i < itemsCount ; i ++){
				currentPageItemsIndices[i] = i;
			}
		}
		PageReference pr = new PageReference(currentPageType, currentPageItemsIndices, activityOptions);
		
		return pr;
	}
	
	public ItemActivityOptions getItemActivityOptions(){
		return activityOptions;
	}

	public PageType getPageType(){
		return currentPageType;
	}
	
	public boolean isPreviewMode(){
		return activityOptions.previewMode;
	}

	public int getCurrentPageIndex(){
		return currentPageIndex;
	}

	
}
