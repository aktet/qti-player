package com.qtitools.player.client.controller;

import com.qtitools.player.client.controller.communication.ItemData;
import com.qtitools.player.client.controller.flow.navigation.NavigationIncidentType;
import com.qtitools.player.client.controller.session.ItemSessionResultAndStats;
import com.qtitools.player.client.controller.session.ItemSessionSocket;
import com.qtitools.player.client.model.Item;
import com.qtitools.player.client.model.ItemStateChangedEventsListener;
import com.qtitools.player.client.module.IInteractionModule;
import com.qtitools.player.client.view.item.ItemViewCarrier;
import com.qtitools.player.client.view.item.ItemViewSocket;

public class ItemController implements ItemStateChangedEventsListener {

	public ItemController(ItemViewSocket ivs, ItemSessionSocket iss){
		itemViewSocket = ivs;
		itemSessionSocket = iss;
	}
	
	private Item item;
	
	private int itemIndex;
	
	private ItemViewSocket itemViewSocket;
	private ItemSessionSocket itemSessionSocket;
	
	private ItemNavigationIncidentsStats navigationIncidentsStats;
	
	public void init(ItemData data){
		item = new Item(data.data, this);
		itemIndex = data.itemIndex;
		itemViewSocket.setItemView(new ItemViewCarrier(String.valueOf(itemIndex+1) + ". " + item.getTitle(), item.getContentView(), item.getFeedbackView(), item.getScoreView()));
		item.setState(itemSessionSocket.getState(itemIndex));
		itemSessionSocket.beginItemSession(itemIndex);
		navigationIncidentsStats = new ItemNavigationIncidentsStats();
	}
	
	public void close(){
		itemSessionSocket.setState(itemIndex, item.getState());
		/*
		ItemSessionData isd = new ItemSessionData();
		itemSessionSocket.setSessionData(itemIndex, isd);
		*/
		itemSessionSocket.endItemSession(itemIndex);
		itemSessionSocket.setSessionResultAndStats(itemIndex, 
				new ItemSessionResultAndStats(item.getResult(), navigationIncidentsStats.getNavigationIncidentsCount(NavigationIncidentType.CHECK), item.getMistakesCount())
			);
		// albo zresetowa� mistakes count
		// albo zrobi� przekazywanie mistakes count tylko na zamkni�ciu (close())
		// to zale�y od tego kto b�dzie pokazywa� item results
		// POBIERANIE ON CLOSE - STATS RESETUJ� SI� SAME
	}
	
	public void updateState(){
		if (item != null){
			item.setState(itemSessionSocket.getState(itemIndex));
		}
	}

	@Override
	public void onItemStateChanged(IInteractionModule sender) {
		item.process(sender != null, sender != null ? sender.getIdentifier() : "");
		// update result
		itemSessionSocket.setSessionResult(itemIndex, item.getResult());
		if (sender != null)
			itemSessionSocket.setState(itemIndex, item.getState());
	}

	public void onNavigationIncident(NavigationIncidentType nit){
		if (item != null){
			if (nit == NavigationIncidentType.CHECK){
				item.checkItem();
			} else if (nit == NavigationIncidentType.CONTINUE){
				item.continueItem();
			} else if (nit == NavigationIncidentType.RESET){
				item.resetItem();
			}
			navigationIncidentsStats.addNavigiationIncident(nit);
		}
	}
}
