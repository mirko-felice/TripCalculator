package com.example.tripcalculator.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Trip.class, Location.class}, version = 7)
@TypeConverters({DateConverter.class, StringConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "trip-database";
    private static AppDatabase instance;
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            String addInsertDateToTrip = "ALTER TABLE `Trip` ADD COLUMN `InsertDate` INTEGER NOT NULL DEFAULT 0";
            database.execSQL(addInsertDateToTrip);
            String addLatitudeToLocation = "ALTER TABLE `Location` ADD COLUMN `Latitude` REAL NOT NULL DEFAULT 0.0";
            database.execSQL(addLatitudeToLocation);
            String addLongitudeToLocation = "ALTER TABLE `Location` ADD COLUMN `Longitude` REAL NOT NULL DEFAULT 0.0";
            database.execSQL(addLongitudeToLocation);
            String addDisplayNameToLocation = "ALTER TABLE `Location` ADD COLUMN `DisplayName` TEXT NOT NULL DEFAULT ''";
            database.execSQL(addDisplayNameToLocation);
        }
    };
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE `Location`");
            database.execSQL("CREATE TABLE IF NOT EXISTS `Location` (`Id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Order` INTEGER NOT NULL, `TripId` INTEGER NOT NULL, `Note` TEXT, `ImgNames` TEXT, `IsPassed` INTEGER NOT NULL, `PreviousId` INTEGER, `Reminder` TEXT, `Latitude` REAL NOT NULL, `Longitude` REAL NOT NULL, `DisplayName` TEXT NOT NULL, FOREIGN KEY(`TripId`) REFERENCES `Trip`(`TripId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Location_TripId_Order` ON `Location` (`TripId`, `Order`)");
        }
    };
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP INDEX `index_Location_TripId_Order`");
        }
    };
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_Location_TripId` ON `Location` (`TripId`)");
        }
    };
    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            String addFullNameToLocation = "ALTER TABLE `Location` ADD COLUMN `FullName` TEXT NOT NULL DEFAULT ''";
            database.execSQL(addFullNameToLocation);
        }
    };
    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            String addIsPassedToTrip = "ALTER TABLE `Trip` ADD COLUMN `IsPassed` INTEGER NOT NULL DEFAULT 0";
            database.execSQL(addIsPassedToTrip);
        }
    };


    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(Context context) {
        if(instance == null){
            synchronized (AppDatabase.class) {
                instance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .addMigrations(MIGRATION_2_3)
                        .addMigrations(MIGRATION_3_4)
                        .addMigrations(MIGRATION_4_5)
                        .addMigrations(MIGRATION_5_6)
                        .addMigrations(MIGRATION_6_7)
                        .build();
            }
        }
        return instance;
    }

    public abstract TripDao tripDao();

    public abstract LocationDao locationDao();
}
