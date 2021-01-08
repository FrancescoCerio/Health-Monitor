package com.example.health_monitor.DB;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Report.class, version = 9)
public abstract class ReportDB extends RoomDatabase {

    private static ReportDB instance;

    public abstract ReportDAO reportDao();

    public static synchronized ReportDB getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ReportDB.class,
                    "reportdb")
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private ReportDAO reportDao;

        private PopulateDbAsyncTask(ReportDB db){
            reportDao = db.reportDao();
        }
        
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE reportdb ADD COLUMN importance FLOAT");
        }
    };
}

