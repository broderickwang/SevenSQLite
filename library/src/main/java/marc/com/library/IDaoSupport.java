package marc.com.library;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by 王成达
 * Date: 2017/9/14
 * Time: 15:57
 * Version: 1.0
 * Description:
 * Email:wangchengda1990@gmail.com
 **/
public interface IDaoSupport<T> {

	void init(SQLiteDatabase database, Class<T> claz);

	long insert(List<T> list);

	long insert(T t);

	long delete(T t);

	long update(T t);

	List<T> query(String selction, String[] selectionArgs );

	int delete(String whereClause,String... whereArgs);

	int update(T t,String whereClause,String... whereArgs);

}
