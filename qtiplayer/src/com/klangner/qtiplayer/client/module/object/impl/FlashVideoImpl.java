package com.klangner.qtiplayer.client.module.object.impl;

import com.google.gwt.dom.client.Document;


public class FlashVideoImpl implements VideoImpl{

  public String getHTML(String src){
    String  id = Document.get().createUniqueId();
    
    return  "<div id='" + id + "'></div>" + 
            "<script>" + 
            " vp=new FAVideo('" + 
            id + "', '" + src + "',0,0,{autoLoad:true, autoPlay:false});" +
            "</script>";
  }
}