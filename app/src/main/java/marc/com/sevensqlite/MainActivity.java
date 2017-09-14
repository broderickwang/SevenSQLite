package marc.com.sevensqlite;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import marc.com.library.DaoSupportFactory;
import marc.com.library.IDaoSupport;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
	private IDaoSupport support;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		requestPermission();
		support = DaoSupportFactory.getFactory("test").getDao(Person.class);
		Button c = (Button)findViewById(R.id.create);
		c.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				support.insert(new Person("john",13));
			}
		});

		Button q = (Button)findViewById(R.id.query);
		q.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Person> persons = support.query("id>?",new String[]{"0"});
				Toast.makeText(MainActivity.this, ""+persons.size(), Toast.LENGTH_SHORT).show();
			}
		});
		Button d = (Button)findViewById(R.id.delete);
		d.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int a = support.delete(new Person("john",13));
				Toast.makeText(MainActivity.this, ""+a, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void requestPermission( )
	{
		RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
		rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE
				,Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(new Action1<Permission>() {
			@Override
			public void call(Permission permission) {
				if(permission.granted){
					Toast.makeText(MainActivity.this, "get "+permission.name, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
