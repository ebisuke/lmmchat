package jp.mochisuke.lmmchat.chat;

import java.util.List;

public class ChatHistory  implements  Cloneable{

    List<ChatData> chatDataList;
    public ChatHistory() {
        chatDataList=new java.util.Vector<>();
    }
    public ChatHistory(List<ChatData> chatDataList) {
        this.chatDataList = chatDataList;
    }

    public List<ChatData> getChatDataList() {
        return chatDataList;
    }
    public void Add(ChatData chatData){
        chatDataList.add(chatData);
    }

    public void Clear(){
        chatDataList.clear();
    }

    @Override
    public ChatHistory clone() {
        try {
            ChatHistory clone = (ChatHistory) super.clone();

            clone.chatDataList = new java.util.Vector<>(chatDataList);




            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
