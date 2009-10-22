package com.klangner.qtiplayer.client.module.choice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.klangner.qtiplayer.client.module.IActivity;
import com.klangner.qtiplayer.client.module.IModuleSocket;
import com.klangner.qtiplayer.client.module.IStateful;
import com.klangner.qtiplayer.client.util.RandomizedSet;
import com.klangner.qtiplayer.client.util.XmlElement;

/**
 * Widget with choice implementation
 * @author Krzysztof Langner
 *
 */
public class ChoiceModule extends Composite implements IActivity, IStateful{

	/** root element for this module */
	private XmlElement			choiceElement;
	/** Work mode single or multiple choice */
	private boolean 				multi = false;
	/** Shuffle? */
	private boolean 				shuffle = false;
	/** option widgets */
	private Vector<OptionWidget>	options;
	/** response processing interface */
	private IModuleSocket 	moduleSocket;
	
	
	public ChoiceModule(Element choiceNode, IModuleSocket moduleSocket){
		
		choiceElement = new XmlElement(choiceNode);

		this.moduleSocket = moduleSocket;
		this.multi = (choiceElement.getAttributeAsInt("maxChoices") != 1);
		this.shuffle = choiceElement.getAttributeAsBoolean("shuffle");

		VerticalPanel vp = new VerticalPanel();
		
		vp.setStyleName("qp-choice-module");
		vp.add(getPromptView());
		vp.add(getOptionsView());
		
		initWidget(vp);
	}
	
	/**
	 * @see IActivity#markAnswers()
	 */
	public void markAnswers() {
		
		for(OptionWidget option : options){
			option.markAnswers();
		}
	}

	/**
	 * @see IActivity#reset()
	 */
	public void reset() {
		for(OptionWidget option : options){
			option.reset();
		}
	}

	/**
	 * @see IActivity#showCorrectAnswers()
	 */
	public void showCorrectAnswers() {
		for(OptionWidget option : options){
			option.showCorrectAnswers();
		}
	}

	
  /**
   * @see IStateful#getState()
   */
  public Serializable getState() {
    HashMap<String, Serializable>  state = new HashMap<String, Serializable>();
    
    for(OptionWidget option : options){
      state.put(option.getId(), option.getState());
    }
    
    return state;
  }

  /**
   * @see IStateful#setState(Serializable)
   */
  @SuppressWarnings("unchecked")
  public void setState(Serializable newState) {

    if(newState instanceof HashMap){
      HashMap<String, Serializable> state = (HashMap<String, Serializable>)newState;
      
      for(OptionWidget option : options){
        option.setState(state.get(option.getId()));
      }
    }
  }
  
	/**
	 * Get options view
	 * @return
	 */
	private Widget getOptionsView(){
		
		VerticalPanel 	panel = new VerticalPanel();
		NodeList 				optionNodes = choiceElement.getElementsByTagName("simpleChoice");
		RandomizedSet<XmlElement>	randomizedNodes = new RandomizedSet<XmlElement>();
		String					responseIdentifier = choiceElement.getAttributeAsString("responseIdentifier");

		options = new Vector<OptionWidget>();
		// Add randomized nodes to shuffle table
		if(shuffle){
			for(int i = 0; i < optionNodes.getLength(); i++){
				XmlElement	option = new XmlElement((Element)optionNodes.item(i));
				if(!option.getAttributeAsBoolean("fixed"))
					randomizedNodes.push(option);
			}
		}
		
		// Create buttons
		for(int i = 0; i < optionNodes.getLength(); i++){
			XmlElement		option = new XmlElement((Element)optionNodes.item(i));
			OptionWidget 	button;
			
			if(shuffle && !option.getAttributeAsBoolean("fixed") ){
				option = randomizedNodes.pull();
			}
			
			button = new OptionWidget(option, moduleSocket, responseIdentifier, multi);
			options.add(button);
			panel.add(button);
		}
		
		return panel;
	}
	
	
	/**
	 * Get prompt
	 * @return
	 */
	private Widget getPromptView(){
		
		HTML	promptHTML = new HTML();
		Element prompt = choiceElement.getElement("prompt");
		
		promptHTML.setStyleName("qp-choice-prompt");
		if(prompt != null){
			promptHTML.setHTML(prompt.getFirstChild().getNodeValue());
		}
		
		return promptHTML;
		
	}

}
