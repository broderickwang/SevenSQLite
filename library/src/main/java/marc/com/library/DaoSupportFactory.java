package marc.com.library;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by 王成达
 * Date: 2017/9/14
 * Time: 16:01
 * Version: 1.0
 * Description:
 * Email:wangchengda1990@gmail.com
 **/
public class DaoSupportFactory {
	private static DaoSupportFactory mFactory;
	private SQLiteDatabase mSqlLiteDatabase;
	private String mDBName;
	private String mPathName;

	private DaoSupportFactory(String dbName) {
		if (dbName == null){
			throw new RuntimeException("please set SQLite DataBase name!");
		}
		this.mDBName = dbName;


		if(!mDBName.contains(".db")) {
			mPathName = mDBName;
			mDBName += ".db";
		}else{
			String[] ss = mDBName.split(".");
			mPathName = ss[0];
		}

		File dbRoot = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()+File.separator+mPathName+File.separator+"database");
		if(!dbRoot.exists()){
			dbRoot.mkdirs();
		}


		File dbFile = new File(dbRoot,mDBName);

		Log.i("TAG", "数据库路径: "+dbFile.toString());

		//打开或者创建一个数据库
		mSqlLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile,null);
	}

	public static DaoSupportFactory getFactory(String dbName){
		if(mFactory == null){
			synchronized (DaoSupportFactory.class){
				if(mFactory == null){
					mFactory = new DaoSupportFactory(dbName);
				}
			}
		}
		return mFactory;
	}

	public <T>IDaoSupport<T> getDao(Class<T> claz){
		IDaoSupport<T> daoSupport = new DaoSupport<T>();
		daoSupport.init(mSqlLiteDatabase,claz);
		return daoSupport;
	}
}
