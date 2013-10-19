package com.wwc.jajing.listadapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wwc.jajing.R;
import com.wwc.jajing.activities.DetachUser;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

@SuppressLint("DefaultLocale")
public class FastIndexEntityAdapter extends BaseAdapter implements
		SectionIndexer {

	public List<DetachUser> itemList;
	private Context context;
	private static String sections = "abcdefghijklmnopqrstuvwxyz";

	public FastIndexEntityAdapter(List<DetachUser> itemList, Context ctx) {
		this.itemList = itemList;
        //Collections.sort(this.itemList, DetachUser.DetachUserNameComparator);
        this.context = ctx;
	}

	public int getCount() {
		return itemList.size();
	}

	public DetachUser getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return itemList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View contactView = convertView;
		if (contactView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contactView = inflater.inflate(R.layout.list_view, parent, false);
		}

		DetachUser detachUser = itemList.get(position);
		
		String detachContactId = getContactId( context,  detachUser.getPhoneno() );
		
		detachUser.setLocalContactId(( ( detachContactId != null && detachContactId.length() > 0 ) ? Long.parseLong(detachContactId) :-1l ));
		
		//Load contact name from phone list
		TextView text = (TextView) contactView.findViewById(R.id.contactname);
		text.setText((detachUser.getName() == null ? getContactName(context, detachContactId) : detachUser.getName()));

		//Load busy status from detach user status id
		ImageView imageView = (ImageView) contactView.findViewById(R.id.contact_image);
		imageView.setImageBitmap(getBitmapFromAsset(detachUser.getStatusId().getStatusIcon()));

		//Load status updated @
		TextView updatedOn = (TextView) contactView.findViewById(R.id.updateon);
		updatedOn.setText((detachUser.getLastUpdated() == null ? "Just now" : detachUser
				.getLastUpdated()));

		//Load status message
		TextView status = (TextView) contactView.findViewById(R.id.status);
		status.setText((detachUser.getStatusmessage() == null ? "Idle" : detachUser
				.getStatusmessage()));

		//Load remaining time
		TextView remainingHours = (TextView) contactView.findViewById(R.id.timeremaining );
		remainingHours.setText((  detachUser.getLastUpdated() == null ? "In a few minutes" : detachUser
				.getTimeRemainingAsString()));
		
		return contactView;

	}

	@Override
	public int getPositionForSection(int section) {
		Log.d("ListView", "Get position for section");
		for (int i = 0; i < this.getCount(); i++) {
			if( this.getItem(i) == null || this.getItem(i).getName() == null )
				break ;
			String item = this.getItem(i).getName().toLowerCase();
			if (item.charAt(0) == sections.charAt(section))
				return i;
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int arg0) {
		Log.d("ListView", "Get section");
		return 0;
	}

	@Override
	public Object[] getSections() {
		Log.d("ListView", "Get sections");
		String[] sectionsArr = new String[sections.length()];
		for (int i = 0; i < sections.length(); i++)
			sectionsArr[i] = "" + sections.charAt(i);

		return sectionsArr;

	}

	/**
	 * Load contact status icon
	 * 
	 * @param strName
	 * @return
	 */
	private Bitmap getBitmapFromAsset(String strName) {
		AssetManager assetManager = context.getResources().getAssets();
		InputStream istr = null;
		try {
			istr = assetManager.open(strName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap bitmap = BitmapFactory.decodeStream(istr);
		return bitmap;
	}
	
	/**
	 * Query contact id value of any given phone number.
	 * 
	 * @param context - Detach activity context
	 * @param phoneNumber - should be unique phone number of contact member
	 * @return - long raw contact id otherwise -1
	 */
	private String getContactId( Context context, String phoneNumber ) {
	    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
	            phoneNumber);
	    Cursor cursor = context.getContentResolver().query(uri, new String[] {
	            PhoneLookup.DISPLAY_NAME, BaseColumns._ID }, null, null, null);
	    String contactId = "";
	    if (cursor.moveToFirst()) {
	        do {
	            contactId = cursor.getString(cursor
	                    .getColumnIndex(BaseColumns._ID));
	        } while (cursor.moveToNext());
	    }
	    cursor.close();
	    cursor = null;
	    return contactId;
	}
	
	/**
	 * Query contact display name value of any given phone number.
	 * 
	 * @param context - Detach activity context
	 * @param contactId - should be unique phone number of contact member
	 * @return - string value of name otherwise empty string
	 */
	private String getContactName(Context context, String contactId ) {
	    String[] projection = new String[] { Contacts.DISPLAY_NAME };
	    Cursor cursor = context.getContentResolver().query(Contacts.CONTENT_URI, projection,
	            Contacts._ID + "=?", new String[] { contactId }, null);
	    String displayName = "Anonymous";
	    if (cursor.moveToFirst()) {
	        displayName = cursor.getString(0);
	    }
	    cursor.close();
	    cursor = null;
	    return displayName;
	}

    public String getContactName(String contactId) {
        String[] projection = new String[] { Contacts.DISPLAY_NAME };
        Cursor cursor = context.getContentResolver().query(Contacts.CONTENT_URI, projection,
                Contacts._ID + "=?", new String[] { contactId }, null);
        String displayName = "Anonymous";
        if (cursor.moveToFirst()) {
            displayName = cursor.getString(0);
        }
        cursor.close();
        cursor = null;
        return displayName;
    }

    public String getContactId( String phoneNumber ) {
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                phoneNumber);
        Cursor cursor = context.getContentResolver().query(uri, new String[] {
                PhoneLookup.DISPLAY_NAME, BaseColumns._ID }, null, null, null);
        String contactId = "";
        if (cursor.moveToFirst()) {
            do {
                contactId = cursor.getString(cursor
                        .getColumnIndex(BaseColumns._ID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = null;
        return contactId;
    }

    private String parseDateTime(String dateTime) {

        if(dateTime == "") {
            return "";
        } else {
            LocalDateTime date = (new DateTime(dateTime)).toLocalDateTime();
            return date.toString("MMM")+". " +
                   date.getDayOfMonth()+" " +
                   date.getYear()+" " +
                   date.toString("HH:mm");
        }

    }

    private boolean isSuperCloseToCurrentTime(String dateTime) {
        if(dateTime == "") {
            return false;
        }
        else if((new DateTime(dateTime)).getMillis() <= DateTime.now().plusMinutes(5).getMillis()) {
            return true;
        } else {
            return false;
        }
    }

}
