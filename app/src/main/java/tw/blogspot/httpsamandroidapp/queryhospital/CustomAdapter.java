package tw.blogspot.httpsamandroidapp.queryhospital;

import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class CustomAdapter extends BaseAdapter{
    private LayoutInflater myInflater;
    private List<Hospital> hospitals;

    public CustomAdapter(Context context, List<Hospital> hospitallist) {
        // TODO Auto-generated constructor stub
        myInflater = LayoutInflater.from(context);
        this.hospitals= hospitallist;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return hospitals.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return hospitals.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return hospitals.indexOf(getItem(position));
    }

    public class Holder
    {
        TextView name;
        TextView addr;
        TextView phone;
        Button copyBtn;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder=new Holder();
        View rowView;
        rowView = myInflater.inflate(R.layout.list_item, null);
        holder.name=(TextView) rowView.findViewById(R.id.hospital_name);
        holder.addr=(TextView) rowView.findViewById(R.id.hospital_addr);
        holder.phone=(TextView) rowView.findViewById(R.id.hospital_phone);
        holder.copyBtn = (Button) rowView.findViewById(R.id.hospital_copy);
        Hospital hospital = (Hospital)getItem(position);
        holder.name.setText(hospital.getName());
        holder.addr.setText(hospital.getAddr());
        holder.phone.setText(hospital.getPhone());
        holder.copyBtn.setTag(hospital.getPhone());
        if (position != 0) {
            holder.name.setPadding(0, 0, 0, 10);
            holder.addr.setPadding(0, 0, 0, 10);
        }
        if (position == 0)
            holder.copyBtn.setVisibility(View.INVISIBLE);
        return rowView;
    }


}