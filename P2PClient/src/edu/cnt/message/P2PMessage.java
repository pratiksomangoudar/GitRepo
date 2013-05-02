package edu.cnt.message;

import java.io.File;
import java.io.Serializable;

import edu.cnt.bitfield.BitFieldHandler;
import edu.cnt.common.ConfigData;
import edu.cnt.common.Constants;
import edu.cnt.filehandler.FileHandler;
import edu.cnt.peers.PeerHandler;

		/**
		 * @author pratiksomanagoudar
		 *
		 */
		public class P2PMessage implements Messages,Serializable{
			
			
			private int message_Length;
			private char message_Type;
			private MessagePayload payload;
			private transient ConfigData config;
			
			public P2PMessage(char messageType, int seq){
				this.message_Type=messageType;
				this.message_Length= Constants.MESSAGE_TYPE_LENGTH;
				
				
				if(messageType==Constants.MESSAGE_TYPE_BITFIELD){
					//BITFIELD MESSAGE
				
					payload= new MessagePayload(BitFieldHandler.getBitField());
					message_Length= message_Length+1;
					
				}
				if(messageType==Constants.MESSAGE_TYPE_REQUEST){
					//REQUEST MESSAGE
					message_Length=message_Length+ 4;
					payload=new MessagePayload(seq);
				}
				if(messageType==Constants.MESSAGE_TYPE_HAVE){
					//HAVE MESSAGE
					message_Length=message_Length+ 4;
					payload=new MessagePayload(seq);
				}
				
				if(messageType==Constants.MESSAGE_TYPE_PIECE){
					//PIECE MESSAGE
					config = ConfigData.getInstance();
					FileHandler fileHandler=new FileHandler(Constants.FILE_INPUT_PATH+PeerHandler.hostPeerID+File.separator, config.getFilename());
					File file=fileHandler.getFileBySeq(seq);
					if(file==null){
						System.out.println("No File Found!!");
					}
					message_Length=message_Length+ (int) file.length()+4;
					payload= new MessagePayload(file,seq);
					
				}
				else{
					message_Length=message_Length+4;
				}
			}
			/**
			 * @return
			 */
			public int getMsgLength() {
				return message_Length;
			}

			/**
			 * @param msgLength
			 */
			public void setMsgLength(int msgLength) {
				this.message_Length = msgLength;
			}

			/**
			 * @return
			 */
			public char getP2PMessageType() {
				return message_Type;
			}

			/**
			 * @param msgType
			 */
			public void setP2PMsgType(char msgType) {
				this.message_Length = msgType;
			}

			/**
			 * @return
			 */
			public MessagePayload getPayload() {
				return payload;
			}

			/**
			 * @param payload
			 */
			public void setPayload(MessagePayload payload) {
				this.payload = payload;
			}

			
			/* (non-Javadoc)
			 * @see edu.cnt.message.Messages#getMessageType()
			 */
			@Override
			public String getMessageType() {
				return Constants.PEER2PEERMESSAGE;
			}
		}


