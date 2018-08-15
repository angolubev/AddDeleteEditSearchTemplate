package agolubeff.adddeleteeditsearchtemplate.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.view.ActionMode;
import android.text.InputType;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agolubeff.adddeleteeditsearchtemplate.Adapter.MyAdapter;
import agolubeff.adddeleteeditsearchtemplate.Globals;
import agolubeff.adddeleteeditsearchtemplate.Listener.MyRecyclerViewClickListener;
import agolubeff.adddeleteeditsearchtemplate.R;

public class MainActivity extends AppCompatActivity
{
    boolean is_multi_select = false;
    boolean is_search_mode = false;

    MyAdapter adapter;
    FloatingActionButton fab;
    MaterialSearchView search_view;
    private String search_string;
    Menu multi_select_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitList();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitRecyclerView();
        InitFab();
        //InitSearchView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        InitSearchView(menu);
        return true;
    }

    private void InitRecyclerView()
    {
        LinearLayoutManager layout_manager = new LinearLayoutManager(this);
        RecyclerView recycler_view = findViewById(R.id.my_recycler_view);
        adapter = new MyAdapter(this);

        recycler_view.setLayoutManager(layout_manager);
        recycler_view.setAdapter(adapter);
        recycler_view.addOnItemTouchListener(new MyRecyclerViewClickListener(this, recycler_view, new MyRecyclerViewClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                if (is_multi_select) MultiSelect(position);
                else Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position)
            {
                if (!is_multi_select)
                {
                    Globals.selected_items = new ArrayList<>();
                    is_multi_select = true;

                    if (action_mode == null)
                    {
                       action_mode = startSupportActionMode(mActionModeCallback);
                    }

                    MultiSelect(position);
                }
            }
        }));
    }

    private void MultiSelect(int position)
    {
        if (action_mode != null)
        {
            List<String> list;

            if(is_search_mode) list = Globals.search_result;
            else list = Globals.list;

            if (Globals.selected_items.contains(list.get(position))) Globals.selected_items.remove(list.get(position));
            else Globals.selected_items.add(list.get(position));

            MenuItem edit = multi_select_menu.findItem(R.id.action_edit);
            if (Globals.selected_items.size() > 1) edit.setVisible(false);
            else edit.setVisible(true);

            if (Globals.selected_items.size() > 0) action_mode.setTitle("" + Globals.selected_items.size());
            else action_mode.finish();

            adapter.notifyDataSetChanged();
        }
    }

    private void InitFab()
    {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ShowDialogAdd();
            }
        });
    }

    private void InitSearchView(Menu menu)
    {
        search_view = findViewById(R.id.search_view);
        search_view.setMenuItem(menu.findItem(R.id.action_search));
        search_view.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                Search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                Search(newText);
                return true;
            }
        });

        search_view.setOnSearchViewListener(new MaterialSearchView.SearchViewListener()
        {
            @Override
            public void onSearchViewShown()
            {
                fab.hide();
                search_view.setQuery(search_string, false);
            }

            @Override
            public void onSearchViewClosed()
            {
                search_string = "";
                adapter.is_search_mode = false;
                is_search_mode = false;
                fab.show();
            }
        });
    }

    private void Search(String s)
    {
        search_string = s;

        // in real app you'd have it instantiated just once
        Globals.search_result = new ArrayList<>();

        // case insensitive search
        for (String i : Globals.list)
        {
            if (i.toLowerCase().contains(s.toLowerCase()))
            {
                Globals.search_result.add(i);
            }
        }

        adapter.is_search_mode = true;
        is_search_mode = true;
        adapter.notifyDataSetChanged();
    }


    ActionMode action_mode;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            multi_select_menu = menu;
            adapter.is_action_mode = true;
            adapter.notifyDataSetChanged();
            fab.hide();
            //context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.action_delete:
                    //alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false);
                    ShowDialogDelete();
                    return true;
                case R.id.action_edit:
                    ShowDialogEdit();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            action_mode = null;
            is_multi_select = false;
            Globals.selected_items = new ArrayList<>();
            adapter.is_action_mode=false;
            adapter.notifyDataSetChanged();

            fab.show();
        }
    };

    private void ShowDialogDelete()
    {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Delete");  // заголовок
        ad.setMessage("Delete item?"); // сообщение

        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int arg1)
            {
                if(Globals.selected_items.size()>0)
                {
                    for(int i=0;i<Globals.selected_items.size();i++)
                    {
                        Globals.list.remove(Globals.selected_items.get(i));
                        if(Globals.search_result.size() > 0) Globals.search_result.remove(Globals.selected_items.get(i));
                    }

                    adapter.notifyDataSetChanged();

                    if (action_mode != null) action_mode.finish();

                    //Toast.makeText(getApplicationContext(), "Delete Click", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ad.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int arg1)
            {
                if (action_mode != null) action_mode.finish();
            }
        });

        ad.setCancelable(false);
        ad.show();
    }

    private void ShowDialogEdit()
    {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Edit");  // заголовок
        ad.setMessage("Set new name for item"); // сообщение

        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setText(Globals.selected_items.get(0));
        ad.setView(input);

        ad.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int arg1)
            {
                Globals.list.remove(Globals.selected_items.get(0));
                if(Globals.search_result.size() > 0) Globals.search_result.remove(Globals.selected_items.get(0));

                Globals.list.add(input.getText().toString());
                adapter.notifyDataSetChanged();

                if (action_mode != null) action_mode.finish();
            }
        });


        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });

        ad.show();
    }

    private void ShowDialogAdd()
    {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Add");  // заголовок
        ad.setMessage("Set name for new item"); // сообщение

        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setText("name");
        ad.setView(input);

        ad.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int arg1)
            {
                Globals.list.add(input.getText().toString());
                adapter.notifyDataSetChanged();

                if (action_mode != null) action_mode.finish();
            }
        });


        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });

        ad.show();
    }

    private void InitList()
    {
        //Globals.list = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        Globals.list = new ArrayList<>();
        Globals.search_result = new ArrayList<>();
        Globals.selected_items = new ArrayList<>();

        Globals.list.add("Afghanistan");
        Globals.list.add("Albania");
        Globals.list.add("Algeria");
        Globals.list.add("Andorra");
        Globals.list.add("Angola");
        Globals.list.add("Anguilla");
        Globals.list.add("Antigua & Barbuda");
        Globals.list.add("Argentina");
        Globals.list.add("Armenia");
        Globals.list.add("Australia");
        Globals.list.add("Austria");
        Globals.list.add("Azerbaijan");
        Globals.list.add("Bahamas");
        Globals.list.add("Bahrain");
        Globals.list.add("Bangladesh");
        Globals.list.add("Barbados");
        Globals.list.add("Belarus");
        Globals.list.add("Belgium");
        Globals.list.add("Belize");
        Globals.list.add("Benin");
        Globals.list.add("Bermuda");
        Globals.list.add("Bhutan");
        Globals.list.add("Bolivia");
        Globals.list.add("Bosnia & Herzegovina");
        Globals.list.add("Botswana");
        Globals.list.add("Brazil");
        Globals.list.add("Brunei Darussalam");
        Globals.list.add("Bulgaria");
        Globals.list.add("Burkina Faso");
        Globals.list.add("Myanmar/Burma");
        Globals.list.add("Burundi");
        Globals.list.add("Cambodia");
        Globals.list.add("Cameroon");
        Globals.list.add("Canada");
        Globals.list.add("Cape Verde");
        Globals.list.add("Cayman Islands");
        Globals.list.add("Central African Republic");
        Globals.list.add("Chad");
        Globals.list.add("Chile");
        Globals.list.add("China");
        Globals.list.add("Colombia");
        Globals.list.add("Comoros");
        Globals.list.add("Congo");
        Globals.list.add("Costa Rica");
        Globals.list.add("Croatia");
        Globals.list.add("Cuba");
        Globals.list.add("Cyprus");
        Globals.list.add("Czech Republic");
        Globals.list.add("Democratic Republic of the Congo");
        Globals.list.add("Denmark");
        Globals.list.add("Djibouti");
        Globals.list.add("Dominican Republic");
        Globals.list.add("Dominica");
        Globals.list.add("Ecuador");
        Globals.list.add("Egypt");
        Globals.list.add("El Salvador");
        Globals.list.add("Equatorial Guinea");
        Globals.list.add("Eritrea");
        Globals.list.add("Estonia");
        Globals.list.add("Ethiopia");
        Globals.list.add("Fiji");
        Globals.list.add("Finland");
        Globals.list.add("France");
        Globals.list.add("French Guiana");
        Globals.list.add("Gabon");
        Globals.list.add("Gambia");
        Globals.list.add("Georgia");
        Globals.list.add("Germany");
        Globals.list.add("Ghana");
        Globals.list.add("Great Britain");
        Globals.list.add("Greece");
        Globals.list.add("Grenada");
        Globals.list.add("Guadeloupe");
        Globals.list.add("Guatemala");
        Globals.list.add("Guinea");
        Globals.list.add("Guinea-Bissau");
        Globals.list.add("Guyana");
        Globals.list.add("Haiti");
        Globals.list.add("Honduras");
        Globals.list.add("Hungary");
    }

}
