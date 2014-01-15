package com.nmfinal.syncaccount;


import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nmfinal.nmfinalimageunlock.MainActivity;
import com.nmfinal.nmfinalimageunlock.R;

public class LinkAccountDialog extends DialogFragment {
	
	private String accountType;
	
	private EditText accountEdit;
	private EditText passwordEdit;
	private EditText confirmPwdEdit;
	private Button   saveBtn;
	private Button   cancelBtn;
	
	
	public static LinkAccountDialog newInstance( String accountType )
	{
		LinkAccountDialog dialog = new LinkAccountDialog();
		dialog.accountType = accountType;
		Bundle args = new Bundle();
		args.putString("title", accountType);
		dialog.setArguments( args );
		return dialog;
	}
	
	public LinkAccountDialog()
	{
		
	}
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.dialog_linkaccount, container);
		accountEdit 	= (EditText) view.findViewById( R.id.edit_account );
		passwordEdit 	= (EditText) view.findViewById( R.id.edit_password );
		confirmPwdEdit 	= (EditText) view.findViewById( R.id.edit_confirmpassword );
		
		saveBtn 		= (Button) view.findViewById( R.id.btn_save_account );
		cancelBtn 		= (Button) view.findViewById( R.id.btn_cancel_linking );
		
		if (saveBtn == null){
			Log.e(MainActivity.TAB, "saveBtn == null");
		}
		
		saveBtn.setOnClickListener( onClickListener );
		cancelBtn.setOnClickListener( onClickListener );
		
		getDialog().setTitle( getArguments().getString("title") ); 
		
		getDialog().setCanceledOnTouchOutside( true );
		
		return view;
	}
	
	
	@Override
	public void onActivityCreated( Bundle savedInstanceState )
	{
		super.onActivityCreated(savedInstanceState);
		
		SharedPreferences pref = (SharedPreferences) getActivity().getSharedPreferences( SyncAccountsActivity.PREF_NAME_ACCOUNTS, Context.MODE_PRIVATE);
		String acctInfo = pref.getString( accountType, null );
		if (acctInfo == null){
			return;
		}
		Log.i(MainActivity.TAB, "acctInfo got:" + acctInfo);
		String[] usernameAndPwd = acctInfo.split( "/" );
		String username = usernameAndPwd[0];
		String password = usernameAndPwd[1];
		
		accountEdit.setText(username);
		passwordEdit.setText(password);
		confirmPwdEdit.setText(password);
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if ( v.getId() == R.id.btn_save_account ){
				Log.i(MainActivity.TAB,"save onclicked");
				((SyncAccountsActivity) getActivity()).saveLinkedAccount( 
															accountType, 
															accountEdit.getText().toString(), 
															passwordEdit.getText().toString()
														);
				Log.i(MainActivity.TAB,"test1");
				getDialog().dismiss();
				Log.i(MainActivity.TAB,"test2");
			}
			else if ( v.getId() == R.id.btn_cancel_linking ){
				getDialog().cancel();
			}
		}
	};
	
	
}
