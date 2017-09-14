package marc.com.library;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
				case "String":
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

		}
		sBuilder.replace(sBuilder.length()-2,sBuilder.length(),")");
		Log.d(TAG, "Database createSQL："+sBuilder.toString());

		mSQLiteDatabase.execSQL(sBuilder.toString());
	}

	@Override
	public long insert(List<T> list) {
		long index = 0;
		for (T t : list) {
			insert(t);
			index++;
		}
		Log.d(TAG, "insert: insert "+index+" rows");
		return index;
	}

	@Override
	public long insert(T t) {
		String tableName = t.getClass().getSimpleName();

		mSQLiteDatabase.beginTransaction();

		long id = 0;
		id = mSQLiteDatabase.insert(tableName,null,Object2ContentValues(t));
		Log.d(TAG, "insert: id="+id);
		mSQLiteDatabase.setTransactionSuccessful();
		mSQLiteDatabase.endTransaction();

		return id;
	}

	@Override
	public int delete(T t) {
		String whereClause;
		StringBuilder whereBuilder = new StringBuilder();
		String[] whereArgs ;
		int index = 0;
		Field[] fields = mClaz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			if(field.getName().equalsIgnoreCase("$change")||field.getName().equalsIgnoreCase("serialVersionUID"))
				continue;
			if(i == fields.length-1){
				whereBuilder.append(field.getName()+"=?");
			}else{
				whereBuilder.append(field.getName()+"=? and ");
			}
			index++;
		}
		whereArgs = new String[index];
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			if(field.getName().equalsIgnoreCase("$change")||field.getName().equalsIgnoreCase("serialVersionUID"))
				continue;
			try {
				Object obj = field.get(t);
				whereArgs[i] = obj.toString();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if(!whereBuilder.toString().endsWith("?")){
			whereClause = whereBuilder.toString().substring(0,whereBuilder.toString().length()-4);
		}else{
			whereClause = whereBuilder.toString();
		}
		int rows = delete(whereClause,whereArgs);
		return rows;
	}

	@Override
	public long update(T t) {
		return 0;
	}

	@Override
	public List<T> query(String selction, String[] selectionArgs) {

		Field[] fids = mClaz.getDeclaredFields();
		Method[] methods = mClaz.getMethods();
		List<T> list = new ArrayList<>();

		Cursor c = mSQLiteDatabase.query(mClaz.getSimpleName(),null,selction,selectionArgs,null,null,null);
		if(c != null){
			if(c.moveToFirst()){
				for (int i=0;i<c.getCount();i++){
					T t = null;
					try {
						t = mClaz.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

					c.moveToPosition(i);
					for (Field fid : fids) {
						fid.setAccessible(true);
						for (Method method : methods) {
							if(method.getName().contains("set")&&method.getName().toLowerCase().contains(fid.getName())){
								String type = fid.getGenericType().toString();
								try {
									switch (type){
										case "int":
											method.invoke(t,c.getInt(c.getColumnIndex(fid.getName())));
											break;
										case "boolean":
											method.invoke(t,c.getBlob(c.getColumnIndex(fid.getName())));
											break;
										case "long":
											method.invoke(t,c.getLong(c.getColumnIndex(fid.getName())));
											break;
										case "double":
											method.invoke(t,c.getDouble(c.getColumnIndex(fid.getName())));
											break;
										case "float":
											method.invoke(t,c.getFloat(c.getColumnIndex(fid.getName())));
											break;
										case "short":
											method.invoke(t,c.getShort(c.getColumnIndex(fid.getName())));
											break;
										default:
											method.invoke(t,c.getString(c.getColumnIndex(fid.getName())));
											break;
									}
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}
					}

					list.add(t);
				}
			}
		}
		return list;
	}

	@Override
	public int delete(String whereClause, String... whereArgs) {
		int rows = mSQLiteDatabase.delete(mClaz.getSimpleName(),whereClause,whereArgs);
		Log.d(TAG, "delete: delete table "+rows+" rows");
		return rows;
	}

	@Override
	public int update(T t, String whereClause, String... whereArgs) {
		int rows = mSQLiteDatabase.update(mClaz.getSimpleName(),Object2ContentValues(t),whereClause,whereArgs);
		Log.d(TAG, "update: update table "+rows +" rows");
		return rows;
	}

	private ContentValues Object2ContentValues(T t) {
		ContentValues cv = new ContentValues();

		Field[] fields = t.getClass().getDeclaredFields();

		for (Field field : fields) {

			try {
				field.setAccessible(true);
				if(field.getName().equalsIgnoreCase("$change")||field.getName().equalsIgnoreCase("serialVersionUID"))
					continue;
				String type = field.getType().getName();
				String key = field.getName();
				Object value = field.get(t);

				Log.d(TAG, "Object2ContentValues: "+value.getClass().toString()+":"+field.getType().getName());

				Method putMethod = mCVPutMethods.get(type);
				if(putMethod == null){
					putMethod = ContentValues.class.getDeclaredMethod("put",String.class,value.getClass());
					mCVPutMethods.put(type,putMethod);
				}
				putMethod.invoke(cv,key,value);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}

		}

		return cv;
	}

}
