package com.qtitools.player.client.controller.data;

import java.util.Vector;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.qtitools.player.client.controller.data.events.AssessmentDataLoaderEventListener;
import com.qtitools.player.client.controller.style.StyleLinkDeclaration;
import com.qtitools.player.client.util.xml.document.XMLData;

public class AssessmentDataSourceManager {

	public AssessmentDataSourceManager(AssessmentDataLoaderEventListener l){
		listener = l;
		itemsCount = -1;
		errorMessage = "";
	}
	
	private XMLData data;
	private AssessmentDataLoaderEventListener listener;
	private StyleLinkDeclaration styleDeclaration;
	private int itemsCount;
	private String errorMessage;
	
	public void setAssessmentData(XMLData d){
		data = d;
		itemsCount = -1;
		styleDeclaration = new StyleLinkDeclaration(data.getDocument().getElementsByTagName("styleDeclaration"), data.getBaseURL());
		listener.onAssessmentDataLoaded();
	}
	
	public void setAssessmentLoadingError(String err){
		errorMessage = err;
	}
	
	public XMLData getAssessmentData(){
		return data;
	}
	
	public boolean isError(){
		return errorMessage.length() > 0;
	}
	
	public String getAssessmentTitle(){
		String title = "";
		if (data != null){
			try{
				Node rootNode = data.getDocument().getElementsByTagName("assessmentTest").item(0);
				title = ((Element)rootNode).getAttribute("title");
			} catch (Exception e) {
			}
			
		}
		return title;
	}
	
	public String[] getItemUrls(){
		
		String[] itemUrls = new String[0];
		
		if (data != null){
			try {
				NodeList nodes = data.getDocument().getElementsByTagName("assessmentItemRef");
				String[] tmpItemUrls = new String[nodes.getLength()];
				for(int i = 0; i < nodes.getLength(); i++){
					Node itemRefNode = nodes.item(i);
					tmpItemUrls[i] = data.getBaseURL() + ((Element)itemRefNode).getAttribute("href");
			    }
				itemUrls = tmpItemUrls;
			} catch (Exception e) {	}
		}
		
		//TODO
		return itemUrls;
	}
	
	public int getItemsCount(){
		if (itemsCount == -1)
			itemsCount = getItemUrls().length;
		
		return itemsCount;
	}
	
	public Vector<String> getStyleLinksForUserAgent(String userAgent){
		return styleDeclaration.getStyleLinksForUserAgent(userAgent);
	}
}
