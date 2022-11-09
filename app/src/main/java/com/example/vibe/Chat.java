package com.example.vibe;

import java.util.List;

public class Chat {

    String id;
    List<String> ids;

    public Chat(){}

    public Chat(String id, List<String> ids){
        this.id = id;
        this.ids = ids;
    }

    public String getId(){
        return id;
    }

    public List<String> getIds(){
        return ids;
    }
}
