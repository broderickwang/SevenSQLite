package marc.com.sevensqlite;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import marc.com.library.DaoSupportFactory;
import marc.com.library.IDaoSupport;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
	private IDaoSupport support;
	private RecyclerView recyclerView;
	private List<Person> persons;
	private MAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recyclerView = (RecyclerView) findViewById(R.id.list);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		adapter = new MAdapter();
		recyclerView.setAdapter(adapter);
		requestPermission();
		support = DaoSupportFactory.getFactory("test").getDao(Person.class);
		Button c = (Button)findViewById(R.id.create);
		c.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				support.insert(new Person("john",13));
				queryAll();
				adapter.notifyDataSetChanged();
			}
		});

		Button q = (Button)findViewById(R.id.query);
		q.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				persons = support.query("id>?",new String[]{"0"});
				adapter.notifyDataSetChanged();
				for (Person person : persons) {
					Toast.makeText(MainActivity.this, ""+persons.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		Button d = (Button)findViewById(R.id.delete);
		d.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int a = support.delete(new Person("john",13));
				Toast.makeText(MainActivity.this, ""+a, Toast.LENGTH_SHORT).show();
				queryAll();
				adapter.notifyDataSetChanged();
			}
		});
		Button u = (Button)findViewById(R.id.update);
		u.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int a = support.update(new Person("johddn",23),"id>?",new String[]{"0"});
				Toast.makeText(MainActivity.this, ""+a, Toast.LENGTH_SHORT).show();
				queryAll();
				adapter.notifyDataSetChanged();
			}
		});
	}

	public void queryAll(){
		persons = support.query("id>?",new String[]{"0"});
		/*for (Person person : persons) {
			Toast.makeText(MainActivity.this, ""+persons.toString(), Toast.LENGTH_SHORT).show();
		}*/
	}

	public void requestPermission( )
	{
		RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
		rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE
				,Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(new Action1<Permission>() {
			@Override
			public void call(Permission permission) {
				if(permission.granted){
//					Toast.makeText(MainActivity.this, "get "+permission.name, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private class MAdapter extends RecyclerView.Adapter<MAdapter.MViewHolder>{
		@Override
		public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item,parent);
			MViewHolder holder = new MViewHolder(v);
			return holder;
		}

		@Override
		public void onBindViewHolder(MViewHolder holder, int position) {
			holder.name.setText(persons.get(position).getName());
			holder.age.setText(String.valueOf(persons.get(position).getAge()));
		}

		@Override
		public int getItemCount() {
			return persons==null?0:persons.size();
		}

		class MViewHolder extends RecyclerView.ViewHolder{

			public TextView name;
			public TextView age;

			public MViewHolder(View itemView) {
				super(itemView);
			}
		}
	}
}
