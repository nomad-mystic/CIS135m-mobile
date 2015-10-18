package com.nomad_mystic_passwordmanager.passwordmanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class AccountItemFragment extends ListFragment {

    private String mAccountString = null;
    private ArrayAdapter<AccountItem> mAdaptor;
    private OnFragmentInteractionListener mListener;
    private AccountItem mCurrentItem;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdaptor = new ArrayAdapter<AccountItem>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1
        );
        if(mAccountString != null) {
            setAccounts(mAccountString);
        }
        setListAdapter(mAdaptor);

    }

    @Override
    public void onStart() {
        super.onStart();
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mCurrentItem = mAdaptor.getItem(position);
            mListener.onFragmentInteraction(mCurrentItem);
            getListView().setItemChecked(position, true);
        }
    }
    public void addAccount(AccountItem item) {
        mAdaptor.add(item);
    }

    public void removeCurrentItem() {
        mAdaptor.remove(mCurrentItem);
    }

    public String getAccounts() {
        String accountString = "";

        for(int i = 0; i < mAdaptor.getCount(); i++) {
            AccountItem item = mAdaptor.getItem(i);
            try {
                String encodeName = URLEncoder.encode(item.getName(), "utf-8");
                String encodePassword = URLEncoder.encode(item.getPassword(), "utf-8");

                accountString = accountString + encodeName + ": " + encodePassword + "\n";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return accountString;
    }

    public void setAccounts(String accountText) {
        mAccountString = accountText;
        if(mAdaptor != null) {
            String[] lines = accountText.split("\n");

            for(int i = 0; i <lines.length; i++) {
                String[] parans = lines[i].split(":");
                try {
                    String name = URLDecoder.decode(parans[0], "utf-8");
                    String password = URLDecoder.decode(parans[1], "utf-8");

                    AccountItem item = new AccountItem(name, password);
                    mAdaptor.add(item);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(AccountItem item);
    }

}
