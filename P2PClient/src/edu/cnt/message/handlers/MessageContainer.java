package edu.cnt.message.handlers;

import edu.cnt.message.Messages;

public class MessageContainer {
private Messages msg;
private String IP;
/**
 * @return the msg
 */
public Messages getMsg() {
	return msg;
}
/**
 * @param msg the msg to set
 */
public void setMsg(Messages msg) {
	this.msg = msg;
}
/**
 * @return the iP
 */
public String getIP() {
	return IP;
}
/**
 * @param iP the iP to set
 */
public void setIP(String iP) {
	IP = iP;
}

}
