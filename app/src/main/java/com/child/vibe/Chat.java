package com.child.vibe;

import java.util.List;

public class Chat {

    String id;
    List<String> ids;
    String image;

    public Chat(){}

    public Chat(String id, List<String> ids,String image){
        this.id = id;
        this.ids = ids;
        this.image = image;
    }

    public String getId(){
        return id;
    }

    public List<String> getIds(){
        return ids;
    }

    public String getImage(){return image;}
}
