package com.example.prestamolab.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PrestamoDatabase_Impl extends PrestamoDatabase {
  private volatile EquipoDao _equipoDao;

  private volatile SolicitudDao _solicitudDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `equipos` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `categoria` TEXT NOT NULL, `estado` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `solicitudes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `equipoId` INTEGER NOT NULL, `ambienteDestino` TEXT NOT NULL, `proposito` TEXT NOT NULL, `duracionHoras` INTEGER NOT NULL, `estado` TEXT NOT NULL, `fechaSolicitud` INTEGER NOT NULL, `fechaLimiteDevolucion` INTEGER NOT NULL, `evidenciaUri` TEXT, `evidenciaEstado` TEXT NOT NULL, `latitud` REAL, `longitud` REAL, `sincronizado` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '634bd1b4a65de3eda7e7cba52402064c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `equipos`");
        db.execSQL("DROP TABLE IF EXISTS `solicitudes`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsEquipos = new HashMap<String, TableInfo.Column>(4);
        _columnsEquipos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEquipos.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEquipos.put("categoria", new TableInfo.Column("categoria", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEquipos.put("estado", new TableInfo.Column("estado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEquipos = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEquipos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEquipos = new TableInfo("equipos", _columnsEquipos, _foreignKeysEquipos, _indicesEquipos);
        final TableInfo _existingEquipos = TableInfo.read(db, "equipos");
        if (!_infoEquipos.equals(_existingEquipos)) {
          return new RoomOpenHelper.ValidationResult(false, "equipos(com.example.prestamolab.data.local.EquipoEntity).\n"
                  + " Expected:\n" + _infoEquipos + "\n"
                  + " Found:\n" + _existingEquipos);
        }
        final HashMap<String, TableInfo.Column> _columnsSolicitudes = new HashMap<String, TableInfo.Column>(13);
        _columnsSolicitudes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("equipoId", new TableInfo.Column("equipoId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("ambienteDestino", new TableInfo.Column("ambienteDestino", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("proposito", new TableInfo.Column("proposito", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("duracionHoras", new TableInfo.Column("duracionHoras", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("estado", new TableInfo.Column("estado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("fechaSolicitud", new TableInfo.Column("fechaSolicitud", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("fechaLimiteDevolucion", new TableInfo.Column("fechaLimiteDevolucion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("evidenciaUri", new TableInfo.Column("evidenciaUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("evidenciaEstado", new TableInfo.Column("evidenciaEstado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("latitud", new TableInfo.Column("latitud", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("longitud", new TableInfo.Column("longitud", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSolicitudes.put("sincronizado", new TableInfo.Column("sincronizado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSolicitudes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSolicitudes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSolicitudes = new TableInfo("solicitudes", _columnsSolicitudes, _foreignKeysSolicitudes, _indicesSolicitudes);
        final TableInfo _existingSolicitudes = TableInfo.read(db, "solicitudes");
        if (!_infoSolicitudes.equals(_existingSolicitudes)) {
          return new RoomOpenHelper.ValidationResult(false, "solicitudes(com.example.prestamolab.data.local.SolicitudEntity).\n"
                  + " Expected:\n" + _infoSolicitudes + "\n"
                  + " Found:\n" + _existingSolicitudes);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "634bd1b4a65de3eda7e7cba52402064c", "f1c8ad5b3cb2ca1e2fdb29b07d606a4d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "equipos","solicitudes");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `equipos`");
      _db.execSQL("DELETE FROM `solicitudes`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(EquipoDao.class, EquipoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SolicitudDao.class, SolicitudDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public EquipoDao equipoDao() {
    if (_equipoDao != null) {
      return _equipoDao;
    } else {
      synchronized(this) {
        if(_equipoDao == null) {
          _equipoDao = new EquipoDao_Impl(this);
        }
        return _equipoDao;
      }
    }
  }

  @Override
  public SolicitudDao solicitudDao() {
    if (_solicitudDao != null) {
      return _solicitudDao;
    } else {
      synchronized(this) {
        if(_solicitudDao == null) {
          _solicitudDao = new SolicitudDao_Impl(this);
        }
        return _solicitudDao;
      }
    }
  }
}
