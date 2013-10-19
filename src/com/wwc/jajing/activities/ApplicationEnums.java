package com.wwc.jajing.activities;


public class ApplicationEnums {

	/**
	 * 
	 * User status can be retrieved through status id(int) of json
	 * and will be mapped respectively in this enum.  
	 * 
	 * Status Icon are part of assets and will be loaded through
	 * bitmap
	 * @author nagendhiran
	 *
	 */
	public enum UserStatus {
		AVAILABLE( 1, "available24.png" ), 
		AWAY( 2, "asleep24.png" ),
		BUSY( 3, "asleep24.png" ),
		DOD( 4, "flap24.png" ), 
		IDLE( 5, "asleep24.png" ), 
		DRIVING( 6, "flap24.png" );

		private int m_statusId;
		private String m_statusIcon;

		private UserStatus( int statusId, String statusIcon ) {
			m_statusId = statusId;
			m_statusIcon = statusIcon;
		}

		public int getStatusId() {
			return this.m_statusId;
		}

		public String getStatusIcon() {
			return this.m_statusIcon;
		}
	}
	
}
