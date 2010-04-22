package com.qtitools.player.client.module.order;

import java.util.HashMap;
import java.util.Vector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.qtitools.player.client.components.htmldnd.DragContainerPanel;
import com.qtitools.player.client.components.htmldnd.DragMode;
import com.qtitools.player.client.model.internalevents.InternalEventTrigger;
import com.qtitools.player.client.model.variables.response.Response;
import com.qtitools.player.client.module.IInteractionModule;
import com.qtitools.player.client.module.IModuleSocket;
import com.qtitools.player.client.module.IStateChangedListener;
import com.qtitools.player.client.util.RandomizedSet;
import com.qtitools.player.client.util.xml.XMLConverter;
import com.qtitools.player.client.util.xml.XMLUtils;

public class OrderModule extends Composite implements IInteractionModule {
	
	/** response processing interface */
	private Response response;
	/** module state changed listener */
	private IStateChangedListener stateListener;
	/** response id */
	private String responseIdentifier;
	/** Shuffle? */
	private boolean shuffle = false;
	private Vector<Boolean> shuffledOptions;
	/** option widgets */
	private Vector<AbsolutePanel> options;
	private Vector<String> optionsIdentifiers;
	private HashMap<String, Integer> tagIdMap;
	
	private DragContainerPanel container;
	private VerticalPanel mainPanel;

	public OrderModule(Element element, IModuleSocket moduleSocket, IStateChangedListener stateChangedListener) {

		shuffle = XMLUtils.getAttributeAsBoolean(element, "shuffle");
		
		if (shuffle)
			shuffledOptions = new Vector<Boolean>();

		responseIdentifier = XMLUtils.getAttributeAsString(element, "responseIdentifier");
		response = moduleSocket.getResponse(responseIdentifier);
		stateListener = stateChangedListener;
		
		optionsIdentifiers = new Vector<String>();
		
		extractOptionsWidgets(element);

		// update tag id 

		tagIdMap = new HashMap<String, Integer>();
		
		for (int i = 0 ; i < options.size() ; i++){
			AbsolutePanel currOption = options.get(i);			
			tagIdMap.put(currOption.getElement().getId(), i);
			if (currOption.getElement().getElementsByTagName("div").getLength() > 1){
				String spanID = currOption.getElement().getElementsByTagName("div").getItem(1).getId();
				tagIdMap.put(spanID, i);
			}
		}
		
		container = new DragContainerPanel();
		//container.setSize("200", "200");
		container.setStylePrimaryName("qp-order-container");
		container.setDragMode(DragMode.HORIZONTAL);
		
		mainPanel = new VerticalPanel();
		mainPanel.add(getPromptView(element));
		mainPanel.add(container);
		mainPanel.setStylePrimaryName("qp-order-module");
		
		initWidget(mainPanel);
	}

	@Override
	public void onOwnerAttached() {
		
		if (options.size() == 0)
			return;
		
		
		for (int i = 0 ; i < options.size() ; i++){
			AbsolutePanel currOption = options.get(i);
			container.add(currOption);
		}

		container.setAutoSize();
		
		if (shuffle){
			Vector<Integer> randomOrder = new Vector<Integer>();
			RandomizedSet<Integer> randomSet = new RandomizedSet<Integer>();
			
			for (int i = 0 ; i < options.size() ; i ++){
				if (shuffledOptions.get(i))
					randomSet.push(i);
			}
			

			for (int i = 0 ; i < options.size() ; i ++){
				if (shuffledOptions.get(i))
					randomOrder.add(randomSet.pull());
				else
					randomOrder.add(i);
			}
			
			container.setElementsOrder(randomOrder);
			
		}
		
		updateResponse();

	}

	/**
	 * Get prompt
	 * @return
	 */
	private Widget getPromptView(Element element){
		
		HTML	promptHTML = new HTML();
		Element prompt = XMLUtils.getFirstElementWithTagName(element, "prompt");
		
		promptHTML.setStyleName("qp-order-prompt");
		
		if(prompt != null){
			promptHTML.setHTML(prompt.getFirstChild().getNodeValue());
		}
		
		return promptHTML;
		
	}
	
	private void extractOptionsWidgets(Element element){
		options = new Vector<AbsolutePanel>();
		
		NodeList optionNodes = element.getElementsByTagName("simpleChoice");

		// Add randomized nodes to shuffle table
		if(shuffle){
			for(int i = 0; i < optionNodes.getLength(); i++){
				Element	option = (Element)optionNodes.item(i);
				boolean currOptionShuffle = !XMLUtils.getAttributeAsBoolean(option, "fixed");
				shuffledOptions.add(currOptionShuffle);
			}
		}

		// Create buttons
		for(int i = 0; i < optionNodes.getLength(); i++){
			Element option = (Element)optionNodes.item(i);
			
			String currId = Document.get().createUniqueId();
			
			AbsolutePanel optionPanel = new AbsolutePanel();
			optionPanel.getElement().setId(currId);
			optionPanel.setStylePrimaryName("qp-order-option");
			
			Vector<String> ignoredTags = new Vector<String>();
			ignoredTags.add("feedbackInline");
			com.google.gwt.dom.client.Element dom = XMLConverter.getDOM(option, ignoredTags);
			
			SimplePanel optionContentPanel = new SimplePanel();
			optionContentPanel.getElement().setId(Document.get().createUniqueId());
			optionContentPanel.setStylePrimaryName("qp-order-option-content");
			optionContentPanel.getElement().setInnerHTML(dom.getInnerHTML());
			DOM.setElementProperty(optionContentPanel.getElement(), "align", "center"); 

			optionPanel.insert(optionContentPanel, 0,0,0);
			
			Label optionCover = new Label();
			optionCover.getElement().setId(Document.get().createUniqueId());
			optionCover.setStylePrimaryName("qp-order-option-cover");
			optionPanel.insert(optionCover, 0,0,optionPanel.getWidgetCount());
			
			options.add(optionPanel);
			
			String currIdentifier = XMLUtils.getAttributeAsString(option, "identifier");
			
			optionsIdentifiers.add(currIdentifier);
		}
	}

	@Override
	public void markAnswers() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		container.removeAll();
		onOwnerAttached();

	}

	@Override
	public void showCorrectAnswers() {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONArray getState() {
		
		Vector<Integer> optionsIndexes = container.getElementsOrder();
		
		JSONArray statesArr = new JSONArray();
		
		for (int  i = 0 ; i < optionsIndexes.size() ; i ++){
			statesArr.set(i, new JSONNumber(optionsIndexes.get(i)) );
		}
		
		return statesArr;
	}

	@Override
	public void setState(JSONArray newState) {
		Vector<Integer> optionsIndexes = new Vector<Integer>();
		
		for (int  i = 0 ; i < newState.size() ; i ++){
			optionsIndexes.add((int)newState.get(i).isNumber().doubleValue());
		}
		
		container.setElementsOrder(optionsIndexes);
		updateResponse();
		stateListener.onStateChanged();
	}

	@Override
	public Vector<InternalEventTrigger> getTriggers() {
		Vector<InternalEventTrigger> t = new Vector<InternalEventTrigger>(0);
		t.add(new InternalEventTrigger(container.getElement().getId(), Event.ONMOUSEUP));
		t.add(new InternalEventTrigger(container.getElement().getId(), Event.ONMOUSEMOVE));
		
		if (options.size() > 0){
			for (String currId : tagIdMap.keySet()){
				t.add(new InternalEventTrigger(currId, Event.ONMOUSEDOWN));
				t.add(new InternalEventTrigger(currId, Event.ONMOUSEUP));
				t.add(new InternalEventTrigger(currId, Event.ONMOUSEMOVE));
			}
		}
		
		return t;
	}

	@Override
	public void handleEvent(String tagId, Event event) {
		if (event.getTypeInt() == Event.ONMOUSEDOWN){
			int currWidgetIndex = tagIdMap.get(tagId);
			container.startDrag(currWidgetIndex, event.getClientX(), event.getClientY());
		} else if (event.getTypeInt() == Event.ONMOUSEMOVE){
			container.drag(event.getClientX(), event.getClientY());
		} else if (event.getTypeInt() == Event.ONMOUSEUP || event.getTypeInt() == Event.ONMOUSEOUT  || event.getTypeInt() == Event.ONMOUSEOVER){
			container.stopDrag();
			updateResponse();
		}

	}
	

	
	private void updateResponse(){
		
		Vector<Integer> optionsIndexes = container.getElementsOrder();
		
		Vector<String> currResponseValues = new Vector<String>();
		
		for (Integer i : optionsIndexes){
			currResponseValues.add(optionsIdentifiers.get(i));
		}
		
		response.set(currResponseValues);
		stateListener.onStateChanged();
	}


}