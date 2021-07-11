package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteTextAdapter extends ArrayAdapter<ContactsPersons> {

    private List<ContactsPersons> contactListAll;



     AutoCompleteTextAdapter(@NonNull Context context, @NonNull List<ContactsPersons> contactList) {
        super(context, 0, contactList);

        try{
        contactListAll = new ArrayList<>(contactList);
    }catch (Exception e){


        }
     }

    @NonNull
    @Override
    public Filter getFilter() {

        return contactFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.contact_list_rescyclerview, parent, false
            );
        }

        TextView contactName = convertView.findViewById(R.id.contactname);

        TextView contactPhone = convertView.findViewById(R.id.contactphone);
       CheckBox checkBox=convertView.findViewById(R.id.checkBox);


        ContactsPersons contact = getItem(position);

        if (contact != null) {
            contactName.setText(contact.getNameContact());
            contactPhone.setText(contact.getPhoneContact());
            checkBox.setVisibility(View.GONE);
        }

        return convertView;
    }

    private Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ContactsPersons> suggestions = new ArrayList<>();



            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(contactListAll);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ContactsPersons person : contactListAll) {
                    if (person.getNameContact().toLowerCase().contains(filterPattern))
                        suggestions.add(person);



                    else if(person.getPhoneContact().toLowerCase().contains(filterPattern))
                        suggestions.add(person);



                }

                if(suggestions.isEmpty())
                    if (filterPattern.matches("[0-9]+"))
                    suggestions.add(new ContactsPersons(filterPattern,filterPattern));
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();

            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {


            return ((ContactsPersons) resultValue).getPhoneContact();
        }
    };
}
