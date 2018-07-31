package agolubeff.adddeleteeditsearchtemplate.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import agolubeff.adddeleteeditsearchtemplate.Globals;
import agolubeff.adddeleteeditsearchtemplate.R;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>
{
    private Context context;
    public boolean is_action_mode = false;
    public boolean is_search_mode = false;

    public MyAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public int getItemCount()
    {
        if(is_search_mode) return Globals.search_result.size();
        else return Globals.list.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        List<String> list;

        if(is_search_mode) list = Globals.search_result;
        else list = Globals.list;

        holder.text.setText(list.get(position));

        if(is_action_mode)
        {
            holder.check_box.setVisibility(View.VISIBLE);
            if(Globals.selected_items.contains(list.get(position)))
                holder.check_box.setChecked(true);
            else holder.check_box.setChecked(false);
        }
        else holder.check_box.setVisibility(View.GONE);

    }
}

class MyViewHolder extends RecyclerView.ViewHolder
{
    CheckBox check_box;
    TextView text;

    MyViewHolder(View item)
    {
        super(item);

        //text = item.findViewById(R.id.name);
        text = item.findViewById(R.id.text);
        check_box = item.findViewById(R.id.check_box);
    }
}
