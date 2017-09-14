package marc.com.library;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by 王成达
 * Date: 2017/9/14
 * Time: 16:00
 * Version: 1.0
 * Description:
 * Email:wangchengda1990@gmail.com
 **/
public class DaoSupport<T> implements IDaoSupport<T> {

	private SQLiteDatabase mSQLiteDatabase;
	private Class<T> mClaz;

	private final Map<String,Method> mCVPutMethods = new HashMap<>();


	@Override
	public void init(SQLiteDatabase database, Class<T> claz) {
		this.mClaz = claz;
		this.mSQLiteDatabase = database;

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("create table if not exists "+mClaz.getSimpleName()+"(id integer primary key autoincrement, ");

		Field[] fields = mClaz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String name = field.getName();
			if(name.equalsIgnoreCase("$change")||name.equalsIgnoreCase("serialVersionUID"))
				continue;
			String type = field.getType().getSimpleName();
			String t = "";
			switch (type){
				case "int":
					t = "integer";
					break;
				case "string":
					t = "text";
					break;
				case "char":
					t = "varchar";
					break;
				default:
					t = type;
					break;
			}
			sBuilder.append(name +" "+t+", ");
			sBuilder.replace(-2,sBuilder.length(),")");
			Log.d(TAG, "数据库建表语句："+sBuilder.toString());

			mSQLiteDatabase.execSQL(sBuilder.toString());
		}
	}

	@Override
	public long insert(List<T> list) {
		return 0;
	}

	@Override
	public long insert(T t) {
		String tableName = t.getClass().getSimpleName();

		mSQLiteDatabase.beginTransaction();

		long id = 0;
		id = mSQLiteDatabase.insert(tableName,null,Object2ContentValues(t));

		mSQLiteDatabase.setTransactionSuccessful();
		mSQLiteDatabase.endTransaction();

		return id;
	}

	@Override
	public long delete(T t) {
		return 0;
	}

	@Override
	public long update(T t) {
		return 0;
	}

	@Override
	public List<T> query(String selction, String[] selectionArgs) {
		return null;
	}

	@Override
	public int delete(String whereClause, String... whereArgs) {
		return 0;
	}

	@Override
	public int update(T t, String whereClause, String... whereArgs) {
		return 0;
	}

	private ContentValues Object2ContentValues(T t) {
		ContentValues cv = new ContentValues();

		Field[] fields = t.getClass().getDeclaredFields();

		for (Field field : fields) {

			try {
				field.setAccessible(true);
				String key = field.getName();
				Object value = field.get(key);
				String type = field.getType().getName();

				Log.d(TAG, "Object2ContentValues: "+value.getClass().toString()+":"+field.getType().getName());

				Method putMethod = mCVPutMethods.get(type);
				if(putMethod == null){
					putMethod = ContentValues.class.getDeclaredMethod("put",String.class,value.getClass());
					mCVPutMethods.put(type,putMethod);
				}
				//keyvalue需要保存成数组传入么？
				putMethod.invoke(cv,key,value);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}

		}

		return cv;
	}
}
