package marc.com.sevensqlite;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.rxpermisson.PermissionAppCompatActivity;

import marc.com.library.DaoSupportFactory;
import rx.Subscriber;

public class MainActivity extends PermissionAppCompatActivity {
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA};
	private static final int REQUEST_EXTERNAL_STORAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkPermission(R.string.base_permission, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
				, Manifest.permission.GET_ACCOUNTS,
				Manifest.permission.READ_PHONE_STATE)
				.subscribe(new Subscriber() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(Object o) {
						if (o!=null){
							Toast.makeText(MainActivity.this,"请求权限成功",Toast.LENGTH_SHORT).show();
						}else {
							Toast.makeText(MainActivity.this,"请求权限成功",Toast.LENGTH_SHORT).show();
						}
					}
				});

		Button b = (Button)findViewById(R.id.create);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DaoSupportFactory
						.getFactory("test")
						.getDao(Person.class)
						.insert(new Person("joho",24));
			}
		});
	}


}
