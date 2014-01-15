package com.nmfinal.syncaccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nmfinal.nmfinalimageunlock.MainActivity;
import com.nmfinal.nmfinalimageunlock.R;


public class SyncAccountsActivity extends Activity {

	public final static String HASHKEY_ACCT_TYPE = "accountType";
	public final static String HASHKEY_ACCT_IMG = "accountImg";
	public final static String HASHKEY_ACCT_USERNAME = "accountUsername";
	public final static String HASHKEY_ACCT_PASSWORD = "accountPassword";

	public final static String ACCT_TYPE = "myAcctType";
	
	public final static String PREF_NAME_ACCOUNTS = "prefAccounts";
	
	private ListView accountsListView;
	private AccountsListAdapter accountsListAdapter;

	private ArrayList<HashMap<String, Object>> accountsData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_syncaccount);
		
		initAccountsData();
		accountsListView = ( ListView) findViewById( R.id.listview_accounts );
		accountsListAdapter = new AccountsListAdapter( this, accountsData );
		accountsListView.setAdapter( accountsListAdapter );
	}
	
	
	public void saveLinkedAccount( String accountType, String username, String password )
	{
		Log.i(MainActivity.TAB, "Saving account: " + accountType + ": ( " +username + " )" );
		
		String acctInfo = username + "/" + password;
		
		
		SharedPreferences pref = getSharedPreferences( PREF_NAME_ACCOUNTS, MODE_PRIVATE );
		SharedPreferences.Editor editor = pref.edit();
		editor.putString( accountType, acctInfo );
		editor.commit();
		/**********************************************************
		 * This is the standard way to handle the account-related jobs. 
		 * However, this requires "AccountAuthenticator"s for
		 * every account type. 
		 * Facebook or Google, for example, have their own. 
		 * But you must write one for such as NtuMail...
		 * Give up this way for just demo.
		 **********************************************************/
		/*
		AccountManager acctMgr  = AccountManager.get( this );
		Account[] 	   accounts = acctMgr.getAccountsByType( ACCT_TYPE );
		Account        account  = null;
		for (Account a: accounts) {
			if ( a.type == accountName ){
				account = a;
				break;
			}
		}
		if ( account == null){
			account = new Account( username, accountName );
		}
		acctMgr.addAccountExplicitly(account, password, null);
		*/
	}
	
	
	private void initAccountsData()
	{
		accountsData = new ArrayList<HashMap<String,Object>>();
		
		HashMap<String,Object> ntumailAcct = new HashMap<String, Object>();
		ntumailAcct.put( HASHKEY_ACCT_TYPE, getResources().getString(R.string.acctType_ntumail) );
		ntumailAcct.put( HASHKEY_ACCT_IMG, R.drawable.logo_ntumail );
		ntumailAcct.put( HASHKEY_ACCT_USERNAME, null);
		ntumailAcct.put( HASHKEY_ACCT_PASSWORD, null);
		accountsData.add( ntumailAcct );
		
		HashMap<String,Object> dropboxAcct = new HashMap<String, Object>();
		dropboxAcct.put( HASHKEY_ACCT_TYPE, getResources().getString(R.string.acctType_dropbox) );
		dropboxAcct.put( HASHKEY_ACCT_IMG, R.drawable.logo_dropbox );
		dropboxAcct.put( HASHKEY_ACCT_USERNAME, null);
		dropboxAcct.put( HASHKEY_ACCT_PASSWORD, null);
		accountsData.add( dropboxAcct );
		
	}
	
	class AccountsListAdapter extends BaseAdapter
	{

		private ArrayList<HashMap<String, Object>> data;
		private Context context;
		private LayoutInflater layoutInflater;  

		
		
		public AccountsListAdapter( Context contextIn, ArrayList<HashMap<String, Object>> dataIn )
		{
			this.context = contextIn;
			this.data = dataIn;
			this.layoutInflater = LayoutInflater.from(contextIn);
		}
		
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			
			AccountItem account = null;
			
			if (convertView == null){
	            convertView = layoutInflater.inflate( R.layout.list_syncaccounts, null ); 
	            account = new AccountItem();
				account.accountImgView  = (ImageView) convertView.findViewById( R.id.img_account_icon );
				account.accountImgView.setMaxHeight(100);
				account.accountImgView.setAdjustViewBounds( true );
				account.accountTextView = (TextView)  convertView.findViewById( R.id.text_account_name );
				account.linkBtn			= (Button)    convertView.findViewById( R.id.btn_linkaccount );
				account.linkBtn.setMaxHeight(100);
				account.loginBtn		= (Button)	  convertView.findViewById( R.id.btn_loginaccount );
				account.loginBtn.setMaxHeight(100);
				convertView.setTag( account );
			} 
			else {
				account = (AccountItem) convertView.getTag();
			}
			
			account.accountImgView.setImageDrawable( 
					context.getResources().getDrawable( (Integer) data.get(position).get( HASHKEY_ACCT_IMG ) ) );
			account.accountTextView.setText( (String) data.get(position).get( HASHKEY_ACCT_TYPE ) );
			account.linkBtn.setText( "³sµ²" );
			account.loginBtn.setText( "µn¤J" );
			
			final int pos = position; // be final for being used in the listeners
			account.linkBtn.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					linkAccount( (String) data.get(pos).get(HASHKEY_ACCT_TYPE) );
				}
			});
			account.loginBtn.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					loginAccount( (String) data.get(pos).get(HASHKEY_ACCT_TYPE) );
				}
			});
			
			return convertView;
		}
		
		
		class AccountItem
		{
			ImageView accountImgView;
			TextView  accountTextView;
			Button	  linkBtn;
			Button    loginBtn;
		}
		
		
		public void linkAccount( String acctName )
		{
			LinkAccountDialog.newInstance( acctName ).show(getFragmentManager(), "dialog");
		}
		public void loginAccount( String acctType )
		{
			SharedPreferences pref = getSharedPreferences( PREF_NAME_ACCOUNTS, MODE_PRIVATE );
			String acctInfo = pref.getString( acctType, null );
			if (acctInfo == null){
				Toast.makeText(context, getResources().getString(R.string.toast_notsetaccount), Toast.LENGTH_SHORT);
				return;
			}
			
			
			
			Log.i(MainActivity.TAB, "acctInfo got:" + acctInfo);
			String[] usernameAndPwd = acctInfo.split( "/" );
			String username = usernameAndPwd[0];
			String password = usernameAndPwd[1];
			
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			
			Log.i(MainActivity.TAB, "starting activity. AcctType=" + acctType);
			
			bundle.putString( getResources().getString(R.string.accountType), acctType );
			bundle.putString( getResources().getString(R.string.username), username );
			bundle.putString( getResources().getString(R.string.password), password );
			intent.putExtras( bundle );
			intent.setClass( context, LoginWebviewActivity.class );
			startActivity( intent );
			
		}
		
		
		
		
		
	}
}
