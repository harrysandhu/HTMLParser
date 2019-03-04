package mainModule;

import java.util.ArrayList;

public class Tag {
    private String name;
    private String id;
    private String cssText = "";
    private String text = "";
    public Tag(String name){
        this.name = name;
        this.id = null;
    }
    public Tag(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getTagName(){
        return this.name;
    }
    public String getTagId(){
        return this.id;
    }
    public void setCss(String cssText){
        this.cssText = cssText;
    }
    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return this.text;
    }
    public String getCssText(){
        return this.cssText;
    }

}
