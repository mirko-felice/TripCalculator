package com.example.tripcalculator.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Trip.class, Location.class}, version = 3)
@TypeConverters({DateConverter.class, StringConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "trip-database";
    private static AppDatabase instance;
    private static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            String ADD_INSERT_DATE_TO_TRIP = "ALTER TABLE `Trip` ADD COLUMN `InsertDate` INTEGER NOT NULL DEFAULT 0";
            database.execSQL(ADD_INSERT_DATE_TO_TRIP);
            String ADD_LATITUDE_TO_LOCATION = "ALTER TABLE `Location` ADD COLUMN `Latitude` REAL NOT NULL DEFAULT 0.0";
            database.execSQL(ADD_LATITUDE_TO_LOCATION);
            String ADD_LONGITUDE_TO_LOCATION = "ALTER TABLE `Location` ADD COLUMN `Longitude` REAL NOT NULL DEFAULT 0.0";
            database.execSQL(ADD_LONGITUDE_TO_LOCATION);
            String ADD_DISPLAY_NAME_TO_LOCATION = "ALTER TABLE `Location` ADD COLUMN `DisplayName` TEXT NOT NULL DEFAULT ''";
            database.execSQL(ADD_DISPLAY_NAME_TO_LOCATION);
        }
    };
    private static Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE `Location`");
            database.execSQL("CREATE TABLE IF NOT EXISTS `Location` (`Id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Order` INTEGER NOT NULL, `TripId` INTEGER NOT NULL, `Note` TEXT, `ImgNames` TEXT, `IsPassed` INTEGER NOT NULL, `PreviousId` INTEGER, `Reminder` TEXT, `Latitude` REAL NOT NULL, `Longitude` REAL NOT NULL, `DisplayName` TEXT NOT NULL, FOREIGN KEY(`TripId`) REFERENCES `Trip`(`TripId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Location_TripId_Order` ON `Location` (`TripId`, `Order`)");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .build();
        }
        return instance;
    }

    public abstract TripDao tripDao();

    public abstract LocationDao locationDao();
}
