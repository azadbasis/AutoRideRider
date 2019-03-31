package org.autoride.autoride.TrackRider;


import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.autoride.autoride.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomAdapter extends ArrayAdapter<Contact> implements Filterable {
    private List<Contact> contacts;
    private Context context;
    private Filter contactFilter;
    private List<Contact> origContactList;


    Contact contact;


    public CustomAdapter(Context context, List<Contact> contacts) {
        super(context, R.layout.contact_list, contacts);
        this.context = context;
        this.contacts = contacts;
        this.origContactList = contacts;

    }


    static class ViewHolder {
        TextView nameTv;
        TextView phoneTv;
        CheckBox checkBox;
    }

    /*search item*/
    public int getCount() {
        return contacts.size();
    }

    public Contact getItem(int position) {
        return contacts.get(position);
    }

    public long getItemId(int position) {
        return contacts.get(position).hashCode();
    }

    /*end search item*/

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View v = convertView;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.contact_list, null);

            viewHolder = new ViewHolder();
            TextView tvn = (TextView) v.findViewById(R.id.contactName);
            TextView TvPhn = (TextView) v.findViewById(R.id.contactNumber);
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.listItemCheckBox);

            viewHolder.nameTv = tvn;
            viewHolder.phoneTv = TvPhn;
            viewHolder.checkBox = checkBox;
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    contacts.get(getPosition).selected = buttonView.isChecked();
                }
            });

            viewHolder.checkBox.setTag(position);
            v.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        contact = contacts.get(position);
        viewHolder.nameTv.setText(contact.getContactName());
        viewHolder.phoneTv.setText("" + contact.getContactPhoneNumber());

        return v;
    }


    public void resetData() {
        contacts = origContactList;

    }


    @Override
    public Filter getFilter() {
        if (contactFilter == null)
            contactFilter = new ContactFilter();

         return contactFilter;
    }


    public class ContactFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            String data = constraint.toString().toLowerCase();

            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = origContactList;
                results.count = origContactList.size();
            } else {
                // We perform filtering operation
                List<Contact> nContactList = new ArrayList<Contact>();


                for (Contact c : contacts) {

                    String name = c.getContactName().toLowerCase();
                    String phone = c.getContactPhoneNumber().toLowerCase();

                    if (name.startsWith(data) || PhoneNumberUtils.stripSeparators(phone).startsWith(data))
                        nContactList.add(c);
                }


                    results.values = nContactList;
                    results.count = nContactList.size();


               // Set<Contact> s = new LinkedHashSet<>(nContactList);


            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                contacts = (List<Contact>) results.values;
                notifyDataSetChanged();
            }


        }
    }

}

